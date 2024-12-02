package com.pixelchef.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BackButton(onBack: () -> Unit) {
    PixelatedButton(
        text = "< Back",
        onClick = onBack,
        modifier = Modifier.width(100.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
        fontSize = 12
    )
}