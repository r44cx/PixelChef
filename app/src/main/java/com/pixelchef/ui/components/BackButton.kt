package com.pixelchef.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.pixelchef.R

@Composable
fun BackButton(onBack: () -> Unit) {
    Button(
        onClick = onBack,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(colorResource(R.color.buttonBackground))
    ) {
        Text("< Back", fontSize = 14.sp, color = colorResource(R.color.colorTextSecondary))
    }
}