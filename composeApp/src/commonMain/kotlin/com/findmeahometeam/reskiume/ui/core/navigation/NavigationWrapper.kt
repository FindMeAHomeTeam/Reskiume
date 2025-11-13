package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.findmeahometeam.reskiume.ui.home.HomeScreen
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountScreen
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountScreen
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountScreen
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountScreen

@Composable
fun NavigationWrapper() {

    val mainNavController: NavHostController = rememberNavController()

    NavHost(navController = mainNavController, startDestination = Routes.HOME_SCREEN.route) {

        composable(route = Routes.HOME_SCREEN.route) {
            HomeScreen(mainNavController)
        }

        composable(route = Routes.CREATE_ACCOUNT.route) {
            CreateAccountScreen(
                onBackPressed = { mainNavController.navigateUp() },
                navigateToLoginScreen = { mainNavController.navigate(Routes.LOGIN_ACCOUNT.route) }
            )
        }

        composable(route = Routes.LOGIN_ACCOUNT.route) {
            LoginAccountScreen(
                onBackPressed = { mainNavController.navigateUp() },
                onLoginSuccessful = {
                    mainNavController.popBackStack(Routes.CREATE_ACCOUNT.route, true)
                }
            )
        }

        composable(route = Routes.MODIFY_ACCOUNT.route) {
            ModifyAccountScreen(onBackPressed = {
                mainNavController.navigateUp()
            })
        }

        composable(route = Routes.DELETE_ACCOUNT.route) {
            DeleteAccountScreen(onBackPressed = {
                mainNavController.navigateUp()
            })
        }


    }
}
