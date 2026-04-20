package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo
import com.findmeahometeam.reskiume.domain.model.chat.ActivistInfo
import com.findmeahometeam.reskiume.domain.model.chat.BlockedUserInfo
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import com.findmeahometeam.reskiume.domain.repository.remote.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull

class InsertChatInLocalRepository(
    private val localChatRepository: LocalChatRepository,
    private val authRepository: AuthRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        chat: Chat,
        onInsertChat: suspend (isSuccess: Boolean) -> Unit
    ) {
        val createdChat = chat.copy(savedBy = getMyUid())
        localChatRepository.insertChat(
            createdChat.toEntity(),
            onInsertChat = { rowId ->
                if (rowId > 0) {
                    var isSuccess = insertAllNonHumanAnimalsInfo(createdChat)

                    if (isSuccess) {
                        isSuccess = insertAllActivistsInfo(createdChat)

                        if (isSuccess && chat.allBlockedUsersInfo.isNotEmpty()) {
                            isSuccess = insertAllBlockedUsersInfo(createdChat)
                        }

                        if (isSuccess && chat.allChatMessages.isNotEmpty()) {
                            isSuccess = insertAllChatMessages(createdChat)
                        }
                    }
                    onInsertChat(isSuccess)
                } else {
                    onInsertChat(false)
                }
            }
        )
    }

    private suspend fun getMyUid(): String = authRepository.authState.firstOrNull()?.uid ?: ""

    private suspend fun insertAllNonHumanAnimalsInfo(chat: Chat): Boolean {

        var isSuccess = true
        chat.allNonHumanAnimalsInfo.forEach { nonHumanAnimalInfo: NonHumanAnimalInfo ->
            if (isSuccess) {
                localChatRepository.insertNonHumanAnimalInfoEntity(
                    nonHumanAnimalInfo.toEntity(),
                    onInsertNonHumanAnimalInfoEntity = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertChatInLocalRepository",
                                "insertAllNonHumanAnimalsInfo: inserted the non human animal info ${nonHumanAnimalInfo.nonHumanAnimalId} in the chat ${nonHumanAnimalInfo.chatId} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertChatInLocalRepository",
                                "insertAllNonHumanAnimalsInfo: failed to insert the non human animal info ${nonHumanAnimalInfo.nonHumanAnimalId} in the chat ${nonHumanAnimalInfo.chatId} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun insertAllActivistsInfo(chat: Chat): Boolean {

        var isSuccess = true
        chat.allActivistsInfo.forEach { activistInfo: ActivistInfo ->

            if (isSuccess) {
                localChatRepository.insertActivistInfoEntity(
                    activistInfo.toEntity(),
                    onInsertActivistInfoEntity = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertChatInLocalRepository",
                                "insertAllActivistsInfo: inserted the activist info ${activistInfo.uid} in the chat ${activistInfo.chatId} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertChatInLocalRepository",
                                "insertAllActivistsInfo: failed to insert the activist info ${activistInfo.uid} in the chat ${activistInfo.chatId} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun insertAllBlockedUsersInfo(chat: Chat): Boolean {

        var isSuccess = true
        chat.allBlockedUsersInfo.forEach { blockedUser: BlockedUserInfo ->

            if (isSuccess) {
                localChatRepository.insertBlockedUserInfoEntity(
                    blockedUser.toEntity(),
                    onInsertBlockedUserInfoEntity = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertChatInLocalRepository",
                                "insertAllBlockedUsersInfo: inserted the blocked user info ${blockedUser.uid} in the chat ${blockedUser.chatId} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertChatInLocalRepository",
                                "insertAllBlockedUsersInfo: failed to insert the blocked user info ${blockedUser.uid} in the chat ${blockedUser.chatId} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }

    private suspend fun insertAllChatMessages(chat: Chat): Boolean {

        var isSuccess = true
        chat.allChatMessages.forEach { chatMessage: ChatMessage ->

            if (isSuccess) {
                localChatRepository.insertChatMessageEntity(
                    chatMessage.toEntity(),
                    onInsertChatMessageEntity = { rowId ->
                        if (rowId > 0) {
                            log.d(
                                "InsertChatInLocalRepository",
                                "insertAllChatMessages: inserted the chat message ${chatMessage.id} in the chat ${chatMessage.chatId} in the local data source"
                            )
                        } else {
                            log.e(
                                "InsertChatInLocalRepository",
                                "insertAllChatMessages: failed to insert the chat message ${chatMessage.id} in the chat ${chatMessage.chatId} in the local data source"
                            )
                            isSuccess = false
                        }
                    }
                )
            }
        }
        return isSuccess
    }
}
