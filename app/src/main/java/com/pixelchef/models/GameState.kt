package com.pixelchef.models

data class GameState(
    val levelId: Int,
    val rating: Int = 3,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false
) 