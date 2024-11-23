package com.pixelchef.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pixelchef.models.GameState
import java.io.File

class GameProgressManager(private val context: Context) {
    private val gson = Gson()
    private val gameStateFile = File(context.filesDir, "game_state.json")
    private var gameStates: MutableMap<Int, GameState> = mutableMapOf()

    init {
        loadGameStates()
    }

    private fun loadGameStates() {
        try {
            if (gameStateFile.exists()) {
                val json = gameStateFile.readText()
                val type = object : TypeToken<MutableMap<Int, GameState>>() {}.type
                gameStates = gson.fromJson(json, type) ?: mutableMapOf()
            }
            
            // Always ensure first level is unlocked
            if (!gameStates.containsKey(1)) {
                gameStates[1] = GameState(
                    levelId = 1,
                    isUnlocked = true,
                    rating = 3
                )
                saveGameStates()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Initialize with first level if loading fails
            gameStates = mutableMapOf(
                1 to GameState(levelId = 1, isUnlocked = true, rating = 3)
            )
            saveGameStates()
        }
    }

    private fun saveGameStates() {
        try {
            // Ensure directory exists
            gameStateFile.parentFile?.mkdirs()
            
            // Write the JSON with pretty printing
            val json = gson.toJson(gameStates)
            gameStateFile.writeText(json)
            
            // Debug output
            println("Saved game states to: ${gameStateFile.absolutePath}")
            println("Content: $json")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to save game states: ${e.message}")
        }
    }

    fun getGameState(levelId: Int): GameState {
        return gameStates[levelId] ?: GameState(levelId = levelId)
    }

    fun updateGameState(levelId: Int, update: (GameState) -> GameState) {
        val currentState = getGameState(levelId)
        gameStates[levelId] = update(currentState)
        saveGameStates()
    }

    fun isLevelUnlocked(levelId: Int): Boolean {
        return when {
            levelId == 1 -> true  // First level always unlocked
            gameStates.containsKey(levelId) -> gameStates[levelId]?.isUnlocked == true
            else -> false
        }
    }

    fun getAllGameStates(): List<GameState> {
        return gameStates.values.toList()
    }

    fun completeLevel(levelId: Int, rating: Int) {
        println("Completing level $levelId with rating $rating") // Debug log
        
        // Mark current level as completed
        updateGameState(levelId) { state ->
            state.copy(
                isCompleted = true,
                isUnlocked = true,
                rating = rating
            )
        }
        
        // Unlock next level
        val nextLevelId = levelId + 1
        updateGameState(nextLevelId) { state ->
            state.copy(
                isUnlocked = true,
                rating = 3  // Initialize with 3 stars
            )
        }
        
        // Debug output
    }

    fun clearProgress() {
        gameStates.clear()
        gameStates[1] = GameState(levelId = 1, isUnlocked = true, rating = 3)
        saveGameStates()
    }
} 