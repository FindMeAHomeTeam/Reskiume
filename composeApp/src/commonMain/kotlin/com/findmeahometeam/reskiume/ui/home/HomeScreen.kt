package com.findmeahometeam.reskiume.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.gray
import com.findmeahometeam.reskiume.ui.core.navigation.bottomNavigation.BottomBarItem
import com.findmeahometeam.reskiume.ui.core.navigation.bottomNavigation.BottomNavigationWrapper
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.secondaryTextColor

@Composable
fun HomeScreen(mainNavHostController: NavHostController) {

    val bottomNavHostController: NavHostController = rememberNavController()
    val items: List<BottomBarItem> = listOf(
        BottomBarItem.FosterHomes(),
        BottomBarItem.RescueEvents(),
        BottomBarItem.Chats(),
        BottomBarItem.Profile()
    )

    Scaffold(bottomBar = {
        BottomNavigationBar(bottomNavHostController, items)
    }) { padding: PaddingValues ->
        Box(modifier = Modifier.padding(padding)) {
            BottomNavigationWrapper(bottomNavHostController, mainNavHostController)
        }
    }
}

@Composable
fun BottomNavigationBar(bottomNavHostController: NavHostController, items: List<BottomBarItem>) {
    val navBackStackEntry: NavBackStackEntry? by bottomNavHostController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    Column {
        HorizontalDivider(modifier = Modifier.fillMaxWidth().alpha(0.1f), color = gray)
        NavigationBar(
            containerColor = backgroundColor,
            contentColor = secondaryTextColor
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = secondaryTextColor,
                        unselectedTextColor = secondaryTextColor
                    ),
                    icon = item.icon,
                    label = { Text(item.title(), fontWeight = FontWeight.Bold) },
                    onClick = {
                        bottomNavHostController.navigate(route = item.route) {
                            bottomNavHostController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                )
            }
        }
    }
}
