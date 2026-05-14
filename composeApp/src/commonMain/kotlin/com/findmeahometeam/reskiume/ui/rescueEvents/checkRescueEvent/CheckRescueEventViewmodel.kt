package com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.chat.ActivistInfo
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyChatInRemoteRepository
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
    private val insertChatInRemoteRepository: InsertChatInRemoteRepository,
    private val insertChatInLocalRepository: InsertChatInLocalRepository,
    private val modifyChatInRemoteRepository: ModifyChatInRemoteRepository,
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
        allNonHumanAnimals: List<NonHumanAnimal>,
        onChatFound: (chatId: String, lastTimestamp: Long) -> Unit
    ) {
        viewModelScope.launch {
            var inexistentChatFlag = false
            val localChat = getChatFromLocalRepository(rescueEventId + creatorId).firstOrNull()
            val chat = localChat
                ?: getChatFromRemoteRepository(rescueEventId + creatorId, myUid).firstOrNull()
                ?: Chat(
                    id = rescueEventId + creatorId,
                    fosterHomeId = "",
                    rescueEventId = rescueEventId,
                    chatHolderId = creatorId,
                    allNonHumanAnimalsInfo = allNonHumanAnimals.map { nonHumanAnimal ->
                        NonHumanAnimalInfo(
                            nonHumanAnimalId = nonHumanAnimal.id,
                            chatId = rescueEventId + creatorId,
                            caregiverId = nonHumanAnimal.caregiverId
                        )
                    },
                    allActivistsInfo = listOf(
                        ActivistInfo(
                            id = Clock.System.now().epochSeconds.toString() + rescueEventId + creatorId,
                            chatId = rescueEventId + creatorId,
                            uid = myUid
                        )
                    ),
                    allBlockedUsersInfo = emptyList(),
                    allChatMessages = emptyList(),
                    myUserIsConnected = true,
                    acceptedFoster = false,
                    finished = false,
                    addReview = false,
                    timestamp = Clock.System.now().toEpochMilliseconds()
                ).also {
                    inexistentChatFlag = true
                }
            val lastTimestamp: Long = chat.allChatMessages.maxOfOrNull { it.timestamp } ?: 0L

            when {
                // It doesn't exists
                inexistentChatFlag -> {
                    val result = insertChatInRemoteRepository(chat).first()
                    if (result is DatabaseResult.Success) {
                        insertChatInLocalRepository(chat) { isSuccess ->

                            if (isSuccess) {
                                insertChatInLocalCache(chat.id) {

                                    onChatFound(chat.id, lastTimestamp)
                                }
                            }
                        }
                    }
                }

                // Only exists in remote
                localChat == null -> {

                    val result = modifyChatInRemoteRepository(
                        chat.copy(
                            allActivistsInfo = chat.allActivistsInfo + ActivistInfo(
                                id = Clock.System.now().epochSeconds.toString() + rescueEventId + creatorId,
                                chatId = rescueEventId + creatorId,
                                uid = myUid
                            ),
                            timestamp = lastTimestamp
                        )
                    ).first()

                    if (result is DatabaseResult.Success) {
                        insertChatInLocalRepository(chat) { isSuccess ->

                            if (isSuccess) {
                                insertChatInLocalCache(chat.id) {

                                    onChatFound(chat.id, lastTimestamp)
                                }
                            }
                        }
                    }
                }

                // Exists in local and maybe in remote
                else -> {
                    onChatFound(chat.id, lastTimestamp)
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
