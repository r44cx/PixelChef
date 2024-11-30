package com.pixelchef.viewmodels

import GameConstants
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pixelchef.R
import com.pixelchef.models.GameState
import com.pixelchef.models.Ingredient
import com.pixelchef.models.Level
import com.pixelchef.utils.GameProgressManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class GameViewModel : ViewModel() {

    private val _currentLevel = MutableStateFlow<Level?>(null)
    val currentLevel: StateFlow<Level?> = _currentLevel

    private val _currentRating = MutableStateFlow(GameConstants.MAX_RATING)
    val currentRating: StateFlow<Int> = _currentRating

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
        loadLevelsFromAssets(context)
        _completedLevels.value = setOf(0) // Initialize with the first level unlocked
    }

    private fun loadLevelsFromAssets(context: Context) {
        try {
            context.assets.open("levels.json").use { inputStream ->
                val jsonContent = InputStreamReader(inputStream).readText()
                allLevels = Gson().fromJson(jsonContent, Array<Level>::class.java).toList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun loadLevel(levelId: Int) {
        viewModelScope.launch {
            val level = allLevels.find { it.id == levelId }?.copy()?.apply {
                ingredients = ingredients.shuffled()
            }
            level?.let { prepareLevel(it, levelId) }
        }
    }

    private fun prepareLevel(level: Level, levelId: Int) {
        if (!gameProgressManager.getGameState(levelId).isCompleted) {
            gameProgressManager.updateGameState(levelId) { it.copy(rating = 0) }
        }

        _currentRating.value = GameConstants.MAX_RATING
        _currentLevel.value = level
        _correctlySelectedIngredients.value = emptyList()
        _wronglySelectedIngredients.value = emptyList()
        _isLevelComplete.value = false
        _isLevelFailed.value = false
    }

    fun selectIngredient(ingredient: Ingredient): Boolean {
        val currentLevelValue = _currentLevel.value ?: return false

        if (_isLevelComplete.value || _isLevelFailed.value) return false

        val remainingRequired = currentLevelValue.ingredients.filter { it.correct }
            .filterNot { selected -> _correctlySelectedIngredients.value.contains(selected) }

        return if (remainingRequired.any { it.name == ingredient.name }) {
            handleCorrectSelection(ingredient)
            true
        } else {
            handleIncorrectSelection(ingredient)
            false
        }
    }

    private fun handleCorrectSelection(ingredient: Ingredient) {
        _correctlySelectedIngredients.value += ingredient

        if (_correctlySelectedIngredients.value.size == getTotalRequiredIngredients()) {
            _isLevelComplete.value = true
            currentLevel.value?.let {
                gameProgressManager.completeLevel(it.id, _currentRating.value)
                unlockLevel(it.id + 1)
            }
        }
    }

    private fun handleIncorrectSelection(ingredient: Ingredient) {
        _currentRating.value = (_currentRating.value - 1).coerceAtLeast(0)
        _wronglySelectedIngredients.value += ingredient

        if (_wronglySelectedIngredients.value.size == GameConstants.MAX_WRONG_INGREDIENTS) {
            _isLevelFailed.value = true
        }
    }

    private fun unlockLevel(levelId: Int) {
        val updatedLevels = _completedLevels.value.toMutableSet().apply { add(levelId) }
        _completedLevels.value = updatedLevels
    }

    fun isIngredientSelected(ingredientName: String): Boolean {
        return _correctlySelectedIngredients.value.any { it.name == ingredientName } ||
                _wronglySelectedIngredients.value.any { it.name == ingredientName }
    }

    fun getCorrectSelectedIngredientsCount(): Int = _correctlySelectedIngredients.value.size
    fun getWrongSelectedIngredientsCount(): Int = _wronglySelectedIngredients.value.size
    fun getTotalRequiredIngredients(): Int =
        _currentLevel.value?.ingredients?.count { it.correct } ?: 0

    fun getGameState(levelId: Int): GameState {
        return gameProgressManager.getGameState(levelId)
    }

    fun resetProgress() {
        gameProgressManager.clearProgress()
        _currentLevel.value?.id?.let { loadLevel(it) }
    }

    fun getNextUncompletedLevel(): Int {
        return allLevels.find { !gameProgressManager.getGameState(it.id).isCompleted }?.id ?: 1
    }
}
