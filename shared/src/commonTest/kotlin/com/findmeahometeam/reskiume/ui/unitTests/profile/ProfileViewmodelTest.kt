package com.findmeahometeam.reskiume.ui.unitTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import com.findmeahometeam.reskiume.userWithAllSubscriptionData
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileViewmodelTest : CoroutineTestDispatcher() {

    private val localUserRepository: LocalUserRepository = mock {
        everySuspend { getUser(user.uid) } returns flowOf(userWithAllSubscriptionData)
        
        everySuspend { getUser("wrongUid") } returns flowOf(null)
    }

    val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(user.image) } returns user.image
    }

    private val getUserFromLocalDataSource = GetUserFromLocalDataSource(localUserRepository)

    private val getImagePathForFileNameFromLocalDataSource =
        GetImagePathForFileNameFromLocalDataSource(manageImagePath)

    private val log: Log = mock {
        every { d(any(), any()) } calls { println(it) }
        every { e(any(), any()) } calls { println(it) }
    }

    private fun getProfileViewmodel(authRepository: AuthRepository): ProfileViewmodel {
        val observeAuthStateInAuthDataSource = ObserveAuthStateInAuthDataSource(authRepository)
        return ProfileViewmodel(
            observeAuthStateInAuthDataSource,
            getUserFromLocalDataSource,
            getImagePathForFileNameFromLocalDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when the user opens the profile section_then that user will see it populated`() =
        runTest {
            val authRepository: AuthRepository = mock {
                everySuspend { authState } returns (flowOf(authUser))
            }
            getProfileViewmodel(authRepository).userState.test {
                assertEquals(UiState.Success(user), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a registered user on auth but not on the local data source_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            val authRepository: AuthRepository = mock {
                everySuspend { authState } returns (flowOf(authUser.copy(uid = "wrongUid")))
            }
            getProfileViewmodel(authRepository).userState.test {
                assertTrue { awaitItem() is UiState.Idle }
                awaitComplete()
            }
            verify {
                log.d(
                    "ProfileViewmodel",
                    "userState: User wrongUid not found"
                )
            }
        }

    @Test
    fun `given an unregistered user_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            val authRepository: AuthRepository = mock {
                every { authState } returns flowOf(null)
            }
            getProfileViewmodel(authRepository).userState.test {
                assertTrue { awaitItem() is UiState.Idle }
                awaitComplete()
            }
        }
}
