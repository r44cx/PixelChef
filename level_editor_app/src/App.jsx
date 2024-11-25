import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [levels, setLevels] = useState([]);
  const [newLevel, setNewLevel] = useState({
    name: '',
    ingredients: [],
    image: '',
    recipe: {
      description: '',
      preparationTime: '',
      difficulty: '',
      instructions: []
    }
  });

  const [newIngredient, setNewIngredient] = useState({
    name: '',
    imageResource: '',
    correct: true
  });

  const [newInstruction, setNewInstruction] = useState('');
  const [instructions, setInstructions] = useState([]);

  useEffect(() => {
    fetchLevels();
  }, []);

  const fetchLevels = async () => {
    const response = await fetch('http://localhost:3001/api/levels');
    const data = await response.json();
    setLevels(data);
  };

  const generateImage = async (type, name, ingredients = []) => {
    let prompt;
    if (type === 'meals') {
      prompt = `A highly detailed pixel art illustration of a fully prepared ${name}, designed for a cooking-themed game. The artwork should feature a retro gaming style with vibrant, appealing colors and intricate details that make the dish look delicious and inviting. The ${name} should be presented in an artistic and visually engaging manner, showcasing its key ingredients (${ingredients.join(', ')}). Use a clean, solid white background to emphasize the dish and ensure it can be easily integrated into the game's interface. The overall aesthetic should feel playful, immersive, and fitting for a food-centric pixel art game.`;
    } else {
      prompt = `A highly detailed pixel art depiction of ${name}, suitable as an ingredient in a cooking-themed game. The artwork should have a retro gaming style with vibrant and appealing colors. The ${name} should be stylized but easily recognizable, arranged in a visually engaging manner. Use a simple, solid white background to make it easy for removal or integration into the game's interface. Ensure the design feels playful and inviting, matching the aesthetic of a food-centric pixel art game.`;
    }

    const response = await fetch('http://localhost:3001/api/generate-image', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ prompt, type, name }),
    });

    const data = await response.json();
    return data.imageUrl;
  };

  const handleAddIngredient = async () => {
    if (!newIngredient.name) return;

    const imageResource = `ingredients_${newIngredient.name.toLowerCase().replace(/\s+/g, '_')}`;
    const ingredient = {
      ...newIngredient,
      imageResource
    };
    
    setNewLevel(prev => ({
      ...prev,
      ingredients: [...prev.ingredients, ingredient]
    }));
    
    setNewIngredient({
      name: '',
      imageResource: '',
      correct: true
    });
  };

  const handleAddInstruction = () => {
    if (!newInstruction) return;
    setInstructions([...instructions, newInstruction]);
    setNewInstruction('');
  };

  const handleSaveLevel = async () => {
    // Generate meal image
    const mealImageUrl = await generateImage(
        'meals', 
        newLevel.name, 
        newLevel.ingredients.filter(i => i.correct).map(i => i.name)
    );

    // Generate ingredient images for any new ingredients
    for (const ingredient of newLevel.ingredients) {
        await generateImage('ingredients', ingredient.name);
    }

    const imageResource = `meals_${newLevel.name.toLowerCase().replace(/\s+/g, '_')}`;

    const levelToSave = {
        ...newLevel,
        image: imageResource,
        recipe: {
            ...newLevel.recipe,
            instructions: instructions
        }
    };

    await fetch('http://localhost:3001/api/levels', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(levelToSave),
    });

    fetchLevels();
    setNewLevel({
        name: '',
        ingredients: [],
        image: '',
        recipe: {
            description: '',
            preparationTime: '',
            difficulty: '',
            instructions: []
        }
    });
    setInstructions([]);
  };

  return (
    <div className="App">
      <h1>Level Editor</h1>
      
      <div className="level-form">
        <h2>Add New Level</h2>
        <input
          type="text"
          placeholder="Level Name"
          value={newLevel.name}
          onChange={e => setNewLevel(prev => ({ ...prev, name: e.target.value }))}
        />
        
        <h3>Add Ingredients</h3>
        <div className="ingredient-form">
          <input
            type="text"
            placeholder="Ingredient Name"
            value={newIngredient.name}
            onChange={e => setNewIngredient(prev => ({ ...prev, name: e.target.value }))}
          />
          <label>
            <input
              type="checkbox"
              checked={newIngredient.correct}
              onChange={e => setNewIngredient(prev => ({ ...prev, correct: e.target.checked }))}
            />
            Correct Ingredient
          </label>
          <button onClick={handleAddIngredient}>Add Ingredient</button>
        </div>

        <div className="ingredients-list">
          <h4>Added Ingredients:</h4>
          {newLevel.ingredients.map((ing, idx) => (
            <div key={idx}>
              <span>{ing.name}</span>
              <span>{ing.correct ? '✅' : '❌'}</span>
            </div>
          ))}
        </div>

        <h3>Recipe Details</h3>
        <input
          type="text"
          placeholder="Description"
          value={newLevel.recipe.description}
          onChange={e => setNewLevel(prev => ({
            ...prev,
            recipe: { ...prev.recipe, description: e.target.value }
          }))}
        />
        <input
          type="text"
          placeholder="Preparation Time"
          value={newLevel.recipe.preparationTime}
          onChange={e => setNewLevel(prev => ({
            ...prev,
            recipe: { ...prev.recipe, preparationTime: e.target.value }
          }))}
        />
        <input
          type="text"
          placeholder="Difficulty"
          value={newLevel.recipe.difficulty}
          onChange={e => setNewLevel(prev => ({
            ...prev,
            recipe: { ...prev.recipe, difficulty: e.target.value }
          }))}
        />

        <h3>Instructions</h3>
        <div className="instruction-form">
          <input
            type="text"
            placeholder="Add instruction"
            value={newInstruction}
            onChange={e => setNewInstruction(e.target.value)}
          />
          <button onClick={handleAddInstruction}>Add Instruction</button>
        </div>

        <div className="instructions-list">
          <h4>Added Instructions:</h4>
          {instructions.map((instruction, idx) => (
            <div key={idx}>{instruction}</div>
          ))}
        </div>
        
        <button onClick={handleSaveLevel}>Save Level</button>
      </div>

      <div className="levels-list">
        <h2>Existing Levels</h2>
        {levels.map(level => (
          <div key={level.id} className="level-card">
            <h3>{level.name}</h3>
            <img src={`/images/${level.image}.png`} alt={level.name} style={{width: '100px'}} />
            <p>{level.recipe.description}</p>
            <div className="ingredients-list">
              <h4>Ingredients:</h4>
              {level.ingredients.map((ing, idx) => (
                <div key={idx}>
                  <span>{ing.name}</span>
                  <img 
                    src={`/images/${ing.imageResource}.png`} 
                    alt={ing.name} 
                    style={{width: '50px'}} 
                  />
                  <span>{ing.correct ? '✅' : '❌'}</span>
                </div>
              ))}
            </div>
            <div className="instructions-list">
              <h4>Instructions:</h4>
              {level.recipe.instructions.map((instruction, idx) => (
                <div key={idx}>{instruction}</div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App; 