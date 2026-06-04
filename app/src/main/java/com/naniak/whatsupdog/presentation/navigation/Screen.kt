package com.naniak.whatsupdog.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector = Icons.Default.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Breeds : Screen(
        route = "breeds",
        title = "Breeds",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    )

    data object Favorites : Screen(
        route = "favorites",
        title = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )

    data class Gallery(val breed: String = "") : Screen(
        route = "gallery/{breed}",
        title = "Gallery"
    ) {
        companion object {
            const val ROUTE = "gallery/{breed}"
            fun createRoute(breed: String): String = "gallery/$breed"
        }
    }

    companion object {
        val bottomNavItems = listOf(Home, Breeds, Favorites)
    }
}
