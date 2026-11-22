package com.example.fakeolx.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fakeolx.navigation.Screen

private data class BottomTab(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val icon: ImageVector
)

private val bottomTabs = listOf(
    BottomTab(Screen.Lista, "Ogłoszenia", Icons.Filled.Home, Icons.Outlined.Home),
    BottomTab(Screen.Ulubione, "Ulubione", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomTab(Screen.MojeOgloszenia, "Moje", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    BottomTab(Screen.Profil, "Profil", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun MainBottomBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            val isSelected = tab.screen == currentScreen
            NavigationBarItem(
                selected = isSelected,
                onClick = { if (!isSelected) onTabSelected(tab.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                        contentDescription = null
                    )
                },
                label = { Text(tab.label) }
            )
        }
    }
}
