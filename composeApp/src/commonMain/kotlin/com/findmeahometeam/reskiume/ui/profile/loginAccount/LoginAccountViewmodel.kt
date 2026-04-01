package com.findmeahometeam.reskiume.ui.profile.loginAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginAccountViewmodel(
    private val signInWithEmailAndPasswordFromAuthDataSource: SignInWithEmailAndPasswordFromAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getUserFromRemoteDataSource: GetUserFromRemoteDataSource,
    private val getUserFromLocalDataSource: GetUserFromLocalDataSource,
    private val downloadImageToLocalDataSource: DownloadImageToLocalDataSource,
    private val insertUserInLocalDataSource: InsertUserInLocalDataSource,
    private val modifyUserInLocalDataSource: ModifyUserInLocalDataSource,
    private val log: Log
) : ViewModel() {
    private var _state: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle())
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    fun signInUsingEmail(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading()
            when (val authResult = signInWithEmailAndPasswordFromAuthDataSource(email, password)) {
                is AuthResult.Error -> {
                    _state.value = UiState.Error(authResult.message)
                }

                is AuthResult.Success -> {
                    updateLocalUser(authResult.user)
                }
            }
        }
    }

    private fun updateLocalUser(authUser: AuthUser) {
        viewModelScope.launch {

            getDataByManagingObjectLocalCacheTimestamp(
                cachedObjectId = authUser.uid,
                section = Section.USERS,
                onCompletionInsertCache = {

                    getUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded(authUser.uid) { collectedUser: User ->

                        viewModelScope.launch {
                            insertUserInLocalDataSource(collectedUser) { rowId: Long ->

                                if (rowId > 0) {
                                    log.d(
                                        "LoginAccountViewmodel",
                                        "updateLocalUser: Inserted user with uid ${collectedUser.uid} into local data source."
                                    )
                                    _state.value = UiState.Success(Unit)
                                } else {
                                    log.e(
                                        "LoginAccountViewmodel",
                                        "updateLocalUser: Failed to insert user with uid ${collectedUser.uid} into local data source."
                                    )
                                    _state.value = UiState.Error()
                                }
                            }
                        }
                    }
                },
                onCompletionUpdateCache = {

                    getUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded(authUser.uid) { collectedUser: User ->

                        modifyUserInLocalRepo(collectedUser)
                    }
                },
                onVerifyCacheIsRecent = {

                    val user = getUserFromLocalDataSource(authUser.uid)!!
                    modifyUserInLocalRepo(user.copy(isLoggedIn = true))
                    log.d(
                        "LoginAccountViewmodel",
                        "updateLocalUser: User with uid ${authUser.uid} is up-to-date in local data source."
                    )

                    _state.value = UiState.Success(Unit)
                }
            )
        }
    }

    private fun modifyUserInLocalRepo(collectedUser: User) {
        viewModelScope.launch {

            modifyUserInLocalDataSource(collectedUser) { rowsModified: Int ->

                if (rowsModified > 0) {
                    log.d(
                        "LoginAccountViewmodel",
                        "modifyUserInLocalRepo: Modified user with uid ${collectedUser.uid} in the local data source."
                    )
                    _state.value = UiState.Success(Unit)
                } else {
                    log.e(
                        "LoginAccountViewmodel",
                        "modifyUserInLocalRepo: Failed to modify user with uid ${collectedUser.uid} in the local data source."
                    )
                    _state.value = UiState.Error()
                }
            }
        }
    }

    private fun getUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded(
        userUid: String,
        onSavedAvatar: (collectedUser: User) -> Unit
    ) {
        viewModelScope.launch {

            val collectedUser: User? = getUserFromRemoteDataSource(userUid).firstOrNull()

            if (collectedUser == null) {
                log.d(
                    "LoginAccountViewmodel",
                    "Unless it is the default collectedUser value, it seems that the user $userUid was not found in the remote data source despite successful authentication."
                )
            } else if (collectedUser.image.isNotBlank()) {

                val localImagePath: String = downloadImageToLocalDataSource(
                    userUid = collectedUser.uid,
                    extraId = "",
                    section = Section.USERS
                )
                onSavedAvatar(collectedUser.copy(
                    image = localImagePath.ifBlank { collectedUser.image },
                    isLoggedIn = true
                ))
            } else {
                log.d(
                    "LoginAccountViewmodel",
                    "User ${collectedUser.uid} has no avatar image to save locally."
                )
                onSavedAvatar(collectedUser.copy(isLoggedIn = true))
            }
        }
    }
}
