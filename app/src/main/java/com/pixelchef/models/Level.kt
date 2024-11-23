package com.pixelchef.models

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName

data class Level(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("ingredients")
    var ingredients: List<Ingredient>,
    @SerializedName("image")
    val image: String,
    @SerializedName("recipe")
    val recipe: Recipe,
    @SerializedName("isUnlocked")
    val isUnlocked: Boolean = false,
    @SerializedName("rating")
    val rating: Int
) {
    fun debug() {
        println("Level: $id")
        println("Name: $name")
        println("ingredients: ${ingredients.map { it.name }}")
        println("Recipe: ${recipe.description}")
    }
}