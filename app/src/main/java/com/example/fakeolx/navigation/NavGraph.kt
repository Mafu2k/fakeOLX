package com.example.fakeolx.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fakeolx.ui.screens.*
import com.example.fakeolx.ui.viewmodel.AuthViewModel
import com.example.fakeolx.ui.viewmodel.OgloszeniaViewModel

//Graf nawigacji
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    ogloszeniaViewModel: OgloszeniaViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        //Logowanie
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        //Rejestracja
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        //Lista ogloszen
        composable(Screen.Lista.route) {
            ListaOgloszenScreen(
                navController = navController,
                authViewModel = authViewModel,
                ogloszeniaViewModel = ogloszeniaViewModel
            )
        }

        //Szczegoly ogloszenia
        composable(
            route = Screen.Szczegoly.route + "/{ogloszenieId}",
            arguments = listOf(navArgument("ogloszenieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ogloszenieId = backStackEntry.arguments?.getString("ogloszenieId") ?: ""
            SzczegolyScreen(
                navController = navController,
                ogloszenieId = ogloszenieId,
                authViewModel = authViewModel,
                ogloszeniaViewModel = ogloszeniaViewModel
            )
        }

        //Dodaj ogloszenie
        composable(Screen.DodajOgloszenie.route) {
            DodajOgloszenieScreen(
                navController = navController,
                ogloszeniaViewModel = ogloszeniaViewModel
            )
        }

        //Edytuj ogloszenie
        composable(
            route = Screen.EdytujOgloszenie.route + "/{ogloszenieId}",
            arguments = listOf(navArgument("ogloszenieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ogloszenieId = backStackEntry.arguments?.getString("ogloszenieId") ?: ""
            EdytujOgloszenieScreen(
                navController = navController,
                ogloszenieId = ogloszenieId,
                ogloszeniaViewModel = ogloszeniaViewModel
            )
        }

        //Moje ogloszenia
        composable(Screen.MojeOgloszenia.route) {
            MojeOgloszeniaScreen(
                navController = navController,
                authViewModel = authViewModel,
                ogloszeniaViewModel = ogloszeniaViewModel
            )
        }
    }
}

//Ekrany
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Lista : Screen("lista")
    object Szczegoly : Screen("szczegoly")
    object DodajOgloszenie : Screen("dodaj")
    object EdytujOgloszenie : Screen("edytuj")
    object MojeOgloszenia : Screen("moje")
}
