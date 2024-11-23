package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelchef.viewmodels.GameViewModel
import com.pixelchef.models.GameState


@Composable
fun LevelsScreen(
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val levels = viewModel.getAllLevels()
    
    // Debug output
    LaunchedEffect(Unit) {
        println("LevelsScreen: Number of levels: ${levels.size}")
        levels.forEach { level ->
            println("Level ${level.id}: ${level.name} (Unlocked: ${viewModel.isLevelUnlocked(level.id)})")
        }
    }
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

        Text(
            text = "Levels",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(levels.size) { index ->
                val level = levels[index]
                val gameState = viewModel.getGameState(level.id)
                val isUnlocked = viewModel.isLevelUnlocked(level.id)

                LevelItem(
                    levelNumber = level.id,
                    gameState = gameState,
                    isUnlocked = isUnlocked,
                    image = viewModel.getDrawableId(LocalContext.current, level.image),
                    onClick = { if (isUnlocked) onSelectLevel(level.id) }
                )
            }
        }
    }
}

@Composable
fun LevelItem(
    levelNumber: Int,
    gameState: GameState,
    isUnlocked: Boolean,
    image: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = isUnlocked, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isUnlocked) Color(0x70FFFFFF) else Color(0x90000000))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = levelNumber.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(if(isUnlocked) R.color.colorTextPrimary else R.color.colorTextSecondary)
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (!isUnlocked) {
                Text(
                    text = "Locked",
                    fontSize = 14.sp,
                    color = colorResource(R.color.colorTextSecondary)
                )
            } else {
                StarsForRating(gameState.rating)
            }
        }
    }
}

@Composable
fun StarsForRating(rating: Int) {
    val filledStarColor = colorResource(R.color.ratingStars)
    val unfilledStarColor = colorResource(R.color.buttonGameBackground)

    val starText = buildAnnotatedString {
        append(" ")
        for (i in 1..3) {
            withStyle(
                style = SpanStyle(
                    color = if (i <= rating) filledStarColor else unfilledStarColor,
                    fontSize = 20.sp
                )
            ) {
                append("★ ")
            }
        }
    }

    BasicText(text = starText)
}