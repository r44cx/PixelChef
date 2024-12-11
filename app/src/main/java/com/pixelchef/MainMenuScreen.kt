package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.pixelchef.ui.components.BackgroundImage
import com.pixelchef.ui.theme.pixelatedFont
import com.pixelchef.ui.components.PixelatedButton

@Composable
fun MainMenuScreen(onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BackgroundImage()
        MainMenuContent(onNavigate)
    }
}

@Composable
fun MainMenuContent(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TitleText()
        LogoImage()
        MenuButtons(onNavigate)
    }
}

@Composable
fun TitleText() {
    Text(
        text = "PixelChef",
        fontSize = 32.sp,
        fontFamily = pixelatedFont
    )
}

@Composable
fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.pixelchef_logo),
        contentDescription = "PixelChef Logo",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.width(200.dp)
    )
}

@Composable
fun MenuButtons(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.height(250.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        listOf(
            "Play" to "play",
            "Levels" to "levels",
            "Recipes" to "recipes",
            "Settings" to "settings"
        ).forEach { (label, route) ->
            MainButton(label, route, onNavigate)
        }
    }
}

@Composable
fun MainButton(text: String, navigateTo: String, onNavigate: (String) -> Unit) {
    PixelatedButton(
        text = text,
        onClick = { onNavigate(navigateTo) },
        modifier = Modifier.padding(horizontal = 30.dp)
    )
}
