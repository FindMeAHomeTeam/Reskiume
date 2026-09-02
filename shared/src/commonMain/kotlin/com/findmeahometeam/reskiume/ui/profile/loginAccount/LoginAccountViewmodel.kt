package com.findmeahometeam.reskiume.ui.profile.loginAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthResult
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val subscriptionManagerUtil: SubscriptionManagerUtil,
    private val log: Log
) : ViewModel() {
    private var _state: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle())
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    fun signInUsingEmail(email: String, password: String) {

        _state.value = UiState.Loading()
        signIn(email, password) { authUser ->

            updateLocalUser(
                uid = authUser.uid,
                onCompletionInsertCache = {
                    retrieveUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded(authUser.uid) { collectedUser: User ->

                        insertUserInLocalRepo(collectedUser)
                    }
                },
                onCompletionUpdateCache = {
                    retrieveUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded(authUser.uid) { collectedUser: User ->

                        modifyUserInLocalRepo(collectedUser) {
                            viewModelScope.launch {

                                subscriptionManagerUtil.subscribeToAllTopicsAfterLogin(collectedUser)
                                _state.value = UiState.Success(Unit)
                            }
                        }
                    }
                },
                onCacheRecent = { user ->
                    modifyUserInLocalRepo(user) {
                        viewModelScope.launch {

                            subscriptionManagerUtil.subscribeToAllTopicsAfterLogin(user)
                            _state.value = UiState.Success(Unit)
                        }
                    }
                }
            )
        }
    }

    private fun signIn(
        email: String,
        password: String,
        onSuccess: (AuthUser) -> Unit
    ) {
        viewModelScope.launch {
            when (val authResult = signInWithEmailAndPasswordFromAuthDataSource(email, password)) {
                is AuthResult.Error -> {
                    _state.value = UiState.Error(authResult.message)
                }

                is AuthResult.Success -> {
                    onSuccess(authResult.user)
                }
            }
        }
    }

    private fun updateLocalUser(
        uid: String,
        onCompletionInsertCache: suspend () -> Unit,
        onCompletionUpdateCache: suspend () -> Unit,
        onCacheRecent: suspend (User) -> Unit
    ) {
        viewModelScope.launch {

            getDataByManagingObjectLocalCacheTimestamp(
                cachedObjectId = uid,
                section = Section.USERS,
                onCompletionInsertCache = onCompletionInsertCache,
                onCompletionUpdateCache = onCompletionUpdateCache,
                onVerifyCacheIsRecent = {
                    log.d(
                        "LoginAccountViewmodel",
                        "updateLocalUser: User with uid $uid is up-to-date in local data source."
                    )
                    val user = getUserFromLocalDataSource(uid).first()!!
                    onCacheRecent(user.copy(isLoggedIn = true))
                }
            )
        }
    }

    private fun retrieveUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded(
        userUid: String,
        onSavedAvatar: (collectedUser: User) -> Unit
    ) {
        viewModelScope.launch {

            val collectedUser: User? = getUserFromRemoteDataSource(userUid).firstOrNull()
            when {
                collectedUser == null -> {
                    log.d(
                        "LoginAccountViewmodel",
                        "retrieveUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded: the user $userUid was not found in the remote data source despite successful authentication."
                    )
                }
                collectedUser.image.isNotBlank() -> {

                    val localImagePath: String = downloadImageToLocalDataSource(
                        userUid = collectedUser.uid,
                        extraId = "",
                        section = Section.USERS
                    )
                    onSavedAvatar(
                        collectedUser.copy(
                            image = localImagePath.ifBlank { collectedUser.image },
                            isLoggedIn = true
                        )
                    )
                }
                else -> {
                    log.d(
                        "LoginAccountViewmodel",
                        "retrieveUserFromRemoteDataSourceAndSaveItsAvatarIfNeeded: User ${collectedUser.uid} has no avatar image to save locally."
                    )
                    onSavedAvatar(collectedUser.copy(isLoggedIn = true))
                }
            }
        }
    }

    private fun insertUserInLocalRepo(collectedUser: User) {
        viewModelScope.launch {
            insertUserInLocalDataSource(collectedUser) { isSuccess ->

                if (isSuccess) {
                    log.d(
                        "LoginAccountViewmodel",
                        "insertUserInLocalRepo: Inserted user with uid ${collectedUser.uid} into the local data source."
                    )
                    _state.value = UiState.Success(Unit)
                } else {
                    log.e(
                        "LoginAccountViewmodel",
                        "insertUserInLocalRepo: Failed to insert user with uid ${collectedUser.uid} into the local data source."
                    )
                    _state.value = UiState.Error()
                }
            }
        }
    }

    private fun modifyUserInLocalRepo(
        collectedUser: User,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            modifyUserInLocalDataSource(collectedUser) { isUpdated ->

                if (isUpdated) {
                    log.d(
                        "LoginAccountViewmodel",
                        "modifyUserInLocalRepo: Modified user with uid ${collectedUser.uid} in the local data source."
                    )
                    onSuccess()
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
}
