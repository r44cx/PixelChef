package com.pixelchef.utils

import android.content.Context
import com.pixelchef.models.Level
import org.json.JSONArray

object JsonParser {
    fun parseLevels(context: Context): List<Level> {
        val jsonString = context.assets.open("levels.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val levels = mutableListOf<Level>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            levels.add(
                Level(
                    id = jsonObject.getInt("id"),
                    name = jsonObject.getString("name"),
                    ingredients = jsonObject.getJSONArray("ingredients").toList(),
                    availableIngredients = jsonObject.getJSONArray("availableIngredients").toList(),
                    image = jsonObject.getString("image")
                )
            )
        }

        return levels
    }

    private fun JSONArray.toList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until this.length()) {
            list.add(this.getString(i))
        }
        return list
    }
}