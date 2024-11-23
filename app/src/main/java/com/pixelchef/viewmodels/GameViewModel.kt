package com.pixelchef.viewmodels

import GameConstants
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelchef.R
import com.pixelchef.models.Ingredient
import com.pixelchef.models.Level
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import com.pixelchef.utils.GameProgressManager
import com.pixelchef.models.GameState




class GameViewModel : ViewModel() {
    private val _currentLevel = MutableStateFlow<Level?>(null)
    val currentLevel: StateFlow<Level?> = _currentLevel

    private val _currentRating = MutableStateFlow(GameConstants.MAX_RATING)
    var currentRating: StateFlow<Int> = _currentRating

    private val _correctlySelectedIngredients = MutableStateFlow<List<Ingredient>>(emptyList())

    private val _wronglySelectedIngredients = MutableStateFlow<List<Ingredient>>(emptyList())

    private val _completedLevels = MutableStateFlow<Set<Int>>(setOf())
    val completedLevels: StateFlow<Set<Int>> = _completedLevels

    private val _isLevelComplete = MutableStateFlow(false)
    val isLevelComplete: StateFlow<Boolean> = _isLevelComplete


    private val _isLevelFailed = MutableStateFlow(false)
    val isLevelFailed: StateFlow<Boolean> = _isLevelFailed

    private var allLevels: List<Level> = emptyList()

    private lateinit var gameProgressManager: GameProgressManager

    fun initialize(context: Context) {
        gameProgressManager = GameProgressManager(context)
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
                    println(
                        """
                        Level ${level.id}:
                        Name: ${level.name}
                        Ingredients: ${level.ingredients.map { it.name }}
                    """.trimIndent()
                    )
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
        return gameProgressManager.isLevelUnlocked(levelId)
    }

    @SuppressLint("DiscouragedApi")
    fun getDrawableId(context: Context, drawableName: String): Int {
        return context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            .takeIf { it != 0 } ?: R.drawable.ingredient_placeholder
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
                level.ingredients = level.ingredients.shuffled()
                if (!gameProgressManager.getGameState(levelId).isCompleted) {
                    gameProgressManager.updateGameState(levelId) { state ->
                        state.copy(rating = 0)
                    }
                }
                
            }
            _currentRating.value = GameConstants.MAX_RATING
            _currentLevel.value = level
            _correctlySelectedIngredients.value = emptyList()
            _wronglySelectedIngredients.value = emptyList()
            _isLevelComplete.value = false
        }
    }

    fun selectIngredient(ingredient: Ingredient): Boolean {
        val currentLevelValue = _currentLevel.value ?: return false
        val currentGameState = gameProgressManager.getGameState(currentLevelValue.id)

        // Don't allow selecting if level is complete or failed
        if (_isLevelComplete.value || _isLevelFailed.value) return false

        // Get remaining required ingredients (those that haven't been selected yet)
        val remainingRequired = currentLevelValue.ingredients.toMutableList()
        _correctlySelectedIngredients.value.forEach { selected ->
            remainingRequired.removeIf { it.correct && it.name == selected.name }
        }

        // Check if the selected ingredient is one of the remaining required ingredients
        val isCorrect = remainingRequired.any { it.correct && it.name == ingredient.name }

        if (isCorrect) {
            val newCorrectIngredients = _correctlySelectedIngredients.value + ingredient
            _correctlySelectedIngredients.value = newCorrectIngredients

            // Check if level is complete after adding ingredient
            if (newCorrectIngredients.size == getTotalRequiredIngredients()) {
                _isLevelComplete.value = true
                
                // Complete the level and unlock next level
                gameProgressManager.completeLevel(
                    levelId = currentLevelValue.id,
                    rating = currentRating.value.coerceAtLeast(currentGameState.rating)
                )
                
                // Debug output
                println("Level ${currentLevelValue.id} completed!")
                println("Current rating: ${currentGameState.rating}")
            }
            return true
        } else {
            // Wrong ingredient selected
            _currentRating.value = (_currentRating.value - 1).coerceAtLeast(0)

            _wronglySelectedIngredients.value += ingredient

            // Check if level is complete after adding ingredient
            if (_wronglySelectedIngredients.value.size == GameConstants.MAX_WRONG_INGREDIENTS) {
                _isLevelFailed.value = true
            }

            return false
        }
    }

    fun isIngredientSelected(ingredientName: String): Boolean {
        return _correctlySelectedIngredients.value.any { it.name == ingredientName } || _wronglySelectedIngredients.value.any { it.name == ingredientName }
    }

    fun getCorrectSelectedIngredientsCount(): Int = _correctlySelectedIngredients.value.size
    fun getWrongSelectedIngredientsCount(): Int = _wronglySelectedIngredients.value.size
    fun getTotalRequiredIngredients(): Int = _currentLevel.value?.ingredients?.filter {  it.correct }?.size ?: 0

    fun getGameState(levelId: Int): GameState {
        return gameProgressManager.getGameState(levelId)
    }

    fun resetProgress() {
        gameProgressManager.clearProgress()
        // Optionally reload the current level if in a level
        _currentLevel.value?.let { level ->
            loadLevel(level.id)
        }
    }

    fun getNextUncompletedLevel(): Int {
        return allLevels.find { level -> 
            !gameProgressManager.getGameState(level.id).isCompleted 
        }?.id ?: 1
    }

} 