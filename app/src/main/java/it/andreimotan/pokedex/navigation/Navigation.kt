package it.andreimotan.pokedex.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.andreimotan.pokedex.pages.home.HomeScreen

val LocalNavController = compositionLocalOf<NavController> { error("No NavController found!") }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = NavRoutes.Home) {
            composable(NavRoutes.Home) {
                HomeScreen()

            }

            composable(NavRoutes.PokemonDetails) {
                //PokemonDetailsScreen()
            }
        }
    }
}

object NavRoutes {
    const val Home = "home"
    const val PokemonDetails = "pokemon-details"
}