package com.pixelchef.models

data class Level(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val availableIngredients: List<String>,
    val image: String
)