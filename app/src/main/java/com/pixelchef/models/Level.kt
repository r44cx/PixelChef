package com.pixelchef.models

data class Level(
    val id: Int,
    val name: String,
    var ingredients: List<Ingredient>,
    val availableIngredients: List<Ingredient>,
    val image: String,
    val recipe: Recipe
)
