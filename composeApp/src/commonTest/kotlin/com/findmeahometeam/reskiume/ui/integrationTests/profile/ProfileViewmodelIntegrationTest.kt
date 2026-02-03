package com.findmeahometeam.reskiume.ui.integrationTests.profile

import app.cash.turbine.test
import com.findmeahometeam.reskiume.CoroutineTestDispatcher
import com.findmeahometeam.reskiume.authUser
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.local.LocalUserRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeAuthRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLocalUserRepository
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeLog
import com.findmeahometeam.reskiume.ui.integrationTests.fakes.FakeManageImagePath
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel
import com.findmeahometeam.reskiume.ui.profile.ProfileViewmodel.ProfileUiState
import com.findmeahometeam.reskiume.ui.util.ManageImagePath
import com.findmeahometeam.reskiume.user
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileViewmodelIntegrationTest : CoroutineTestDispatcher() {

    private val log: Log = FakeLog()

    private val manageImagePath: ManageImagePath = FakeManageImagePath()

    private val getCompleteImagePathFromLocalDataSource =
        GetCompleteImagePathFromLocalDataSource(manageImagePath)

    private fun getProfileViewmodel(
        authRepository: AuthRepository = FakeAuthRepository(),
        localUserRepository: LocalUserRepository = FakeLocalUserRepository()
    ): ProfileViewmodel {
        val observeAuthStateInAuthDataSource = ObserveAuthStateInAuthDataSource(authRepository)
        val getUserFromLocalDataSource = GetUserFromLocalDataSource(localUserRepository)
        return ProfileViewmodel(
            observeAuthStateInAuthDataSource,
            getUserFromLocalDataSource,
            getCompleteImagePathFromLocalDataSource,
            log
        )
    }

    @Test
    fun `given a registered user_when the user opens the profile section_then that user will see it populated`() =
        runTest {
            val authRepository: AuthRepository = FakeAuthRepository(authUser = authUser)
            val localUserRepository: LocalUserRepository = FakeLocalUserRepository(mutableListOf(user))
            getProfileViewmodel(authRepository, localUserRepository).state.test {
                assertEquals(ProfileUiState.Success(user), awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `given a registered user on auth but not on the local data source_when the user opens the profile section_then that user will see the default profile`() =
        runTest {
            val authRepository: AuthRepository = FakeAuthRepository(authUser = authUser.copy(uid = "wrongUid"))
            val localUserRepository: LocalUserRepository = FakeLocalUserRepository(mutableListOf(user))
            getProfileViewmodel(authRepository, localUserRepository).state.test {
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
            getProfileViewmodel().state.test {
                assertTrue { awaitItem() is ProfileUiState.Idle }
                awaitComplete()
            }
        }
}
