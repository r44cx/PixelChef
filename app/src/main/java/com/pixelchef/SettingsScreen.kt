package com.pixelchef

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelchef.ui.components.BackButton
import com.pixelchef.viewmodels.GameViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()
        SettingsContent(onBack = onBack, onReset = { viewModel.resetProgress() })
    }
}

@Composable
fun SettingsContent(
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BackButton(onBack = onBack)
        SettingsTitle()
        Spacer(modifier = Modifier.height(32.dp))
        ResetProgressSection(onReset = onReset)
    }
}

@Composable
fun SettingsTitle() {
    Text(
        text = "Settings",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun ResetProgressSection(onReset: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ResetButton(onReset = onReset)
            WarningText()
        }
    }
}

@Composable
fun ResetButton(onReset: () -> Unit) {
    Button(
        onClick = onReset,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text(
            text = "Reset Game Progress",
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun WarningText() {
    Text(
        text = "Warning: This will reset all level progress and stars!",
        color = Color.Red,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}
