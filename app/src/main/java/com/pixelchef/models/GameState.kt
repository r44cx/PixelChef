package com.pixelchef.models

data class GameState(
    val levelId: Int,
    val rating: Int = 0,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false
) 