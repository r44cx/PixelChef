package com.pixelchef

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LevelsScreen(onBack: () -> Unit, onSelectLevel: (Int) -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.gradient),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row() {
            Button(modifier = Modifier
                .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = onBack,
            ) {
                Text("< Back", fontSize = 18.sp, color = colorResource(R.color.colorTextPrimary))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Levels", fontSize = 48.sp)
        }

        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(30) { index ->
                    val level = index + 1
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectLevel(level) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.meals_meal1),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background( if (index < 8) Color(0x70FFFFFF) else Color(0x90000000)) // Transparency
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Level ${level}", fontSize = 20.sp, fontWeight = FontWeight.Bold,color = colorResource(R.color.colorTextPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "★ ★ ★", fontSize = 16.sp, color = colorResource(R.color.ratingStars))
                        }
                    }
                }
            }
        }
    }
}
