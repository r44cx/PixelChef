package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelchef.models.Ingredient
import com.pixelchef.viewmodels.GameViewModel

@Composable
fun GameScreen(
    level: Int,
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    var showMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(level) {
        println("GameScreen: Loading level $level")
        viewModel.loadLevel(level)
    }

    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLevelComplete by viewModel.isLevelComplete.collectAsState()

    Image(
        painter = painterResource(id = R.drawable.gradient),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        Button(
            onClick = onBack,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
        ) {
            Text("< Back", fontSize = 14.sp, color = colorResource(R.color.colorTextSecondary))
        }

        // Recipe image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .shadow(4.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            currentLevel?.let {
                Image(
                    painter = painterResource(id = R.drawable.meals_meal1),
                    contentDescription = it.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Recipe name and progress
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentLevel?.name ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Progress indicator
            Text(
                text = "Correct: ${viewModel.getSelectedIngredientsCount()}/${viewModel.getTotalRequiredIngredients()} ",
                fontSize = 16.sp,
                color = Color.Gray
            )

            // Progress indicator
            Text(
                text = "Failure: ${viewModel.getSelectedIngredientsCount()}/${viewModel.getTotalRequiredIngredients()}",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        if (isLevelComplete) {
            Text(
                text = "Level Complete!",
                fontSize = 20.sp,
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Ingredients grid
        currentLevel?.let { level ->
            if (level.ingredients.isEmpty()) {
                Text(
                    text = "No ingredients available",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(level.ingredients) { ingredient ->
                        IngredientItem(
                            ingredient = ingredient,
                            onClick = {
                                if (!isLevelComplete) {
                                    val success = viewModel.selectIngredient(ingredient)
                                    showMessage = if (success) "Correct!" else "Try again!"
                                }
                            },
                            viewModel
                        )
                    }
                }
            }
        }
    }

    // Feedback message
    showMessage?.let { message ->
        MessageOverlay(message) {
            showMessage = null
        }
    }
}

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    onClick: () -> Unit,
    viewModel: GameViewModel
) {
    val isSelect = viewModel.isIngredientSelected(ingredient.name);
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.buttonGameBackground))
            .clickable(enabled = !isSelect, onClick = onClick)
    ) {
        Column {
            val imageResId = viewModel.getDrawableId(LocalContext.current, ingredient.imageResource)
            
            if (imageResId == R.drawable.ingredient_placeholder) {
                // Show text placeholder if image resource not found
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(150.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ingredient.name.first().toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = ingredient.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(150.dp)
                .align(Alignment.BottomCenter)
                .background( Color(0x70FFFFFF)), // Transparency,
            contentAlignment = Alignment.Center,
        ) {
            Text(text = ingredient.name, fontSize = 14.sp, fontWeight = FontWeight.Bold,color = colorResource(R.color.colorTextPrimary))
        }

        if(isSelect) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x90000000))
            )
        }
    }

}

@Composable
fun MessageOverlay(message: String, onDismiss: () -> Unit) {
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(2000)
        onDismiss()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}