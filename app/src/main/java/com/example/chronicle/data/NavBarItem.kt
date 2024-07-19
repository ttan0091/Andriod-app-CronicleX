package com.example.chronicle.data

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chronicle.R
import com.example.chronicle.navigation.Routes

/**
 * contain the required data for making navigation bar items
 * **/
data class NavBarItem (
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Filled.Home,
    val unSelectedIcon: ImageVector = Icons.Outlined.Home,
    val route: String = ""
) {
    fun NavBarItems(context: Context): List<NavBarItem> {
        return listOf(
            NavBarItem(
                label = context.getString(R.string.nav_home),
                selectedIcon = Icons.Filled.Home,
                unSelectedIcon = Icons.Outlined.Home,
                route = Routes.Calendar.value
            ),
            NavBarItem(
                label = context.getString(R.string.nav_explore),
                selectedIcon = Icons.Filled.Search,
                unSelectedIcon = Icons.Outlined.Search,
                route = Routes.Event.value
            ),
            NavBarItem(
                label = context.getString(R.string.nav_create),
                selectedIcon = Icons.Filled.Edit,
                unSelectedIcon = Icons.Outlined.Edit,
                route = Routes.AddEvent.value
            ),
            NavBarItem(
                label = context.getString(R.string.nav_profile),
                selectedIcon = Icons.Filled.AccountCircle,
                unSelectedIcon = Icons.Outlined.AccountCircle,
                route = Routes.Profile.value
            )
        )
    }
}
