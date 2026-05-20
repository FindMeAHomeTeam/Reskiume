package com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyOnlyActivistsInChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckRescueEvent
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CheckRescueEventViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkRescueEventUtil: CheckRescueEventUtil,
    private val checkActivistUtil: CheckActivistUtil,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val getChatFromLocalRepository: GetChatFromLocalRepository,
    private val getChatFromRemoteRepository: GetChatFromRemoteRepository,
    private val insertChatInLocalRepository: InsertChatInLocalRepository,
    private val modifyOnlyActivistsInChatInRemoteRepository: ModifyOnlyActivistsInChatInRemoteRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val log: Log
) : ViewModel() {

    private val rescueEventId: String =
        saveStateHandleProvider.provideObjectRoute(CheckRescueEvent::class).rescueEventId

    private val creatorId: String =
        saveStateHandleProvider.provideObjectRoute(CheckRescueEvent::class).creatorId

    private var myUid = ""

    private var myUser: User? = null

    val rescueEventDetailState: StateFlow<UiState<UiRescueEventDetail>> =
        checkRescueEventUtil.getRescueEventFlow(
            rescueEventId,
            creatorId,
            viewModelScope
        ).map { rescueEvent: RescueEvent? ->

            if (rescueEvent == null) {
                return@map null
            }
            myUid = observeAuthStateInAuthDataSource().firstOrNull()?.uid ?: " "
            updateMyUserData()

            val creator = checkActivistUtil.getUser(
                activistUid = rescueEvent.creatorId,
                myUserUid = myUid
            )
            if (creator == null) {
                null
            } else {
                UiRescueEventDetail(
                    rescueEvent = rescueEvent.copy(
                        imageUrl = if (rescueEvent.imageUrl.isEmpty()) {
                            rescueEvent.imageUrl
                        } else {
                            getImagePathForFileNameFromLocalDataSource(rescueEvent.imageUrl)
                        }
                    ),
                    allUiNonHumanAnimalsToRescue = rescueEvent.allNonHumanAnimalsToRescue.mapNotNull { nonHumanAnimalToRescue ->

                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            nonHumanAnimalToRescue.nonHumanAnimalId,
                            nonHumanAnimalToRescue.caregiverId,
                            viewModelScope
                        ).firstOrNull()
                    },
                    creator = creator,
                    chatExist = getChatFromLocalRepository(rescueEventId + creatorId).firstOrNull() != null
                )
            }
        }.toUiState()
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = UiState.Loading()
            )

    private suspend fun updateMyUserData() {
        if (myUid.isNotBlank()) {
            myUser = checkActivistUtil.getUser(
                activistUid = myUid,
                myUserUid = myUid
            )
            if (myUser?.isLoggedIn == false) {
                myUid = " "
            }
        }
    }

    fun isLoggedIn(): Boolean = myUser?.isLoggedIn == true

    fun canIStartTheChat(): Boolean = myUid != creatorId

    @OptIn(ExperimentalTime::class)
    fun findChat(
        rescueEventId: String,
        creatorId: String,
        onChatFound: (chatId: String, lastTimestamp: Long) -> Unit
    ) {
        viewModelScope.launch {

            val localChat = getChatFromLocalRepository(rescueEventId + creatorId).firstOrNull()
            if (localChat != null) {
                onChatFound(localChat.id, localChat.timestamp)

            } else {
                val remoteChat =
                    getChatFromRemoteRepository(rescueEventId + creatorId, myUid).firstOrNull()
                if (remoteChat != null) {

                    val result = modifyOnlyActivistsInChatInRemoteRepository(
                        chatId = rescueEventId + creatorId,
                        activistId = myUid,
                        shouldAdd = true
                    ).first()

                    if (result is DatabaseResult.Success) {
                        insertChatInLocalRepository(remoteChat) { isSuccess ->

                            if (isSuccess) {
                                insertChatInLocalCache(remoteChat.id) {

                                    onChatFound(remoteChat.id, remoteChat.timestamp)
                                }
                            }
                        }
                    }
                } else {
                    onChatFound("", 0)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun insertChatInLocalCache(
        chatId: String,
        onSuccess: () -> Unit
    ) {
        insertCacheInLocalRepository(
            LocalCache(
                cachedObjectId = chatId,
                savedBy = myUid,
                section = Section.CHATS,
                timestamp = Clock.System.now().epochSeconds
            )
        ) { rowId ->

            if (rowId > 0) {
                log.d(
                    "CheckRescueEventViewmodel",
                    "insertChatInLocalCache: $chatId added to local cache in section ${Section.CHATS}"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckRescueEventViewmodel",
                    "insertChatInLocalCache: Error adding $chatId to local cache in section ${Section.CHATS}"
                )
            }
        }
    }
}

data class UiRescueEventDetail(
    val rescueEvent: RescueEvent,
    val allUiNonHumanAnimalsToRescue: List<NonHumanAnimal>,
    val distance: Double? = null,
    val creator: User? = null,
    val chatExist: Boolean
)
