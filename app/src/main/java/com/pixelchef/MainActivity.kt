package com.pixelchef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixelchef.viewmodels.GameViewModel

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
    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel()

    // Initialize ViewModel only once
    LaunchedEffect(Unit) {
        println("PixelChefApp: Initializing GameViewModel")
        gameViewModel.initialize(context)
    }

    NavHost(navController, startDestination = "mainMenu") {
        composable("mainMenu") {
            MainMenuScreen { route -> 
                when (route) {
                    "play" -> {
                        // Get the next uncompleted level and navigate to it
                        val nextLevel = gameViewModel.getNextUncompletedLevel()
                        println("Play: Navigating to level $nextLevel")  // Debug log
                        navController.navigate("levelDetail/$nextLevel")
                    }
                    else -> navController.navigate(route)
                }
            }
        }
        composable("levels") {
            LevelsScreen(
                onBack = { navController.popBackStack() },
                onSelectLevel = { level -> navController.navigate("levelDetail/$level") },
                viewModel = gameViewModel
            )
        }
        composable("recipes") {
            RecipeScreen(
                onBack = { navController.popBackStack() },
                viewModel = gameViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                viewModel = gameViewModel
            )
        }
        composable("levelDetail/{level}") { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level")?.toIntOrNull() ?: 1
            GameScreen(
                level = level,
                onBack = { navController.popBackStack() },
                viewModel = gameViewModel
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

