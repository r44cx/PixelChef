const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const fs = require('fs/promises');
const path = require('path');
const axios = require('axios');
require('dotenv').config();

const app = express();
const PORT = 3001;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public'));

// Serve images from Android drawable folder
app.use('/images', express.static(path.join(__dirname, '../app/src/main/res/drawable')));

// Get all levels
app.get('/api/levels', async (req, res) => {
    try {
        const levelsData = await fs.readFile(path.join(__dirname, '../app/src/main/assets/levels.json'), 'utf8');
        res.json(JSON.parse(levelsData));
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Save level
app.post('/api/levels', async (req, res) => {
    try {
        const levelsData = await fs.readFile(path.join(__dirname, '../app/src/main/assets/levels.json'), 'utf8');
        const levels = JSON.parse(levelsData);
        const newLevel = req.body;
        
        // Generate new ID
        newLevel.id = Math.max(...levels.map(l => l.id), 0) + 1;
        
        levels.push(newLevel);
        await fs.writeFile(
            path.join(__dirname, '../app/src/main/assets/levels.json'), 
            JSON.stringify(levels, null, 2)
        );
        
        res.json(newLevel);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Generate image using DALL-E
app.post('/api/generate-image', async (req, res) => {
    try {
        const { prompt, type, name } = req.body;
        
        // Check if image already exists in Android drawable folder
        const imagePath = path.join(
            __dirname, 
            '../app/src/main/res/drawable',
            `${type}_${name.toLowerCase().replace(/\s+/g, '_')}.png`
        );
        
        try {
            await fs.access(imagePath);
            // Image exists, return existing path
            return res.json({ 
                imageUrl: `/images/${type}_${name.toLowerCase().replace(/\s+/g, '_')}.png`,
                reused: true 
            });
        } catch {
            // Image doesn't exist, generate new one
            const response = await axios.post('https://api.openai.com/v1/images/generations', {
                model: "dall-e-3",
                prompt: prompt,
                n: 1,
                size: "1024x1024"
            }, {
                headers: {
                    'Authorization': `Bearer ${process.env.OPENAI_API_KEY}`
                }
            });

            // Download and save image to Android drawable folder
            const imageUrl = response.data.data[0].url;
            const imageResponse = await axios.get(imageUrl, { responseType: 'arraybuffer' });
            await fs.writeFile(imagePath, imageResponse.data);

            res.json({ 
                imageUrl: `/images/${type}_${name.toLowerCase().replace(/\s+/g, '_')}.png`,
                reused: false 
            });
        }
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Ensure required directories exist
async function ensureDirectories() {
    const dirs = [
        path.join(__dirname, '../app/src/main/assets'),
        path.join(__dirname, '../app/src/main/res/drawable')
    ];

    for (const dir of dirs) {
        try {
            await fs.access(dir);
        } catch {
            await fs.mkdir(dir, { recursive: true });
            console.log(`Created directory: ${dir}`);
        }
    }
}

// Initialize server
async function init() {
    await ensureDirectories();
    
    // Create levels.json if it doesn't exist
    const levelsPath = path.join(__dirname, '../app/src/main/assets/levels.json');
    try {
        await fs.access(levelsPath);
    } catch {
        await fs.writeFile(levelsPath, '[]');
        console.log('Created empty levels.json file');
    }

    app.listen(PORT, () => {
        console.log(`Server running on port ${PORT}`);
    });
}

init().catch(console.error); 

// Add this after other middleware
app.use(express.static(path.join(__dirname, 'dist')));

// Add this route at the end, before app.listen
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'index.html'));
}); 