package com.findmeahometeam.reskiume.ui.core.navigation.bottomNavigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.findmeahometeam.reskiume.ui.chats.ChatsScreen
import com.findmeahometeam.reskiume.ui.core.navigation.Routes
import com.findmeahometeam.reskiume.ui.fosterHomes.FosterHomesScreen
import com.findmeahometeam.reskiume.ui.profile.ProfileScreen
import com.findmeahometeam.reskiume.ui.rescue.RescueScreen

@Composable
fun BottomNavigationWrapper(
    navHostController: NavHostController,
    mainNavHostController: NavHostController
) {

    // TODO start destination
    NavHost(navController = navHostController, startDestination = Routes.PROFILE.route) {

        composable(route = Routes.FOSTER_HOMES.route) {
            FosterHomesScreen()
        }

        composable(route = Routes.RESCUE.route) {
            RescueScreen()
        }

        composable(route = Routes.CHATS.route) {
            ChatsScreen()
        }

        composable(route = Routes.PROFILE.route) {
            ProfileScreen(navigateToLoginScreen = {
                mainNavHostController.navigate(Routes.LOGIN.route)
            })
        }

    }


}