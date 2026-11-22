package com.example.fakeolx.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fakeolx.ui.components.MainBottomBar
import com.example.fakeolx.ui.screens.ListaOgloszenScreen
import com.example.fakeolx.ui.screens.LoginScreen
import com.example.fakeolx.ui.screens.MojeOgloszeniaScreen
import com.example.fakeolx.ui.screens.OgloszenieFormScreen
import com.example.fakeolx.ui.screens.ProfilScreen
import com.example.fakeolx.ui.screens.RegisterScreen
import com.example.fakeolx.ui.screens.SzczegolyScreen
import com.example.fakeolx.ui.screens.UlubioneScreen
import com.example.fakeolx.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val startDestination = if (authViewModel.isLoggedIn) Screen.Lista.route else Screen.Login.route
    val ogloszenieIdArgument = listOf(navArgument(Screen.ARG_OGLOSZENIE_ID) { type = NavType.StringType })

    val openSzczegoly: (String) -> Unit = { id -> navController.navigate(Screen.Szczegoly.createRoute(id)) }
    val openDodaj: () -> Unit = { navController.navigate(Screen.DodajOgloszenie.route) }
    val openMainScreen: () -> Unit = {
        navController.navigate(Screen.Lista.route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
    val bottomBar: @Composable (Screen) -> Unit = { current ->
        MainBottomBar(
            currentScreen = current,
            onTabSelected = { tab ->
                navController.navigate(tab.route) {
                    popUpTo(Screen.Lista.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoggedIn = openMainScreen,
                onRegisterClick = {
                    authViewModel.resetAuthState()
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegistered = openMainScreen,
                onBack = {
                    authViewModel.resetAuthState()
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.Lista.route) {
            ListaOgloszenScreen(
                viewModel = viewModel(),
                onOgloszenieClick = openSzczegoly,
                onAddClick = openDodaj,
                bottomBar = { bottomBar(Screen.Lista) }
            )
        }

        composable(Screen.Ulubione.route) {
            UlubioneScreen(
                viewModel = viewModel(),
                onOgloszenieClick = openSzczegoly,
                bottomBar = { bottomBar(Screen.Ulubione) }
            )
        }

        composable(Screen.MojeOgloszenia.route) {
            MojeOgloszeniaScreen(
                viewModel = viewModel(),
                onOgloszenieClick = openSzczegoly,
                onAddClick = openDodaj,
                bottomBar = { bottomBar(Screen.MojeOgloszenia) }
            )
        }

        composable(Screen.Profil.route) {
            ProfilScreen(
                viewModel = viewModel(),
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                bottomBar = { bottomBar(Screen.Profil) }
            )
        }

        composable(Screen.Szczegoly.route, arguments = ogloszenieIdArgument) {
            SzczegolyScreen(
                viewModel = viewModel(),
                onBack = { navController.navigateUp() },
                onEdit = { id -> navController.navigate(Screen.EdytujOgloszenie.createRoute(id)) },
                onDeleted = { navController.navigateUp() }
            )
        }

        composable(Screen.DodajOgloszenie.route) {
            OgloszenieFormScreen(
                viewModel = viewModel(),
                onBack = { navController.navigateUp() },
                onSaved = { navController.navigateUp() }
            )
        }

        composable(Screen.EdytujOgloszenie.route, arguments = ogloszenieIdArgument) {
            OgloszenieFormScreen(
                viewModel = viewModel(),
                onBack = { navController.navigateUp() },
                onSaved = { navController.navigateUp() }
            )
        }
    }
}
