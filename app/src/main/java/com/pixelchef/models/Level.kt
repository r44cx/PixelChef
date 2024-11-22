package com.pixelchef.models

import com.google.gson.annotations.SerializedName

data class Level(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("availableIngredients")
    val availableIngredients: List<Ingredient>,
    @SerializedName("image")
    val image: String,
    @SerializedName("recipe")
    val recipe: Recipe,
    @SerializedName("isUnlocked")
    val isUnlocked: Boolean = false
) {
    fun debug() {
        println("Level: $id")
        println("Name: $name")
        println("Required ingredients: ${ingredients.map { it.name }}")
        println("Available ingredients: ${availableIngredients.map { it.name }}")
        println("Recipe: ${recipe.description}")
    }
}