package com.pixelchef

import GameConstants
import android.annotation.SuppressLint
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GameScreen(
    level: Int,
    onBack: () -> Unit,
    onNextLevel: () -> Unit,
    onGoToRecipes: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    var showMessage by remember { mutableStateOf<Boolean?>(null) }
    
    LaunchedEffect(level) {
        println("GameScreen: Loading level $level")
        viewModel.loadLevel(level)
    }

    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLevelComplete by viewModel.isLevelComplete.collectAsState()
    val isLevelFailed by viewModel.isLevelFailed.collectAsState()

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
                text = "Correct: ${viewModel.getCorrectSelectedIngredientsCount()}/${viewModel.getTotalRequiredIngredients()} " +
                        "Failure: ${viewModel.getWrongSelectedIngredientsCount()}/${GameConstants.MAX_WRONG_INGREDIENTS}",
                fontSize = 16.sp,
                color = Color.Gray
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
                                if (!isLevelComplete && !isLevelFailed) {
                                    showMessage = viewModel.selectIngredient(ingredient)
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
    showMessage?.let { correct ->
        MessageOverlay(correct) {
            showMessage = null
        }
    }

    if (isLevelComplete) {
        LevelCompleteDialog(
            rating = viewModel.currentRating.value,
            onNextLevel = onNextLevel,
            onGoToRecipes = onGoToRecipes,
            onBackToLevels = onBack
        )
    }

    if (isLevelFailed) {
        LevelFailedDialog(
            onRetry = { viewModel.loadLevel(level) },
            onBackToLevels = onBack
        )
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
fun MessageOverlay(correct: Boolean, onDismiss: () -> Unit) {
    val image = if(correct) R.drawable.check else R.drawable.clear
    val message = if(correct) "Correct!" else "Try again!"

    LaunchedEffect(message) {
        kotlinx.coroutines.delay(1000)
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
            Column {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = message,
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = message,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LevelCompleteDialog(
    rating: Int,
    onNextLevel: () -> Unit,
    onGoToRecipes: () -> Unit,
    onBackToLevels: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Level Complete!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stars display
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    Text(
                        text = "★",
                        fontSize = 32.sp,
                        color = if (index < rating) 
                            colorResource(R.color.ratingStars)
                        else 
                            colorResource(R.color.buttonGameBackground)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
                ) {
                    Text("Next Level")
                }
                
                Button(
                    onClick = onGoToRecipes,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
                ) {
                    Text("View Recipe")
                }
                
                Button(
                    onClick = onBackToLevels,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
                ) {
                    Text("Back to Levels")
                }
            }
        }
    }
}

@Composable
fun LevelFailedDialog(
    onRetry: () -> Unit,
    onBackToLevels: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Level Failed!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
                ) {
                    Text("Try Again")
                }
                
                Button(
                    onClick = onBackToLevels,
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
                ) {
                    Text("Back to Levels")
                }
            }
        }
    }
}