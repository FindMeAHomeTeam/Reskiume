package com.findmeahometeam.reskiume.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.findmeahometeam.reskiume.ui.chats.checkChat.CheckChatScreen
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeScreen
import com.findmeahometeam.reskiume.ui.fosterHomes.createFosterHome.CreateFosterHomeScreen
import com.findmeahometeam.reskiume.ui.fosterHomes.modifyFosterHome.ModifyFosterHomeScreen
import com.findmeahometeam.reskiume.ui.home.HomeScreen
import com.findmeahometeam.reskiume.ui.profile.checkAdvice.CheckAdviceScreen
import com.findmeahometeam.reskiume.ui.profile.checkAllAdvice.CheckAllAdviceScreen
import com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes.CheckAllMyFosterHomesScreen
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.CheckAllMyRescueEventsScreen
import com.findmeahometeam.reskiume.ui.profile.checkMyAllNonHumanAnimals.CheckAllMyNonHumanAnimalsScreen
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalScreen
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckAllReviewsScreen
import com.findmeahometeam.reskiume.ui.profile.createAccount.CreateAccountScreen
import com.findmeahometeam.reskiume.ui.profile.createNonHumanAnimal.CreateNonHumanAnimalScreen
import com.findmeahometeam.reskiume.ui.profile.createReview.CreateReviewScreen
import com.findmeahometeam.reskiume.ui.profile.deleteAccount.DeleteAccountScreen
import com.findmeahometeam.reskiume.ui.profile.loginAccount.LoginAccountScreen
import com.findmeahometeam.reskiume.ui.profile.modifyAccount.ModifyAccountScreen
import com.findmeahometeam.reskiume.ui.profile.modifyNonHumanAnimal.ModifyNonHumanAnimalScreen
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventScreen
import com.findmeahometeam.reskiume.ui.rescueEvents.createRescueEvent.CreateRescueEventScreen
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.ModifyRescueEventScreen

@Composable
fun NavigationWrapper() {

    val mainNavController: NavHostController = rememberNavController()
    DeepLinkListener(mainNavController)

    NavHost(navController = mainNavController, startDestination = Routes.HOME_SCREEN.route) {

        composable(route = Routes.HOME_SCREEN.route) {
            HomeScreen(mainNavController)
        }

        composable<CheckAllMyFosterHomes> {
            CheckAllMyFosterHomesScreen(
                myUid = it.toRoute<CheckAllMyFosterHomes>().myUid,
                onBackPressed = { mainNavController.navigateUp() },
                onCreateFosterHome = { ownerId: String ->
                    mainNavController.navigate(CreateFosterHome(ownerId))
                },
                onModifyFosterHome = { fosterHomeId: String ->
                    mainNavController.navigate(ModifyFosterHome(fosterHomeId))
                }
            )
        }

        composable<CreateFosterHome> {
            CreateFosterHomeScreen(
                onCreateNonHumanAnimal = { mainNavController.navigate(Routes.CREATE_NON_HUMAN_ANIMAL.route) },
                onBackPressed = { mainNavController.navigateUp() }
            )
        }

        composable<ModifyFosterHome> {
            ModifyFosterHomeScreen {
                mainNavController.navigateUp()
            }
        }

        composable<CheckFosterHome>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "$FOSTER_HOME_DEEP_LINK/{ownerId}/{fosterHomeId}"
                }
            )
        ) {
            CheckFosterHomeScreen(
                onContactFosterHome = { chatId: String, lastTimestamp: Long ->
                    if (mainNavController.previousBackStackEntry?.destination?.route?.contains(
                            CheckChat::class.simpleName!!
                        ) == true
                    ) {
                        mainNavController.navigateUp()
                    } else {
                        mainNavController.navigate(CheckChat(chatId, lastTimestamp))
                    }
                },
                onReviewClick = { uid ->
                    mainNavController.navigate(CheckAllReviews(uid))
                },
                onCreateAccount = {
                    mainNavController.navigate(Routes.CREATE_ACCOUNT.route)
                },
                onCreateNonHumanAnimal = {
                    mainNavController.navigate(Routes.CREATE_NON_HUMAN_ANIMAL.route)
                },
                onBackPressed = {
                    mainNavController.navigateUp()
                }
            )
        }

        composable<CheckAllMyRescueEvents> {
            CheckAllMyRescueEventsScreen(
                myUid = it.toRoute<CheckAllMyRescueEvents>().myUid,
                onBackPressed = { mainNavController.navigateUp() },
                onCreateRescueEvent = { creatorId: String ->
                    mainNavController.navigate(CreateRescueEvent(creatorId))
                },
                onModifyRescueEvent = { rescueEventId: String ->
                    mainNavController.navigate(ModifyRescueEvent(rescueEventId))
                }
            )
        }

        composable<CreateRescueEvent> {
            CreateRescueEventScreen (
                onCreateNonHumanAnimal = { mainNavController.navigate(Routes.CREATE_NON_HUMAN_ANIMAL.route) },
                onBackPressed = { mainNavController.navigateUp() }
            )
        }

        composable<ModifyRescueEvent> {
            ModifyRescueEventScreen(
                onBackPressed = {
                    mainNavController.navigateUp()
                },
                onChatClicked = { chatId, timestamp ->
                    mainNavController.navigate(CheckChat(chatId, timestamp))
                }
            )
        }

        composable<CheckRescueEvent>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "$RESCUE_EVENT_DEEP_LINK/{creatorId}/{rescueEventId}"
                }
            )
        ) {
            CheckRescueEventScreen(
                onContactRescueEvent = { chatId: String, lastTimestamp: Long ->
                    if (mainNavController.previousBackStackEntry?.destination?.route?.contains(
                            CheckChat::class.simpleName!!
                        ) == true
                    ) {
                        mainNavController.navigateUp()
                    } else {
                        mainNavController.navigate(CheckChat(chatId, lastTimestamp))
                    }
                },
                onCreateAccount = {
                    mainNavController.navigate(Routes.CREATE_ACCOUNT.route)
                },
                onBackPressed = {
                    mainNavController.navigateUp()
                }
            )
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
            ModifyAccountScreen(onBackPressed = { mainNavController.navigateUp() })
        }

        composable<CheckAllReviews> {
            CheckAllReviewsScreen(
                onBackPressed = { mainNavController.navigateUp() },
                onReviewClick = { uid ->
                    mainNavController.navigate(CheckAllReviews(uid))
                }
            )
        }

        composable<CreateReview> {
            CreateReviewScreen(
                onBackPressed = { mainNavController.navigateUp() },
                onFinished = {
                    mainNavController.navigateUp()

                    if (mainNavController.previousBackStackEntry?.destination?.route?.contains(
                            CheckRescueEvent::class.simpleName!!
                        ) == true
                    ) {
                        mainNavController.navigateUp()
                    }
                    mainNavController.navigateUp()
                }
            )
        }

        composable<CheckAllMyNonHumanAnimals> {
            CheckAllMyNonHumanAnimalsScreen(
                onBackPressed = {
                    mainNavController.navigateUp()
                },
                onNonHumanAnimalClick = { nonHumanAnimalId: String, caregiverId: String ->
                    mainNavController.navigate(ModifyNonHumanAnimal(nonHumanAnimalId, caregiverId))
                },
                onCreateNonHumanAnimal = {
                    mainNavController.navigate(Routes.CREATE_NON_HUMAN_ANIMAL.route)
                }
            )
        }

        composable<ModifyNonHumanAnimal> {
            ModifyNonHumanAnimalScreen(onBackPressed = { mainNavController.navigateUp() })
        }

        composable<CheckNonHumanAnimal> {
            CheckNonHumanAnimalScreen(onBackPressed = { mainNavController.navigateUp() })
        }

        composable(route = Routes.CREATE_NON_HUMAN_ANIMAL.route) {
            CreateNonHumanAnimalScreen(onBackPressed = { mainNavController.navigateUp() })
        }

        composable<CheckChat>(
            deepLinks = listOf(
                navDeepLink {
                    this.uriPattern = "$CHAT_DEEP_LINK/{chatId}/{lastTimestamp}"
                }
            )
        ) {
            CheckChatScreen(
                onBackPressed = { mainNavController.navigateUp() },
                onCheckDetails = { isFosterHome: Boolean, id: String, chatHolderId: String, chatId: String ->
                    if (isFosterHome) {
                        mainNavController.navigate(CheckFosterHome(id, chatHolderId, chatId))
                    } else {
                        mainNavController.navigate(CheckRescueEvent(id, chatHolderId))
                    }
                },
                onCheckActivist = { uid: String ->
                    mainNavController.navigate(CheckAllReviews(uid))
                },
                onCheckNonHumanAnimal = { nonHumanAnimalId: String, caregiverId: String ->
                    mainNavController.navigate(CheckNonHumanAnimal(nonHumanAnimalId, caregiverId))
                },
                onAddReview = { allActivistIdsToReview, chatId, rescueEventId, creatorId ->
                    mainNavController.navigate(CreateReview(allActivistIdsToReview, chatId, rescueEventId, creatorId))
                }
            )
        }

        composable(route = Routes.CHECK_ALL_ADVICE.route) {
            CheckAllAdviceScreen(
                onBackPressed = { mainNavController.navigateUp() }
            ) { checkAdvice ->
                mainNavController.navigate(checkAdvice)
            }
        }
        composable<CheckAdvice> {
            CheckAdviceScreen(
                checkAdvice = it.toRoute(),
                onAuthorClick = { uid ->
                    mainNavController.navigate(CheckAllReviews(uid))
                },
                onBackPressed = { mainNavController.navigateUp() }
            )
        }

        composable(route = Routes.DELETE_ACCOUNT.route) {
            DeleteAccountScreen(onBackPressed = { mainNavController.navigateUp() })
        }
    }
}
