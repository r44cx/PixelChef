package com.pixelchef

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.pixelchef.utils.JsonParser

class RecipeBookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_book)

        val levels = JsonParser.parseLevels(this)
        val recipeNames = levels.map { it.name }

        val listView = findViewById<ListView>(R.id.recipeListView)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, recipeNames)
    }
}