package com.example.fakeolx.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Lista : Screen("lista")
    data object Ulubione : Screen("ulubione")
    data object MojeOgloszenia : Screen("moje")
    data object Profil : Screen("profil")
    data object DodajOgloszenie : Screen("dodaj")

    data object Szczegoly : Screen("szczegoly/{$ARG_OGLOSZENIE_ID}") {
        fun createRoute(ogloszenieId: String) = "szczegoly/$ogloszenieId"
    }

    data object EdytujOgloszenie : Screen("edytuj/{$ARG_OGLOSZENIE_ID}") {
        fun createRoute(ogloszenieId: String) = "edytuj/$ogloszenieId"
    }

    companion object {
        const val ARG_OGLOSZENIE_ID = "ogloszenieId"
    }
}
