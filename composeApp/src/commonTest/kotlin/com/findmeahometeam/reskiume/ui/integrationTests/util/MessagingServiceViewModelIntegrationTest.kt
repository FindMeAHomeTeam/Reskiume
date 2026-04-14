package com.findmeahometeam.reskiume.ui.integrationTests.util

import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.util.fcm.MessagingServiceViewModel
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userPwd
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagingServiceViewModelIntegrationTest : CoroutineTestDispatcher(), KoinTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    override fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    private fun startKoin(
        authUser: AuthUser? = null,
        userWithAllSubscriptionData: UserWithAllSubscriptionData? = null
    ) {
        startKoin {
            modules(
                module {
                    single {
                        ObserveAuthStateInAuthDataSource(
                            FakeAuthRepository(
                                authUser = authUser,
                                authEmail = user.email,
                                authPassword = userPwd
                            )
                        )
                    }
                    single {
                        GetUserFromLocalDataSource(
                            FakeLocalUserRepository(
                                if (userWithAllSubscriptionData == null) {
                                    mutableListOf()
                                } else {
                                    mutableListOf(userWithAllSubscriptionData)
                                }
                            )
                        )
                    }
                }
            )
        }
    }

    @Test
    fun `given a registered user_when the app gets notification data_then the app retrieves their activist id`() =
        runTest {
            startKoin(authUser, userWithAllSubscriptionData)
            val messagingServiceViewModel = MessagingServiceViewModel()

            messagingServiceViewModel.retrieveActivistId {
                assertEquals(user.uid, it)
            }
        }

    @Test
    fun `given a logged out user_when the app gets notification data_then the app will not retrieve their activist id`() =
        runTest {
            startKoin(
                authUser,
                userWithAllSubscriptionData.copy(
                    userEntity = user.copy(isLoggedIn = false).toEntity()
                )
            )
            val messagingServiceViewModel = MessagingServiceViewModel()

            messagingServiceViewModel.retrieveActivistId {
                assertEquals("", it)
            }
        }

    @Test
    fun `given a user who deleted their account_when the app gets notification data_then the app will retrieve an empty activist id`() =
        runTest {
            startKoin(authUser)
            val messagingServiceViewModel = MessagingServiceViewModel()

            messagingServiceViewModel.retrieveActivistId {
                assertEquals("", it)
            }
        }

    @Test
    fun `given a user without firebase token_when the app gets notification data_then the app will retrieve an empty activist id`() =
        runTest {
            startKoin()
            val messagingServiceViewModel = MessagingServiceViewModel()

            messagingServiceViewModel.retrieveActivistId {
                assertEquals("", it)
            }
        }
}
