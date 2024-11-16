package com.pixelchef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixelChefTheme {
                PixelChefApp()
            }
        }
    }
}

@Composable
fun PixelChefApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "mainMenu") {
        composable("mainMenu") {
            MainMenuScreen { route -> navController.navigate(route) }
        }
        composable("levels") {
            LevelsScreen(
                onBack = { navController.popBackStack() },
                onSelectLevel = { level -> navController.navigate("levelDetail/$level") }
            )
        }
        composable("levelDetail/{level}") { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level")?.toIntOrNull() ?: 1
            GameScreen(
                level,
                onBack = { navController.popBackStack().not() }
            )
        }
    }
}

@Composable
fun PixelChefTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}

