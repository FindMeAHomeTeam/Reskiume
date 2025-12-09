package com.findmeahometeam.reskiume.ui.core.navigation.bottomNavigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.findmeahometeam.reskiume.ui.chats.ChatsScreen
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllNonHumanAnimals
import com.findmeahometeam.reskiume.ui.core.navigation.CheckReviews
import com.findmeahometeam.reskiume.ui.core.navigation.Routes
import com.findmeahometeam.reskiume.ui.fosterHomes.FosterHomesScreen
import com.findmeahometeam.reskiume.ui.profile.ProfileScreen
import com.findmeahometeam.reskiume.ui.rescue.RescueScreen

@Composable
fun BottomNavigationWrapper(
    bottomNavHostController: NavHostController,
    mainNavHostController: NavHostController
) {

    // TODO start destination
    NavHost(navController = bottomNavHostController, startDestination = Routes.PROFILE.route) {

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
            ProfileScreen(
                navigateToCreateAccountScreen = {
                    mainNavHostController.navigate(Routes.CREATE_ACCOUNT.route)
                },
                navigateToModifyAccountScreen = {
                    mainNavHostController.navigate(Routes.MODIFY_ACCOUNT.route)
                },
                navigateToCheckReviewsScreen = { uid ->
                    mainNavHostController.navigate(CheckReviews(uid))
                },
                navigateToCheckNonHumanAnimalsScreen = { uid ->
                    mainNavHostController.navigate(CheckAllNonHumanAnimals(uid))
                },
                navigateToDeleteAccountScreen = {
                    mainNavHostController.navigate(Routes.DELETE_ACCOUNT.route)
                }
            )
        }
    }
}
