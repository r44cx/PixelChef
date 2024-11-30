package com.pixelchef

import GameConstants
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pixelchef.models.Level
import com.pixelchef.ui.components.BackButton
import com.pixelchef.ui.components.BackgroundImage
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
        viewModel.loadLevel(level)
    }

    val currentLevel by viewModel.currentLevel.collectAsState()
    val isLevelComplete by viewModel.isLevelComplete.collectAsState()
    val isLevelFailed by viewModel.isLevelFailed.collectAsState()

    BackgroundImage()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BackButton(onBack)
        RecipeImage(currentLevel, viewModel)
        RecipeDetails(currentLevel, viewModel)
        IngredientsGrid(
            currentLevel,
            viewModel,
            isLevelComplete,
            isLevelFailed,
            showMessage,
            { showMessage = it })
    }

    FeedbackMessage(showMessage) { showMessage = null }
    if (isLevelComplete) LevelCompleteDialog(
        viewModel.currentRating.value,
        onNextLevel,
        onGoToRecipes,
        onBack
    )
    if (isLevelFailed) LevelFailedDialog({ viewModel.loadLevel(level) }, onBack)
}

@Composable
private fun RecipeImage(currentLevel: Level?, viewModel: GameViewModel) {
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
                painter = painterResource(id = viewModel.getDrawableId(LocalContext.current, currentLevel.image)),
                contentDescription = it.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RecipeDetails(currentLevel: Level?, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentLevel?.name.orEmpty(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Correct: ${viewModel.getCorrectSelectedIngredientsCount()}/${viewModel.getTotalRequiredIngredients()} " +
                    "Failure: ${viewModel.getWrongSelectedIngredientsCount()}/${GameConstants.MAX_WRONG_INGREDIENTS}",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun IngredientsGrid(
    currentLevel: Level?,
    viewModel: GameViewModel,
    isLevelComplete: Boolean,
    isLevelFailed: Boolean,
    showMessage: Boolean?,
    onShowMessageChange: (Boolean) -> Unit
) {
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
                                onShowMessageChange(viewModel.selectIngredient(ingredient))
                            }
                        },
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackMessage(showMessage: Boolean?, onDismiss: () -> Unit) {
    showMessage?.let { correct ->
        MessageOverlay(correct, onDismiss)
    }
}

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    onClick: () -> Unit,
    viewModel: GameViewModel
) {
    val isSelected = viewModel.isIngredientSelected(ingredient.name)
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(R.color.buttonGameBackground))
            .clickable(enabled = !isSelected, onClick = onClick)
    ) {
        Column {
            val imageResId = viewModel.getDrawableId(LocalContext.current, ingredient.imageResource)

            if (imageResId == R.drawable.ingredient_placeholder) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(150.dp),
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
                        .weight(1f),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(150.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0x70FFFFFF)), // Transparency
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ingredient.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.colorTextPrimary)
            )
        }

        if (isSelected) {
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
    val imageResId = if (correct) R.drawable.check else R.drawable.clear
    val message = if (correct) "Correct!" else "Try again!"

    LaunchedEffect(message) {
        kotlinx.coroutines.delay(600)
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = imageResId),
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
    onBack: () -> Unit
) {
    DialogBox(title = "Level Complete!", titleColor = Color.Green) {
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
        ActionButton("Next Level", onNextLevel)
        ActionButton("View Recipe", onGoToRecipes)
        ActionButton("Back", onBack)
    }
}

@Composable
fun LevelFailedDialog(
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    DialogBox(title = "Level Failed!", titleColor = Color.Red) {
        Spacer(modifier = Modifier.height(24.dp))
        ActionButton("Try Again", onRetry)
        ActionButton("Back", onBack)
    }
}

@Composable
fun DialogBox(
    title: String,
    titleColor: Color,
    content: @Composable ColumnScope.() -> Unit
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
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            content()
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
    ) {
        Text(text)
    }
}

