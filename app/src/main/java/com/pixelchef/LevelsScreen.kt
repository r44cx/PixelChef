package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelchef.viewmodels.GameViewModel

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
            Text("< Back", fontSize = 18.sp, color = colorResource(R.color.colorTextPrimary))
        }

        Text(
            text = "Levels",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Debug text
        Text(
            text = "Debug: Found ${levels.size} levels",
            color = Color.Red,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(levels.size) { index ->
                val level = levels[index]
                val isUnlocked = viewModel.isLevelUnlocked(level.id)

                LevelItem(
                    levelNumber = level.id,
                    isUnlocked = isUnlocked,
                    onClick = { if (isUnlocked) onSelectLevel(level.id) }
                )
            }
        }
    }
}

@Composable
fun LevelItem(
    levelNumber: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isUnlocked) colorResource(R.color.buttonGameBackground)
                else Color.Gray.copy(alpha = 0.5f)
            )
            .clickable(enabled = isUnlocked, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = levelNumber.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color.Black else Color.White.copy(alpha = 0.5f)
            )
            
            if (!isUnlocked) {
                Text(
                    text = "Locked",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
