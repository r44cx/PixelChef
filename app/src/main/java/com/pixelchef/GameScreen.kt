package com.pixelchef

import android.view.Gravity
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GameScreen(level: Int, onBack: () -> Unit) {
    var showMessage by remember { mutableStateOf(false) }

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
        ) {
            Box (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(2.dp))
            ){
                Image(
                    painter = painterResource(id = R.drawable.meals_meal1),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp)
        ) {
            Text(text = "Sweet potato on mayo", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        }

        Row(modifier = Modifier
            .padding(horizontal = 16.dp)) {
            Text(text = "Choose your ingredients wisely!", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Row(modifier = Modifier
            .padding(horizontal = 8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(30) { index ->
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorResource(R.color.buttonGameBackground))
                            .clickable {
                                showMessage = true // Show message on click
                            },
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Column() {
                            Image (
                                painter = painterResource(id = R.drawable.ingredients_carrot),
                                contentDescription = null,
                                contentScale = ContentScale.Fit
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(150.dp)
                                .align(Alignment.BottomCenter)
                                .background( Color(0x70FFFFFF)), // Transparency,
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "Carrots", fontSize = 14.sp, fontWeight = FontWeight.Bold,color = colorResource(R.color.colorTextPrimary))
                        }


                    }
                }
            }
        }

    }

    // Conditionally display the TimedMessage
    if (showMessage) {
        TimedMessage(onTimeout = { showMessage = false })
    }
}

@Composable
fun TimedMessage(onTimeout: () -> Unit) {
    // Start a coroutine that dismisses the message after 1 second
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000L) // Wait for 1 second
        onTimeout() // Invoke the timeout callback
    }

    Box (
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("\uD83D\uDE05", fontWeight = FontWeight.Bold, fontSize = 96.sp, textAlign = TextAlign.Center)
                Text("Are you crazy?", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }

    // Display the message box
}