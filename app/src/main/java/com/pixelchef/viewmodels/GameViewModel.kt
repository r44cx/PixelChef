package com.pixelchef.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelchef.models.Ingredient
import com.pixelchef.models.Level
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class GameViewModel : ViewModel() {
    private val _currentLevel = MutableStateFlow<Level?>(null)
    val currentLevel: StateFlow<Level?> = _currentLevel

    private val _selectedIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val selectedIngredients: StateFlow<List<Ingredient>> = _selectedIngredients

    private val _completedLevels = MutableStateFlow<Set<Int>>(setOf())
    val completedLevels: StateFlow<Set<Int>> = _completedLevels

    private val _isLevelComplete = MutableStateFlow(false)
    val isLevelComplete: StateFlow<Boolean> = _isLevelComplete

    private var allLevels: List<Level> = emptyList()

    fun initialize(context: Context) {
        try {
            println("Attempting to load levels.json from assets...")
            
            // List all files in assets to verify levels.json exists
            context.assets.list("")?.forEach { fileName ->
                println("Found asset file: $fileName")
            }

            context.assets.open("levels.json").use { inputStream ->
                println("Successfully opened levels.json")
                val reader = InputStreamReader(inputStream)
                val jsonContent = reader.readText()
                println("JSON Content: $jsonContent")
                
                allLevels = Gson().fromJson(jsonContent, Array<Level>::class.java).toList()
                println("Successfully parsed JSON into ${allLevels.size} levels")
                
                // Print details of each level
                allLevels.forEach { level ->
                    println("""
                        Level ${level.id}:
                        Name: ${level.name}
                        Available Ingredients: ${level.availableIngredients.map { it.name }}
                        Required Ingredients: ${level.ingredients.map { it.name }}
                    """.trimIndent())
                }
                
                // Initialize with first level unlocked
                _completedLevels.value = setOf(0)
            }
        } catch (e: Exception) {
            println("Error loading levels: ${e.message}")
            e.printStackTrace()
            
            // Print the stack trace with more detail
            e.stackTraceToString().split("\n").forEach { line ->
                println(line)
            }
        }
    }

    fun isLevelUnlocked(levelId: Int): Boolean {
        // First level is always unlocked
        if (levelId == 1) return true
        // Other levels are unlocked if the previous level is completed
        return _completedLevels.value.contains(levelId - 1)
    }

    fun getAllLevels(): List<Level> = allLevels

    private fun unlockLevel(levelId: Int) {
        val current = _completedLevels.value.toMutableSet()
        current.add(levelId)
        _completedLevels.value = current
    }

    fun loadLevel(levelId: Int) {
        viewModelScope.launch {
            val level = allLevels.find { it.id == levelId }?.copy()
            
            if (level != null) {
                println("Loading level ${level.id}")
                println("Available ingredients: ${level.availableIngredients}")
                println("Required ingredients: ${level.ingredients}")
            } else {
                println("Level $levelId not found!")
            }
            
            _currentLevel.value = level
            _selectedIngredients.value = emptyList()
            _isLevelComplete.value = false
        }
    }

    fun selectIngredient(ingredient: Ingredient): Boolean {
        val currentSelected = _selectedIngredients.value.toMutableList()
        val currentLevelValue = _currentLevel.value ?: return false

        // Don't allow selecting if level is complete
        if (_isLevelComplete.value) return false

        // Don't allow selecting more ingredients than needed
        if (currentSelected.size >= currentLevelValue.ingredients.size) {
            return false
        }

        // Get remaining required ingredients (those that haven't been selected yet)
        val remainingRequired = currentLevelValue.ingredients.toMutableList()
        currentSelected.forEach { selected ->
            remainingRequired.removeIf { it.name == selected.name }
        }

        // Check if the selected ingredient is one of the remaining required ingredients
        val isCorrect = remainingRequired.any { it.name == ingredient.name }

        if (isCorrect) {
            currentSelected.add(ingredient)
            _selectedIngredients.value = currentSelected
            
            // Check if level is complete after adding ingredient
            if (currentSelected.size == currentLevelValue.ingredients.size) {
                _isLevelComplete.value = true
                unlockLevel(currentLevelValue.id)
            }
            return true
        }

        return false
    }

    fun getSelectedIngredientsCount(): Int = _selectedIngredients.value.size
    fun getTotalRequiredIngredients(): Int = _currentLevel.value?.ingredients?.size ?: 0

    fun getLevelDebugString(level: Level): String {
        return """
            Level ID: ${level.id}
            Name: ${level.name}
            
            Available Ingredients (${level.availableIngredients.size}):
            ${level.availableIngredients.joinToString("\n") { "- ${it.name} (${it.imageResource})" }}
            
            Required Ingredients (${level.ingredients.size}):
            ${level.ingredients.joinToString("\n") { "- ${it.name} (${it.imageResource})" }}
            
            Recipe:
            Description: ${level.recipe.description}
            Prep Time: ${level.recipe.preparationTime}
            Difficulty: ${level.recipe.difficulty}
            
            Instructions:
            ${level.recipe.instructions.joinToString("\n") { "- $it" }}
            
            Is Unlocked: ${level.isUnlocked}
        """.trimIndent()
    }
} 