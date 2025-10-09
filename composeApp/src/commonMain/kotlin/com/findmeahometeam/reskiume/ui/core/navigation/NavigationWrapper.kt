package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.findmeahometeam.reskiume.ui.home.HomeScreen
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountScreen
import com.findmeahometeam.reskiume.ui.profile.deleteUser.DeleteAccountScreen
import com.findmeahometeam.reskiume.ui.profile.login.LoginScreen
import com.findmeahometeam.reskiume.ui.profile.personalInformation.PersonalInformationScreen

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
                navigateToLoginScreen = { mainNavController.navigate(Routes.LOGIN.route) }
            )
        }

        composable(route = Routes.LOGIN.route) {
            LoginScreen(
                onBackPressed = { mainNavController.navigateUp() },
                onLoginSuccessful = {
                    mainNavController.popBackStack(Routes.CREATE_ACCOUNT.route, true)
                }
            )
        }

        composable(route = Routes.PERSONAL_INFORMATION.route) {
            PersonalInformationScreen(onBackPressed = {
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
