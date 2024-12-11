package com.pixelchef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelchef.R
import com.pixelchef.ui.theme.pixelatedFont

@Composable
fun PixelatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
    fontSize: Int = 16
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(R.color.buttonBackground))
            .border(2.dp, Color.Black)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        // Dark border on bottom and right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0x40000000))
                .align(Alignment.BottomCenter)
        )
        
        Text(
            text = text,
            color = colorResource(R.color.colorTextSecondary),
            fontSize = fontSize.sp,
            fontFamily = pixelatedFont
        )
    }
} 