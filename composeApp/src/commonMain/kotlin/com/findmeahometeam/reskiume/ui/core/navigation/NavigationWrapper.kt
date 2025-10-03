package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.findmeahometeam.reskiume.ui.home.HomeScreen
import com.findmeahometeam.reskiume.ui.profile.login.LoginScreen

@Composable
fun NavigationWrapper() {

    val mainNavController: NavHostController = rememberNavController()

    NavHost(navController = mainNavController, startDestination = Routes.HOME_SCREEN.route) {

        composable(route = Routes.HOME_SCREEN.route) {
            HomeScreen(mainNavController)
        }

        composable(route = Routes.LOGIN.route) {
            //LoginScreen(onBackPressed = { mainNavController.navigateUp() })
        }
    }

}


