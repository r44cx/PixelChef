package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelchef.viewmodels.GameViewModel
import com.pixelchef.models.Level

@Composable
fun RecipeScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val allLevels = viewModel.getAllLevels()
    val completedLevels by viewModel.completedLevels.collectAsState()
    
    // Filter levels that are unlocked (completed or next to be completed)
    val unlockedLevels = allLevels.filter { level ->
        viewModel.isLevelUnlocked(level.id)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text("< Back")
        }

        Text(
            "Recipe Book",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            items(unlockedLevels) { level ->
                RecipeCard(level)
            }
        }
    }
}

@Composable
fun RecipeCard(level: Level) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(level.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Preparation: ${level.recipe.preparationTime}")
                Spacer(modifier = Modifier.width(16.dp))
                Text("Difficulty: ${level.recipe.difficulty}")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Instructions:", fontWeight = FontWeight.Bold)
            level.recipe.instructions.forEachIndexed { index, instruction ->
                Text("${index + 1}. $instruction")
            }
        }
    }
} 