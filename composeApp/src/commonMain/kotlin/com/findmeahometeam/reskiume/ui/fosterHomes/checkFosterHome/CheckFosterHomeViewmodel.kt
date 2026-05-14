package com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.chat.ActivistInfo
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.IsFosterHomeInChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.IsNonHumanAnimalInChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckFosterHome
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckReviewsUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.UiReview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CheckFosterHomeViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    checkFosterHomeUtil: CheckFosterHomeUtil,
    private val checkActivistUtil: CheckActivistUtil,
    private val observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    checkReviewsUtil: CheckReviewsUtil,
    getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val isNonHumanAnimalInChatInLocalRepository: IsNonHumanAnimalInChatInLocalRepository,
    private val getChatFromLocalRepository: GetChatFromLocalRepository,
    private val isFosterHomeInChatInLocalRepository: IsFosterHomeInChatInLocalRepository,
    private val insertChatInRemoteRepository: InsertChatInRemoteRepository,
    private val insertChatInLocalRepository: InsertChatInLocalRepository,
    private val insertCacheInLocalRepository: InsertCacheInLocalRepository,
    private val log: Log
) : ViewModel() {

    private val fosterHomeId: String =
        saveStateHandleProvider.provideObjectRoute(CheckFosterHome::class).fosterHomeId

    private val ownerId: String =
        saveStateHandleProvider.provideObjectRoute(CheckFosterHome::class).ownerId

    private var chatId: String =
        saveStateHandleProvider.provideObjectRoute(CheckFosterHome::class).chatId

    private var myUid = ""

    private var myUser: User? = null

    val fosterHomeState: StateFlow<UiState<UiFosterHomeDetail>> =
        checkFosterHomeUtil.getFosterHomeFlow(
            fosterHomeId,
            ownerId,
            viewModelScope
        ).map { fosterHome: FosterHome? ->

            if (fosterHome == null) {
                return@map null
            }
            myUid = observeAuthStateInAuthDataSource().firstOrNull()?.uid ?: " "
            updateMyUserData()

            val owner = checkActivistUtil.getUser(
                activistUid = fosterHome.ownerId,
                myUserUid = myUid
            )
            if (owner == null) {
                null
            } else {
                UiFosterHomeDetail(
                    fosterHome = fosterHome.copy(
                        imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                            fosterHome.imageUrl
                        } else {
                            getImagePathForFileNameFromLocalDataSource(fosterHome.imageUrl)
                        }
                    ),
                    allResidentUiNonHumanAnimals = fosterHome.allResidentNonHumanAnimals.mapNotNull { residentNonHumanAnimal ->

                        checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                            residentNonHumanAnimal.nonHumanAnimalId,
                            residentNonHumanAnimal.caregiverId,
                            viewModelScope
                        ).firstOrNull()
                    },
                    owner = owner,
                    chatExist = isFosterHomeInChatInLocalRepository(fosterHomeId)
                )
            }
        }.toUiState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
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

    val allAvailableNonHumanAnimalsWhoNeedToBeRehomedFlow: StateFlow<List<NonHumanAnimal>> =
        getAllNonHumanAnimalsFromLocalRepository().map {
            it.mapNotNull { nonHumanAnimal ->
                if (nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                    && !isNonHumanAnimalInChatInLocalRepository(nonHumanAnimal.id)
                ) {
                    nonHumanAnimal
                } else {
                    null
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val reviewListFlowState: Flow<UiState<List<UiReview>>> =
        checkReviewsUtil.getReviewListFlow(ownerId).toUiState()

    fun isLoggedIn(): Boolean = myUser?.isLoggedIn == true

    fun canIStartTheChat(): Boolean = myUid != ownerId

    @OptIn(ExperimentalTime::class)
    fun findChat(
        fosterHomeId: String,
        ownerId: String,
        allNonHumanAnimals: List<NonHumanAnimal>,
        onChatFound: (chatId: String, lastTimestamp: Long) -> Unit
    ) {
        viewModelScope.launch {

            var insertChatFlag = false
            if (chatId.isEmpty() && myUser?.uid != ownerId) {
                chatId = fosterHomeId + myUid
            }
            val chat = getChatFromLocalRepository(chatId).firstOrNull()
                ?: Chat(
                    id = fosterHomeId + myUid,
                    fosterHomeId = fosterHomeId,
                    rescueEventId = "",
                    chatHolderId = ownerId,
                    allNonHumanAnimalsInfo = allNonHumanAnimals.map { nonHumanAnimal ->
                        NonHumanAnimalInfo(
                            nonHumanAnimalId = nonHumanAnimal.id,
                            chatId = fosterHomeId + myUid,
                            caregiverId = nonHumanAnimal.caregiverId
                        )
                    },
                    allActivistsInfo = listOf(
                        ActivistInfo(
                            id = Clock.System.now().epochSeconds.toString() + fosterHomeId + myUid,
                            chatId = fosterHomeId + myUid,
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
                    insertChatFlag = true
                }
            val lastTimestamp: Long = chat.allChatMessages.maxOfOrNull { it.timestamp } ?: 0L

            if (insertChatFlag) {
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
            } else {
                onChatFound(chat.id, lastTimestamp)
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
                    "CheckFosterHomeViewmodel",
                    "insertChatInLocalCache: $chatId added to local cache in section ${Section.CHATS}"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckFosterHomeViewmodel",
                    "insertChatInLocalCache: Error adding $chatId to local cache in section ${Section.CHATS}"
                )
            }
        }
    }
}

data class UiFosterHomeDetail(
    val fosterHome: FosterHome,
    val allResidentUiNonHumanAnimals: List<NonHumanAnimal>,
    val distance: Double? = null,
    val owner: User? = null,
    val chatExist: Boolean
)
