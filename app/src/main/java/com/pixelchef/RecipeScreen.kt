package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelchef.models.Level
import com.pixelchef.ui.components.BackButton
import com.pixelchef.ui.theme.pixelatedFont
import com.pixelchef.viewmodels.GameViewModel

@Composable
fun RecipeScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val allLevels = viewModel.getAllLevels()
    val completedLevels by viewModel.completedLevels.collectAsState()

    val unlockedLevels = allLevels.filter { level ->
        viewModel.isLevelUnlocked(level.id)
    }

    BackgroundImage()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BackButton(onBack)
        HeaderText("Recipe Book")
        LevelList(unlockedLevels, viewModel)
    }
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.gradient),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun HeaderText(title: String) {
    Text(
        title,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp),
        fontFamily = pixelatedFont
    )
}

@Composable
fun LevelList(levels: List<Level>, viewModel: GameViewModel) {
    LazyColumn {
        items(levels) { level ->
            val gameState = viewModel.getGameState(level.id)
            RecipeCard(level, gameState.isCompleted)
        }
    }
}

@Composable
fun RecipeCard(level: Level, isCompleted: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                level.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pixelatedFont
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecipeDetails(level)

            if (isCompleted) {
                RecipeIngredients(level)
                RecipeInstructions(level)
            } else {
                LockedRecipeMessage()
            }
        }
    }
}

@Composable
fun RecipeDetails(level: Level) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "Preparation: ${level.recipe.preparationTime}",
            fontFamily = pixelatedFont
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            "Difficulty: ${level.recipe.difficulty}",
            fontFamily = pixelatedFont
        )
    }
}

@Composable
fun RecipeIngredients(level: Level) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "Required Ingredients:", 
        fontWeight = FontWeight.Bold,
        fontFamily = pixelatedFont
    )
    level.ingredients.filter { it.correct }.forEach { ingredient ->
        Text(
            "• ${ingredient.name}",
            fontFamily = pixelatedFont
        )
    }
}

@Composable
fun RecipeInstructions(level: Level) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "Instructions:", 
        fontWeight = FontWeight.Bold,
        fontFamily = pixelatedFont
    )
    level.recipe.instructions.forEachIndexed { index, instruction ->
        Text(
            "${index + 1}. $instruction",
            fontFamily = pixelatedFont
        )
    }
}

@Composable
fun LockedRecipeMessage() {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Complete this level to unlock the recipe!",
        color = colorResource(R.color.colorTextSecondary),
        fontFamily = pixelatedFont
    )
}
