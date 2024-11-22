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
fun GameScreen(level: Int, onBack: () -> Unit, viewModel: GameViewModel = viewModel()) {
    var showMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(level) {
        println("GameScreen: Loading level $level")
        viewModel.loadLevel(level)
    }

    val currentLevel by viewModel.currentLevel.collectAsState()
    val selectedIngredients by viewModel.selectedIngredients.collectAsState()
    val isLevelComplete by viewModel.isLevelComplete.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text("< Back", color = colorResource(R.color.colorTextPrimary))
        }

        // Debug output
        currentLevel?.let { level ->
            Text(
                text = "DEBUG OUTPUT:",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
          // Text(
          //     text = viewModel.getLevelDebugString(level),
          //     color = Color.Red,
          //     modifier = Modifier
          //         .background(Color.Black.copy(alpha = 0.1f))
          //         .padding(8.dp)
          // )
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
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = currentLevel?.name ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Progress indicator
            Text(
                text = "Progress: ${viewModel.getSelectedIngredientsCount()}/${viewModel.getTotalRequiredIngredients()}",
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
            if (level.availableIngredients.isEmpty()) {
                Text(
                    text = "No ingredients available",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(level.availableIngredients) { ingredient ->
                        IngredientItem(
                            ingredient = ingredient,
                            onClick = {
                                if (!isLevelComplete) {
                                    val success = viewModel.selectIngredient(ingredient)
                                    showMessage = if (success) "Correct!" else "Try again!"
                                }
                            }
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.buttonGameBackground))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val context = LocalContext.current
            val imageResId = remember(ingredient.imageResource) {
                context.resources.getIdentifier(
                    ingredient.imageResource,
                    "drawable",
                    context.packageName
                ).takeIf { it != 0 } ?: R.drawable.ingredient_placeholder
            }
            
            if (imageResId == R.drawable.ingredient_placeholder) {
                // Show text placeholder if image resource not found
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ingredient.name.first().toString(),
                        fontSize = 24.sp,
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
            
            Text(
                text = ingredient.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color(0x70FFFFFF))
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center
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