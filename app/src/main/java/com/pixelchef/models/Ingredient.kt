package com.pixelchef.models

import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("name")
    val name: String,
    @SerializedName("imageResource")
    val imageResource: String
) 