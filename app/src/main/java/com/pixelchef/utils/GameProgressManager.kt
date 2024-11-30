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
        if (gameStateFile.exists()) {
            try {
                val json = gameStateFile.readText()
                val type = object : TypeToken<MutableMap<Int, GameState>>() {}.type
                gameStates = gson.fromJson(json, type) ?: mutableMapOf()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        ensureFirstLevelUnlocked()
    }

    private fun ensureFirstLevelUnlocked() {
        if (!gameStates.containsKey(1)) {
            gameStates[1] = GameState(levelId = 1, isUnlocked = true, rating = 3)
            saveGameStates()
        }
    }

    private fun saveGameStates() {
        try {
            gameStateFile.parentFile?.mkdirs()
            gameStateFile.writeText(gson.toJson(gameStates))
            debugLog("Game states saved", gson.toJson(gameStates))
        } catch (e: Exception) {
            e.printStackTrace()
            debugLog("Failed to save game states", e.message ?: "Unknown error")
        }
    }

    fun getGameState(levelId: Int): GameState {
        return gameStates[levelId] ?: GameState(levelId = levelId)
    }

    fun updateGameState(levelId: Int, update: (GameState) -> GameState) {
        gameStates[levelId] = update(getGameState(levelId))
        saveGameStates()
    }

    fun isLevelUnlocked(levelId: Int): Boolean {
        return levelId == 1 || gameStates[levelId]?.isUnlocked == true
    }

    fun completeLevel(levelId: Int, rating: Int) {
        debugLog("Completing level", "Level $levelId with rating $rating")
        markLevelAsCompleted(levelId, rating)
        unlockNextLevel(levelId)
        debugLog("Game states after level completion", gameStates.toString())
    }

    private fun markLevelAsCompleted(levelId: Int, rating: Int) {
        updateGameState(levelId) { it.copy(isCompleted = true, isUnlocked = true, rating = rating) }
    }

    private fun unlockNextLevel(levelId: Int) {
        val nextLevelId = levelId + 1
        updateGameState(nextLevelId) { it.copy(isUnlocked = true, rating = 0) }
    }

    fun clearProgress() {
        gameStates.clear()
        ensureFirstLevelUnlocked()
    }

    private fun debugLog(tag: String, message: String) {
        println("[$tag]: $message")
    }
}
