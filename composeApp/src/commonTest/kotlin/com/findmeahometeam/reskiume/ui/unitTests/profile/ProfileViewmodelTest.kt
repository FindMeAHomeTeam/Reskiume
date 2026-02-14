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
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel.ProfileUiState
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
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
        everySuspend { getUser(user.uid) } returns user
        everySuspend { getUser("wrongUid") } returns null
    }

    val manageImagePath: ManageImagePath = mock {
        every { getImagePathForFileName(user.image) } returns user.image
    }

    private val getUserFromLocalDataSource = GetUserFromLocalDataSource(localUserRepository)

    private val getImagePathForFileNameFromLocalDataSource =
        GetImagePathForFileNameFromLocalDataSource(manageImagePath)

    private val log: Log = mock {
        every { d(any(), any()) } returns Unit
        every { e(any(), any()) } returns Unit
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
            getProfileViewmodel(authRepository).state.test {
                assertEquals(ProfileUiState.Success(user), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a registered user on auth but not on the local data source_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            val authRepository: AuthRepository = mock {
                everySuspend { authState } returns (flowOf(authUser.copy(uid = "wrongUid")))
            }
            getProfileViewmodel(authRepository).state.test {
                assertEquals(
                    ProfileUiState.Error("ProfileViewmodel - User wrongUid not found"),
                    awaitItem()
                )
                awaitComplete()
            }
        }

    @Test
    fun `given an unregistered user_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            val authRepository: AuthRepository = mock {
                every { authState } returns flowOf(null)
            }
            getProfileViewmodel(authRepository).state.test {
                assertTrue { awaitItem() is ProfileUiState.Idle }
                awaitComplete()
            }
        }

    @Test
    fun `given an error loading the profile_when the app register the error_then it will call to logE`() {
        val authRepository: AuthRepository = mock {
            everySuspend { authState } returns (flowOf(authUser))
        }
        getProfileViewmodel(authRepository).logError("TestTag", "This is a test error")
        verify { log.e("TestTag", "This is a test error") }
    }
}
