package com.findmeahometeam.reskiume.ui.chats.checkChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteMyChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteMyChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetAllChatMessagesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetAllChatMessagesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatMessageInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatMessageInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.ui.chats.checkAllMyChats.ManageChatUtil
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckChat
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimal.CheckNonHumanAnimalUtil
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.modifyRescueEvent.DeleteRescueEventUtil
import com.findmeahometeam.reskiume.ui.util.StringProvider
import com.findmeahometeam.reskiume.ui.util.fcm.SubscriptionManagerUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_chat_screen_accepted_foster
import reskiume.composeapp.generated.resources.check_chat_screen_chat_finished
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_friday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_monday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_saturday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_sunday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_thursday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_today
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_tuesday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_wednesday
import reskiume.composeapp.generated.resources.check_chat_screen_chat_message_date_yesterday
import reskiume.composeapp.generated.resources.check_chat_screen_some_non_human_animals
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class CheckChatViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    getChatFromRemoteRepository: GetChatFromRemoteRepository,
    private val getChatFromLocalRepository: GetChatFromLocalRepository,
    private val checkFosterHomeUtil: CheckFosterHomeUtil,
    private val checkRescueEventUtil: CheckRescueEventUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val checkNonHumanAnimalUtil: CheckNonHumanAnimalUtil,
    private val checkActivistUtil: CheckActivistUtil,
    private val getAllChatMessagesFromRemoteRepository: GetAllChatMessagesFromRemoteRepository,
    getAllChatMessagesFromLocalRepository: GetAllChatMessagesFromLocalRepository,
    private val insertChatMessageInLocalRepository: InsertChatMessageInLocalRepository,
    private val insertChatMessageInRemoteRepository: InsertChatMessageInRemoteRepository,
    private val getStringProvider: StringProvider,
    private val subscriptionManagerUtil: SubscriptionManagerUtil,
    private val modifyChatInRemoteRepository: ModifyChatInRemoteRepository,
    private val manageChatUtil: ManageChatUtil,
    private val deleteMyChatFromRemoteRepository: DeleteMyChatFromRemoteRepository,
    private val deleteMyChatFromLocalRepository: DeleteMyChatFromLocalRepository,
    private val getNonHumanAnimalFromRemoteRepository: GetNonHumanAnimalFromRemoteRepository,
    private val modifyNonHumanAnimalInRemoteRepository: ModifyNonHumanAnimalInRemoteRepository,
    private val deleteNonHumanAnimalFromLocalRepository: DeleteNonHumanAnimalFromLocalRepository,
    private val deleteCacheFromLocalRepository: DeleteCacheFromLocalRepository,
    private val deleteRescueEventUtil: DeleteRescueEventUtil,
    private val getFosterHomeFromLocalRepository: GetFosterHomeFromLocalRepository,
    private val modifyFosterHomeInLocalRepository: ModifyFosterHomeInLocalRepository,
    private val getFosterHomeFromRemoteRepository: GetFosterHomeFromRemoteRepository,
    private val modifyFosterHomeInRemoteRepository: ModifyFosterHomeInRemoteRepository,
    private val getRescueEventFromLocalRepository: GetRescueEventFromLocalRepository,
    private val modifyRescueEventInLocalRepository: ModifyRescueEventInLocalRepository,
    private val log: Log
) : ViewModel() {

    private val chatId: String =
        saveStateHandleProvider.provideObjectRoute(CheckChat::class).chatId

    private val lastTimestamp: Long =
        saveStateHandleProvider.provideObjectRoute(CheckChat::class).lastTimestamp

    private var myUid = ""

    private val oneDayInMilliseconds = 86400000

    private var job: Job? = null

    init {
        viewModelScope.launch {

            if (myUid.isEmpty()) {
                myUid = observeAuthStateInAuthDataSource().first()!!.uid
            }
            getChatFromRemoteRepository(
                chatId,
                myUid
            ).collect { updatedChat: Chat? ->

                if (updatedChat == null) {
                    job?.cancel()
                    return@collect
                }

                viewModelScope.launch {

                    val previousChat = getChatFromLocalRepository(chatId).firstOrNull()
                    if (previousChat == null) {
                        manageChatUtil.insertChatInLocalRepo(
                            chat = updatedChat,
                            myUid = myUid
                        )
                    } else {
                        manageChatUtil.modifyChatInLocalRepo(
                            updatedChat = updatedChat,
                            previousChat = previousChat,
                            myUid = myUid
                        )
                    }
                }

                // TODO check if user is blocked

                if (updatedChat.fosterHomeId.isNotEmpty()) {

                    if (updatedChat.chatHolderId == myUid
                        && updatedChat.allActivistsInfo.isEmpty()
                    ) {
                        deleteChatAsHolder(
                            shouldDeleteMyLocalChat = false,
                            fosterHomeId = updatedChat.fosterHomeId,
                            allNonHumanAnimals = updatedChat.allNonHumanAnimalsInfo.map {
                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    it.nonHumanAnimalId,
                                    it.caregiverId,
                                    viewModelScope
                                ).first()
                            },
                            onError = {
                                _uiState.value = UiState.Error()
                            }
                        )
                    }

                    viewModelScope.launch {

                        if (!updatedChat.finished
                            && updatedChat.acceptedFoster
                            && updatedChat.chatHolderId != myUid
                        ) {
                            val firstNonHumanAnimal =
                                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                                    updatedChat.allNonHumanAnimalsInfo.first().nonHumanAnimalId,
                                    updatedChat.allNonHumanAnimalsInfo.first().caregiverId,
                                    viewModelScope
                                ).firstOrNull()

                            if (firstNonHumanAnimal?.nonHumanAnimalState != NonHumanAnimalState.REHOMED) {

                                addFosterHomeResidentsInLocalRepo(
                                    fosterHomeId = updatedChat.fosterHomeId,
                                    allNonHumanAnimalsInfo = updatedChat.allNonHumanAnimalsInfo
                                )
                            }
                        }
                    }
                }
            }
        }
        viewModelScope.launch {

            if (myUid.isEmpty()) {
                myUid = observeAuthStateInAuthDataSource().first()!!.uid
            }
            subscribeToChatIfNecessary(myUid) {

                job = viewModelScope.launch {

                    getAllChatMessagesFromRemoteRepository(
                        chatId,
                        lastTimestamp
                    ).collect { allChatMessages ->

                        allChatMessages.forEach { chatMessage ->
                            insertChatMessageInLocalRepository(chatMessage) {
                                log.d(
                                    "CheckChatViewmodel",
                                    "init: Inserted the message: ${chatMessage.message}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiChatDetailState: StateFlow<UiState<UiChatDetail>> =
        getChatFromLocalRepository(chatId)
            .flatMapConcat { chat: Chat? ->

                if (chat == null) {
                    return@flatMapConcat flowOf()
                }
                var avatar: String
                var title: String

                if (chat.fosterHomeId.isNotEmpty()) {

                    val fosterHome = checkFosterHomeUtil.getFosterHomeFlow(
                        chat.fosterHomeId,
                        chat.chatHolderId,
                        viewModelScope
                    ).firstOrNull()

                    avatar = fosterHome?.imageUrl ?: ""
                    title = fosterHome?.title ?: ""
                } else {

                    val rescueEvent = checkRescueEventUtil.getRescueEventFlow(
                        chat.rescueEventId,
                        chat.chatHolderId,
                        viewModelScope
                    ).firstOrNull()

                    avatar = rescueEvent?.imageUrl ?: ""
                    title = rescueEvent?.title ?: ""
                }

                var allNonHumanAnimals = chat.allNonHumanAnimalsInfo.map {

                    checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                        it.nonHumanAnimalId,
                        it.caregiverId,
                        viewModelScope
                    ).first()
                }
                val chatHolderId = checkActivistUtil.getUser(
                    activistUid = chat.chatHolderId,
                    myUserUid = chat.savedBy
                )?.uid ?: ""

                val allActivists = chat.allActivistsInfo.mapNotNull {

                    if (it.uid == myUid) {
                        null
                    } else {
                        checkActivistUtil.getUser(
                            activistUid = it.uid,
                            myUserUid = chat.savedBy
                        )
                    }
                }
                val myUsername = checkActivistUtil.getUser(
                    activistUid = myUid,
                    myUserUid = myUid
                )!!.username

                flowOf(
                    UiState.Success(
                        UiChatDetail(
                            chatId = chatId,
                            avatar = if (avatar.isEmpty()) {
                                avatar
                            } else {
                                getImagePathForFileNameFromLocalDataSource(avatar)
                            },
                            title = title,
                            allNonHumanAnimals = allNonHumanAnimals,
                            chatHolderId = chatHolderId,
                            amIChatHolder = chatHolderId == myUid,
                            fosterHomeId = chat.fosterHomeId,
                            rescueEventId = chat.rescueEventId,
                            allActivists = allActivists,
                            myUsername = myUsername,
                            finished = if (chat.fosterHomeId.isNotEmpty()
                                && chatHolderId == myUid
                                && allActivists.isEmpty()
                            ) {
                                true
                            } else {
                                chat.finished
                            },
                            addReview = chat.addReview
                        )
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allChatMessagesState: StateFlow<UiState<List<UiChatMessage>>> =
        getAllChatMessagesFromLocalRepository(chatId)
            .map { list ->

                var previousChatMessage: ChatMessage? = null
                var currentChatMessage: ChatMessage?
                list.map { chatMessage ->

                    currentChatMessage = chatMessage
                    val sender = if (chatMessage.senderId.isEmpty()) {
                        null
                    } else {
                        checkActivistUtil.getUser(
                            activistUid = chatMessage.senderId,
                            myUserUid = myUid
                        )
                    }
                    UiChatMessage(
                        message = chatMessage.message,
                        senderId = chatMessage.senderId,
                        senderName = sender?.username ?: "",
                        avatar = if (chatMessage.senderId != myUid && chatMessage.senderId.isNotEmpty()) {

                            sender?.image?.let {
                                if (it.isEmpty()) {
                                    it
                                } else {
                                    getImagePathForFileNameFromLocalDataSource(it)
                                }
                            } ?: ""
                        } else {
                            ""
                        },
                        isMyMessage = chatMessage.senderId == myUid,
                        hour = getTime(chatMessage.timestamp),
                        date = if (
                            previousChatMessage == null
                            || currentChatMessage.timestamp - previousChatMessage.timestamp >= oneDayInMilliseconds
                            || getTime(currentChatMessage.timestamp).take(2).toInt()
                            < getTime(previousChatMessage.timestamp).take(2).toInt() // Is a new day
                        ) {
                            getDate(currentChatMessage.timestamp)
                        } else {
                            ""
                        }
                    ).also {
                        previousChatMessage = currentChatMessage
                    }
                }
            }
            .toUiState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading()
            )

    private val _uiState: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Idle())
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    private suspend fun subscribeToChatIfNecessary(
        myUid: String,
        onComplete: () -> Unit
    ) {
        val myUser = checkActivistUtil.getUser(
            activistUid = myUid,
            myUserUid = myUid
        )!!
        if (!myUser.subscriptions.any { it.topic == chatId }) {

            subscriptionManagerUtil.subscribeToTopic(
                user = myUser,
                topic = chatId,
                coroutineScope = viewModelScope,
                onComplete = onComplete
            )
        } else {
            onComplete()
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun getDate(timestamp: Long): String {
        val currentDate =
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val messageDate =
            Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

        val daysBetween = currentDate.toEpochDays() - messageDate.toEpochDays()
        return when (daysBetween) {

            // Today
            0L -> {
                getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_today)
            }
            // Yesterday
            1L -> {
                getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_yesterday)
            }
            // Within the last 6 days
            in 2..6 -> {
                when (messageDate.dayOfWeek) {
                    DayOfWeek.MONDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_monday)
                    DayOfWeek.TUESDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_tuesday)
                    DayOfWeek.WEDNESDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_wednesday)
                    DayOfWeek.THURSDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_thursday)
                    DayOfWeek.FRIDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_friday)
                    DayOfWeek.SATURDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_saturday)
                    DayOfWeek.SUNDAY -> getStringProvider.getStringResource(Res.string.check_chat_screen_chat_message_date_sunday)
                }
            }
            // Older than a week (DD/MM/YY)
            else -> {
                val dd = messageDate.day.toString().padStart(2, '0')
                val mm = messageDate.month.number.toString().padStart(2, '0')
                val yy = messageDate.year.toString().takeLast(2)

                "$dd/$mm/$yy"
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val hh = dateTime.hour.toString().padStart(2, '0')
        val mm = dateTime.minute.toString().padStart(2, '0')
        return "$hh:$mm"
    }

    @OptIn(ExperimentalTime::class)
    fun sendMessage(
        message: String,
        isAppMessage: Boolean = false,
        onSuccess: () -> Unit = {},
    ) {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val chatMessage = ChatMessage(
            id = "$timestamp${myUid}",
            chatId = chatId,
            message = message,
            senderId = if (isAppMessage) "" else myUid,
            timestamp = timestamp
        )
        viewModelScope.launch {
            val result = insertChatMessageInRemoteRepository(chatMessage).first()
            if (result is DatabaseResult.Success) {
                onSuccess()
            }
        }
    }

    fun leaveTheCurrentChat(
        fosterHomeId: String,
        allNonHumanAnimalAndCaregiverIdPairs: List<Pair<String, String>>
    ) {
        viewModelScope.launch {

            _uiState.value = UiState.Loading()

            removeMyUserInChat {

                unsubscribeFromTopic {

                    deleteMyChatFromLocalRepo {

                        deleteCacheObjectInLocalRepo(chatId) {

                            if (fosterHomeId.isNotEmpty()) {

                                updateNonHumanAnimalsToNeedsToBeRehomedIfFosterHome(
                                    fosterHomeId,
                                    allNonHumanAnimalAndCaregiverIdPairs
                                ) {
                                    _uiState.value = UiState.Success(Unit)
                                }
                            } else {
                                _uiState.value = UiState.Success(Unit)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun removeMyUserInChat(onSuccess: suspend () -> Unit) {

        val chat = getChatFromLocalRepository(chatId).first()!!
        val updatedChat = chat.copy(
            allActivistsInfo = chat.allActivistsInfo.filterNot { it.uid == myUid },
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        val modifyChatInRemoteResult = modifyChatInRemoteRepository(updatedChat).first()
        if (modifyChatInRemoteResult is DatabaseResult.Success) {
            onSuccess()
        }
    }

    fun unsubscribeFromTopic(onComplete: () -> Unit = {}) {
        viewModelScope.launch {

            val myUser = checkActivistUtil.getUser(
                activistUid = myUid,
                myUserUid = myUid
            )!!
            subscriptionManagerUtil.unsubscribeFromTopic(
                myUser,
                chatId,
                viewModelScope
            ) {
                onComplete()
            }
        }
    }

    private fun deleteMyChatFromLocalRepo(
        onSuccess: suspend () -> Unit = {}
    ) {
        viewModelScope.launch {

            deleteMyChatFromLocalRepository(chatId) { rowsDeleted ->

                if (rowsDeleted > 0) {
                    log.d(
                        "CheckChatViewmodel",
                        "deleteMyChatFromLocalRepo: Successfully deleted the chat $chatId from the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CheckChatViewmodel",
                        "deleteMyChatFromLocalRepo: Something went wrong deleting the chat $chatId from the local data source"
                    )
                }
            }
        }
    }

    private fun updateNonHumanAnimalsToNeedsToBeRehomedIfFosterHome(
        fosterHomeId: String,
        allNonHumanAnimalAndCaregiverIdPairs: List<Pair<String, String>>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val allNonHumanAnimals = allNonHumanAnimalAndCaregiverIdPairs.map {
                checkNonHumanAnimalUtil.getNonHumanAnimalFlow(
                    nonHumanAnimalId = it.first,
                    caregiverId = it.second,
                    coroutineScope = viewModelScope
                ).first()
            }
            if (allNonHumanAnimals.any { it.nonHumanAnimalState != NonHumanAnimalState.NEEDS_TO_BE_REHOMED }) {

                viewModelScope.launch {
                    var counter = 0
                    allNonHumanAnimals.forEach {

                        if (it.nonHumanAnimalState != NonHumanAnimalState.NEEDS_TO_BE_REHOMED) {

                            val remoteNonHumanAnimal = getNonHumanAnimalFromRemoteRepository(
                                it.id,
                                it.caregiverId
                            ).first()!!

                            val updatedNonHumanAnimal = it.copy(
                                fosterHomeId = "",
                                imageUrl = remoteNonHumanAnimal.imageUrl,
                                nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                            )
                            modifyNonHumanAnimalInRemoteRepo(updatedNonHumanAnimal) {
                                viewModelScope.launch {

                                    modifyFosterHomeInLocalRepo(
                                        fosterHomeId,
                                        updatedNonHumanAnimal.copy(imageUrl = it.imageUrl)
                                    ) {
                                        if (counter == allNonHumanAnimals.size - 1) {
                                            onComplete()
                                        } else {
                                            counter += 1
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                log.d(
                    "CheckChatViewmodel",
                    "updateNonHumanAnimalsToNeedsToBeRehomedIfFosterHome: No non human animals need to be updated"
                )
                onComplete()
            }
        }
    }

    private suspend fun modifyNonHumanAnimalInRemoteRepo(
        nonHumanAnimal: NonHumanAnimal,
        onSuccess: () -> Unit
    ) {
        modifyNonHumanAnimalInRemoteRepository(nonHumanAnimal) { result ->

            if (result is DatabaseResult.Success) {
                log.d(
                    "CheckChatViewmodel",
                    "modifyNonHumanAnimalInRemoteRepo: Successfully updated the non human animal ${nonHumanAnimal.id} with state ${nonHumanAnimal.nonHumanAnimalState} in the remote data source"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckChatViewmodel",
                    "modifyNonHumanAnimalInRemoteRepo: Something went wrong updating the non human animal ${nonHumanAnimal.id} in the remote data source"
                )
            }
        }
    }

    private fun manageFosterHomesInRepos(
        fosterHomeId: String,
        nonHumanAnimal: NonHumanAnimal,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            modifyFosterHomeInLocalRepo(
                fosterHomeId = fosterHomeId,
                nonHumanAnimal = nonHumanAnimal,
                onError = onError
            ) {
                viewModelScope.launch {

                    modifyFosterHomeInRemoteRepo(
                        fosterHomeId = fosterHomeId,
                        nonHumanAnimal = nonHumanAnimal,
                        onError = onError
                    ) {
                        onSuccess()
                    }
                }
            }
        }
    }

    private suspend fun modifyFosterHomeInRemoteRepo(
        fosterHomeId: String,
        nonHumanAnimal: NonHumanAnimal,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        val fosterHome = getFosterHomeFromRemoteRepository(fosterHomeId).first()!!

        modifyFosterHomeInRemoteRepository(
            isNonHumanAnimalSaved = nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.SAVED,
            updatedFosterHome = fosterHome.copy(
                allResidentNonHumanAnimals = if (nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.REHOMED) {
                    fosterHome.allResidentNonHumanAnimals.plus(
                        ResidentNonHumanAnimalForFosterHome(
                            nonHumanAnimalId = nonHumanAnimal.id,
                            caregiverId = nonHumanAnimal.caregiverId,
                            fosterHomeId = fosterHomeId
                        )
                    )
                } else {
                    fosterHome.allResidentNonHumanAnimals.filter { it.nonHumanAnimalId != nonHumanAnimal.id }
                }
            ),
            previousFosterHome = fosterHome,
            viewModelScope
        ) { result ->

            if (result is DatabaseResult.Success) {
                log.d(
                    "CheckChatViewmodel",
                    "modifyFosterHomeInRemoteRepo: Successfully modified the foster home $fosterHomeId to modify the non human animal ${nonHumanAnimal.id} with state ${nonHumanAnimal.nonHumanAnimalState} in the remote data source"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckChatViewmodel",
                    "modifyFosterHomeInRemoteRepo: Something went wrong modifying the foster home $fosterHomeId to modify the non human animal ${nonHumanAnimal.id} with state ${nonHumanAnimal.nonHumanAnimalState} in the remote data source"
                )
                onError()
            }
        }
    }

    private suspend fun modifyFosterHomeInLocalRepo(
        fosterHomeId: String,
        nonHumanAnimal: NonHumanAnimal,
        onError: () -> Unit = {},
        onSuccess: () -> Unit = {}
    ) {
        val fosterHome = getFosterHomeFromLocalRepository(fosterHomeId).first()!!

        modifyFosterHomeInLocalRepository(
            isNonHumanAnimalSaved = nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.SAVED,
            updatedFosterHome = fosterHome.copy(
                allResidentNonHumanAnimals = if (nonHumanAnimal.nonHumanAnimalState == NonHumanAnimalState.REHOMED) {
                    fosterHome.allResidentNonHumanAnimals.plus(
                        ResidentNonHumanAnimalForFosterHome(
                            nonHumanAnimalId = nonHumanAnimal.id,
                            caregiverId = nonHumanAnimal.caregiverId,
                            fosterHomeId = fosterHomeId
                        )
                    )
                } else {
                    fosterHome.allResidentNonHumanAnimals.filter { it.nonHumanAnimalId != nonHumanAnimal.id }
                }
            ),
            previousFosterHome = fosterHome,
            coroutineScope = viewModelScope
        ) { isSuccess ->

            if (isSuccess) {
                log.d(
                    "CheckChatViewmodel",
                    "modifyFosterHomeInLocalRepo: Successfully modified the foster home $fosterHomeId in the local data source"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckChatViewmodel",
                    "modifyFosterHomeInLocalRepo: Something went wrong modifying the foster home $fosterHomeId in the local data source"
                )
                onError()
            }
        }
    }

    fun leaveTheDeletedChat() {

        _uiState.value = UiState.Loading()

        unsubscribeFromTopic {

            deleteMyChatFromLocalRepo {

                deleteCacheObjectInLocalRepo(chatId) {

                    _uiState.value = UiState.Success(Unit)
                }
            }
        }
    }

    fun declineFoster(
        myUsername: String,
        fosterHomeId: String,
        allNonHumanAnimals: List<NonHumanAnimal>
    ) {
        viewModelScope.launch {

            _uiState.value = UiState.Loading()

            updateChatAsFinished(
                myUsername = myUsername,
                shouldReview = false
            ) {
                viewModelScope.launch {

                    unsubscribeFromTopic {

                        deleteChatAsHolder(
                            fosterHomeId = fosterHomeId,
                            allNonHumanAnimals = allNonHumanAnimals,
                            onError = {
                                _uiState.value = UiState.Error()
                            },
                            onComplete = {
                                _uiState.value = UiState.Success(Unit)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun deleteChatAsHolder(
        shouldDeleteMyLocalChat: Boolean = true,
        fosterHomeId: String,
        allNonHumanAnimals: List<NonHumanAnimal>,
        onError: () -> Unit,
        onComplete: () -> Unit = {}
    ) {
        val isFosterHome = fosterHomeId.isNotEmpty()
        val allNonHumanAnimalIds = allNonHumanAnimals.map { it.id }

        viewModelScope.launch {

            deleteMyChatFromRemoteRepo {

                if (shouldDeleteMyLocalChat) {

                    deleteMyChatFromLocalRepo {

                        deleteCacheObjectInLocalRepo(chatId) {

                            if (isFosterHome) {

                                manageNonHumanAnimalsInFosterHomes(
                                    fosterHomeId = fosterHomeId,
                                    allNonHumanAnimals = allNonHumanAnimals,
                                    onError = onError
                                ) {
                                    deleteAllCachedObjectsInLocalRepo(allNonHumanAnimalIds) {

                                        onComplete()
                                    }
                                }
                            }
                        }
                    }
                } else if (isFosterHome) {

                    manageNonHumanAnimalsInFosterHomes(
                        fosterHomeId = fosterHomeId,
                        allNonHumanAnimals = allNonHumanAnimals,
                        onError = onError
                    ) {
                        deleteAllCachedObjectsInLocalRepo(allNonHumanAnimalIds) {

                            onComplete()
                        }
                    }
                }
            }
        }
    }

    private fun manageNonHumanAnimalsInFosterHomes(
        fosterHomeId: String,
        allNonHumanAnimals: List<NonHumanAnimal>,
        onError: () -> Unit,
        onComplete: () -> Unit
    ) {
        var counter = 0
        allNonHumanAnimals.forEach {

            manageFosterHomesInRepos(
                fosterHomeId = fosterHomeId,
                nonHumanAnimal = it.copy(nonHumanAnimalState = NonHumanAnimalState.NEEDS_TO_BE_REHOMED),
                onError = onError
            ) {
                if (counter == allNonHumanAnimals.size - 1) {
                    onComplete()
                } else {
                    counter += 1
                }
            }
        }
    }

    private suspend fun addFosterHomeResidentsInLocalRepo(
        fosterHomeId: String,
        allNonHumanAnimalsInfo: List<NonHumanAnimalInfo>
    ) {
        val fosterHome = getFosterHomeFromLocalRepository(fosterHomeId).first()!!
        var allResidentNonHumanAnimals = fosterHome.allResidentNonHumanAnimals
        allNonHumanAnimalsInfo.forEach { nonHumanAnimalInfo ->

            allResidentNonHumanAnimals = allResidentNonHumanAnimals.plus(
                ResidentNonHumanAnimalForFosterHome(
                    nonHumanAnimalId = nonHumanAnimalInfo.nonHumanAnimalId,
                    caregiverId = nonHumanAnimalInfo.caregiverId,
                    fosterHomeId = fosterHomeId
                )
            )
        }

        modifyFosterHomeInLocalRepository(
            updatedFosterHome = fosterHome.copy(allResidentNonHumanAnimals = allResidentNonHumanAnimals),
            previousFosterHome = fosterHome,
            coroutineScope = viewModelScope
        ) { isUpdated ->

            if (isUpdated) {
                log.d(
                    "CheckChatViewmodel",
                    "addFosterHomeResidentsInLocalRepo: Successfully added the non human animals in the foster home $fosterHomeId in the local data source"
                )
            } else {
                log.e(
                    "CheckChatViewmodel",
                    "addFosterHomeResidentsInLocalRepo: Something went wrong adding the non human animals in the foster home $fosterHomeId in the local data source"
                )
            }
        }
    }

    private fun deleteMyChatFromRemoteRepo(onSuccess: suspend () -> Unit) {
        viewModelScope.launch {

            val result = deleteMyChatFromRemoteRepository(myUid, chatId).first()
            if (result is DatabaseResult.Success) {
                log.d(
                    "CheckChatViewmodel",
                    "deleteMyChatFromRemoteRepo: Successfully deleted the chat $chatId from the remote data source"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckChatViewmodel",
                    "deleteMyChatFromRemoteRepo: Something went wrong deleting the chat $chatId from the remote data source"
                )
            }
        }
    }

    private fun deleteNonHumanAnimalFromLocalRepo(
        allNonHumanAnimalIds: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            allNonHumanAnimalIds.forEach { nonHumanAnimalId ->

                deleteNonHumanAnimalFromLocalRepository(
                    nonHumanAnimalId,
                ) { rowsDeleted ->

                    if (rowsDeleted > 0) {
                        log.d(
                            "CheckChatViewmodel",
                            "deleteNonHumanAnimalFromLocalRepo: Successfully deleted the non human animal $nonHumanAnimalId from the local data source"
                        )
                        onSuccess()
                    } else {
                        log.e(
                            "CheckChatViewmodel",
                            "deleteNonHumanAnimalFromLocalRepo: Something went wrong deleting the non human animal $nonHumanAnimalId from the local data source"
                        )
                    }
                }
            }
        }
    }

    private fun deleteCacheObjectInLocalRepo(
        cachedObjectId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {

            deleteCacheFromLocalRepository(cachedObjectId) { rowsDeleted: Int ->

                if (rowsDeleted > 0) {
                    log.d(
                        "CheckChatViewmodel",
                        "deleteCacheObjectInLocalRepo: Successfully deleted the cache object id $cachedObjectId from the local data source"
                    )
                    onSuccess()
                } else {
                    log.e(
                        "CheckChatViewmodel",
                        "deleteCacheObjectInLocalRepo: Something went wrong deleting the cache for object id $cachedObjectId from the local data source"
                    )
                }
            }
        }
    }

    private fun deleteAllCachedObjectsInLocalRepo(
        allCachedObjectIds: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            var counter = 0

            allCachedObjectIds.forEach { cachedObjectId ->

                deleteCacheObjectInLocalRepo(
                    cachedObjectId
                ) {
                    if (counter == allCachedObjectIds.size - 1) {
                        onSuccess()
                    } else {
                        counter += 1
                    }
                }
            }
        }
    }

    fun acceptFoster(
        fosterHomeId: String,
        myUsername: String,
        allNonHumanAnimals: List<NonHumanAnimal>,
        nonHumanAnimalState: NonHumanAnimalState
    ) {
        viewModelScope.launch {

            sendMessage(
                message = getStringProvider.getStringResource(
                    Res.string.check_chat_screen_accepted_foster,
                    myUsername,
                    if (allNonHumanAnimals.size == 1) {
                        allNonHumanAnimals.first().name
                    } else {
                        getStringProvider.getStringResource(
                            Res.string.check_chat_screen_some_non_human_animals,
                            allNonHumanAnimals.subList(0, allNonHumanAnimals.size - 1)
                                .joinToString(", ") { it.name },
                            allNonHumanAnimals.last().name
                        )
                    }
                ),
                isAppMessage = true
            )
            addAcceptedFosterToChatInRemoteRepo {

                updateNonHumanAnimalsToStateInFosterHome(
                    fosterHomeId = fosterHomeId,
                    allNonHumanAnimals = allNonHumanAnimals,
                    nonHumanAnimalState = nonHumanAnimalState,
                    onError = {
                        _uiState.value = UiState.Error()
                    }
                )
            }
        }
    }

    private fun addAcceptedFosterToChatInRemoteRepo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val chat = getChatFromLocalRepository(chatId).first()!!.copy(
                acceptedFoster = true,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            val modifyChatInRemoteResult = modifyChatInRemoteRepository(chat).first()
            if (modifyChatInRemoteResult is DatabaseResult.Success) {
                onSuccess()
            }
        }
    }

    private fun updateNonHumanAnimalsToStateInFosterHome(
        fosterHomeId: String,
        allNonHumanAnimals: List<NonHumanAnimal>,
        nonHumanAnimalState: NonHumanAnimalState,
        onError: () -> Unit,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            var counter = 0
            allNonHumanAnimals.forEach { nonHumanAnimal ->

                val remoteNonHumanAnimal = getNonHumanAnimalFromRemoteRepository(
                    nonHumanAnimal.id,
                    nonHumanAnimal.caregiverId
                ).first()!!

                val updatedNonHumanAnimal = nonHumanAnimal.copy(
                    fosterHomeId = if (nonHumanAnimalState == NonHumanAnimalState.REHOMED) {
                        fosterHomeId
                    } else {
                        ""
                    },
                    imageUrl = remoteNonHumanAnimal.imageUrl,
                    nonHumanAnimalState = nonHumanAnimalState
                )
                manageFosterHomesInRepos(
                    fosterHomeId = fosterHomeId,
                    nonHumanAnimal = updatedNonHumanAnimal,
                    onError = onError
                ) {
                    if (counter == allNonHumanAnimals.size - 1) {
                        onComplete()
                    } else {
                        counter += 1
                    }
                }
            }
        }
    }

    fun onClickEvaluateFromChatHolder(
        fosterHomeId: String,
        allNonHumanAnimals: List<NonHumanAnimal>
    ) {
        _uiState.value = UiState.Loading()
        unsubscribeFromTopic {

            if (fosterHomeId.isEmpty()) {
                _uiState.value = UiState.Success(Unit)
                return@unsubscribeFromTopic
            }

            val allNonHumanAnimalIds = allNonHumanAnimals.map { it.id }
            deleteNonHumanAnimalFromLocalRepo(allNonHumanAnimalIds) {

                deleteAllCachedObjectsInLocalRepo(allNonHumanAnimalIds) {
                    _uiState.value = UiState.Success(Unit)
                }
            }
        }
    }

    fun onClickEvaluateFromActivist(
        fosterHomeId: String,
        rescueEventId: String,
        allNonHumanAnimals: List<NonHumanAnimal>
    ) {
        _uiState.value = UiState.Loading()
        unsubscribeFromTopic {

            viewModelScope.launch {
                if (allNonHumanAnimals.any {
                        it.nonHumanAnimalState != NonHumanAnimalState.NEEDS_TO_BE_REHOMED
                                && it.nonHumanAnimalState != NonHumanAnimalState.SAVED
                    }
                ) {
                    if (fosterHomeId.isNotEmpty()) {

                        updateFosterHomeWithLastNonHumanAnimalState(
                            allNonHumanAnimals = allNonHumanAnimals,
                            fosterHomeId = fosterHomeId,
                            onSuccess = {
                                _uiState.value = UiState.Success(Unit)
                            },
                            onError = {
                                _uiState.value = UiState.Error()
                            }
                        )
                    } else {

                        val remoteNonHumanAnimal = getNonHumanAnimalFromRemoteRepository(
                            allNonHumanAnimals.first().id,
                            allNonHumanAnimals.first().caregiverId
                        ).first()!!

                        modifyRescueEventWithFinalNonHumanAnimalStateInLocalRepo(
                            rescueEventId = rescueEventId,
                            nonHumanAnimalState = remoteNonHumanAnimal.nonHumanAnimalState,
                            allNonHumanAnimals = allNonHumanAnimals,
                            onError = {
                                _uiState.value = UiState.Error()
                            },
                            onSuccess = {
                                _uiState.value = UiState.Success(Unit)
                            }
                        )
                    }
                } else {
                    log.d(
                        "CheckChatViewmodel",
                        "onClickEvaluateFromActivist: No non human animals need to be updated"
                    )
                    _uiState.value = UiState.Success(Unit)
                }
            }
        }
    }

    private suspend fun updateFosterHomeWithLastNonHumanAnimalState(
        allNonHumanAnimals: List<NonHumanAnimal>,
        fosterHomeId: String,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        allNonHumanAnimals.forEach { nonHumanAnimal ->

            val remoteNonHumanAnimal = getNonHumanAnimalFromRemoteRepository(
                nonHumanAnimal.id,
                nonHumanAnimal.caregiverId
            ).first()!!

            // Update the forster home with the last non human animal state
            modifyFosterHomeInLocalRepo(
                fosterHomeId = fosterHomeId,
                nonHumanAnimal = remoteNonHumanAnimal,
                onError = onError,
                onSuccess = onSuccess
            )
        }
    }

    fun finishAndEvaluate(
        fosterHomeId: String,
        myUsername: String,
        allNonHumanAnimals: List<NonHumanAnimal>,
        nonHumanAnimalState: NonHumanAnimalState,
        rescueEventId: String,
        creatorId: String,
        isThereAnyActivist: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading()

            updateChatAsFinished(
                myUsername = myUsername,
                shouldReview = isThereAnyActivist
            ) {
                unsubscribeFromTopic {

                    deleteMyChatFromRemoteRepo {

                        if (rescueEventId.isNotEmpty()) {

                            deleteRescueEventUtil.deleteRescueEvent(
                                id = rescueEventId,
                                creatorId = creatorId,
                                nonHumanAnimalState = nonHumanAnimalState,
                                coroutineScope = viewModelScope,
                                deleteOnLocal = !isThereAnyActivist,
                                deleteOnRemote = true,
                                onError = {
                                    _uiState.value = UiState.Error()
                                },
                                onComplete = {

                                    viewModelScope.launch {

                                        if (isThereAnyActivist) {

                                            modifyRescueEventWithFinalNonHumanAnimalStateInLocalRepo(
                                                rescueEventId = rescueEventId,
                                                nonHumanAnimalState = nonHumanAnimalState,
                                                allNonHumanAnimals = allNonHumanAnimals,
                                                onError = {
                                                    _uiState.value = UiState.Error()
                                                },
                                                onSuccess = {
                                                    _uiState.value = UiState.Success(Unit)
                                                }
                                            )
                                        } else {
                                            deleteMyChatFromLocalRepo {

                                                deleteCacheObjectInLocalRepo(chatId) {

                                                    _uiState.value = UiState.Success(Unit)
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        } else {
                            updateNonHumanAnimalsToStateInFosterHome(
                                fosterHomeId = fosterHomeId,
                                allNonHumanAnimals = allNonHumanAnimals,
                                nonHumanAnimalState = nonHumanAnimalState,
                                onError = {
                                    _uiState.value = UiState.Error()
                                },
                                onComplete = {
                                    _uiState.value = UiState.Success(Unit)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateChatAsFinished(
        myUsername: String,
        shouldReview: Boolean,
        onSuccess: () -> Unit
    ) {
        val chat = getChatFromLocalRepository(chatId).first()!!.copy(
            finished = true,
            addReview = shouldReview,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        val modifyChatInRemoteResult = modifyChatInRemoteRepository(chat).first()
        if (modifyChatInRemoteResult is DatabaseResult.Success) {
            sendMessage(
                message = getStringProvider.getStringResource(
                    Res.string.check_chat_screen_chat_finished,
                    myUsername
                ),
                isAppMessage = true
            ) {
                onSuccess()
            }
        }
    }

    private suspend fun modifyRescueEventWithFinalNonHumanAnimalStateInLocalRepo(
        rescueEventId: String,
        nonHumanAnimalState: NonHumanAnimalState,
        allNonHumanAnimals: List<NonHumanAnimal>,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        val rescueEvent = getRescueEventFromLocalRepository(rescueEventId).first()!!
        val allNonHumanAnimalIds = allNonHumanAnimals.map { it.id }
        modifyRescueEventInLocalRepository(
            isNonHumanAnimalSaved = nonHumanAnimalState == NonHumanAnimalState.SAVED,
            updatedRescueEvent = rescueEvent.copy(
                allNonHumanAnimalsToRescue =
                    rescueEvent.allNonHumanAnimalsToRescue.filter {
                        !allNonHumanAnimalIds.contains(
                            it.nonHumanAnimalId
                        )
                    }
            ),
            previousRescueEvent = rescueEvent,
            viewModelScope
        ) { isUpdated ->

            if (isUpdated) {
                log.d(
                    "CheckChatViewmodel",
                    "modifyRescueEventWithFinalNonHumanAnimalStateInLocalRepo: Successfully modified the non human animals with state $nonHumanAnimalState in the rescue event id $rescueEventId in the local data source"
                )
                onSuccess()
            } else {
                log.e(
                    "CheckChatViewmodel",
                    "modifyRescueEventWithFinalNonHumanAnimalStateInLocalRepo: Something went wrong modifying the non human animals with state $nonHumanAnimalState in the rescue event id $rescueEventId in the local data source"
                )
                onError()
            }
        }
    }
}

data class UiChatDetail(
    val chatId: String,
    val avatar: String,
    val title: String,
    val allNonHumanAnimals: List<NonHumanAnimal>,
    val chatHolderId: String,
    val amIChatHolder: Boolean,
    val fosterHomeId: String = "",
    val rescueEventId: String = "",
    val allActivists: List<User>,
    val myUsername: String,
    val finished: Boolean,
    val addReview: Boolean
)

data class UiChatMessage(
    val message: String,
    val senderId: String,
    val senderName: String,
    val avatar: String,
    val isMyMessage: Boolean = false,
    val hour: String,
    val date: String = ""
)
