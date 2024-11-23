package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenuScreen(onNavigate: (String) -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.gradient),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row() {
            Text(text = "PixelChef", fontSize = 48.sp)
        }
        Row() {
            Image(
                painterResource(id = R.drawable.pixelchef_logo),
                contentDescription = "PixelChef",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(200.dp)
            )
        }
        Column(
            modifier = Modifier.height(250.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            MainButton("Play", "play", onNavigate)
            MainButton("Levels", "levels", onNavigate)
            MainButton("Recipes", "recipes", onNavigate)
            MainButton("Settings", "settings", onNavigate)
        }
        Row { }
    }
}


@Composable
fun MainButton(text: String, navigateTo: String, onNavigate: (String) -> Unit) {
    Button(
        onClick = { onNavigate(navigateTo) },
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
    ) {
        Text(text = text, color = colorResource(R.color.colorTextSecondary), fontSize = 20.sp)
    }
}

