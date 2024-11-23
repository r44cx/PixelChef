package com.pixelchef.models

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("description")
    val description: String,
    @SerializedName("preparationTime")
    val preparationTime: String,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("instructions")
    val instructions: List<String>
)