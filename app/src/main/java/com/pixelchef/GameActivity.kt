package com.pixelchef

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.pixelchef.models.Level
import com.pixelchef.utils.JsonParser

class GameActivity : AppCompatActivity() {
    private lateinit var levels: List<Level>
    private var currentLevelIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        levels = JsonParser.parseLevels(this)
        displayLevel(levels[currentLevelIndex])
    }

    private fun displayLevel(level: Level) {
        findViewById<TextView>(R.id.levelNameTextView).text = level.name
        findViewById<ImageView>(R.id.dishImageView).setImageResource(resources.getIdentifier(level.image, "drawable", packageName))

        val ingredientContainer = findViewById<LinearLayout>(R.id.ingredientContainer)
        ingredientContainer.removeAllViews()

        level.availableIngredients.forEach { ingredient ->
            val button = Button(this)
            button.text = ingredient
            button.setOnClickListener {
                if (level.ingredients.contains(ingredient)) {
                    // Correct ingredient
                    it.isEnabled = false
                    checkLevelCompletion(level)
                } else {
                    // Wrong ingredient
                    // You can add some feedback here
                }
            }
            ingredientContainer.addView(button)
        }
    }

    private fun checkLevelCompletion(level: Level) {
        if (findViewById<LinearLayout>(R.id.ingredientContainer).childCount == level.ingredients.size) {
            currentLevelIndex++
            if (currentLevelIndex < levels.size) {
                displayLevel(levels[currentLevelIndex])
            } else {
                // Game completed
                finish()
            }
        }
    }
}