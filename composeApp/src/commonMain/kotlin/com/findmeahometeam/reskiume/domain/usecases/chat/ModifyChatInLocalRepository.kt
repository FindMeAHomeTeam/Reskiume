package com.findmeahometeam.reskiume.domain.usecases.chat

import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.chat.BlockedUserInfo
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository

class ModifyChatInLocalRepository(
    private val localChatRepository: LocalChatRepository,
    private val log: Log
) {
    suspend operator fun invoke(
        updatedChat: Chat,
        previousChat: Chat,
        onModifyChat: suspend (isUpdated: Boolean) -> Unit
    ) {
        localChatRepository.modifyChat(
            updatedChat.toEntity(),
            onModifyChat = { rowsUpdated ->
                if (rowsUpdated > 0) {
                    var isSuccess =
                        manageAllNonHumanAnimalsInfo(
                            previousChat,
                            updatedChat
                        )

                    if (isSuccess) {
                        isSuccess = manageAllActivistsInfo(previousChat, updatedChat)

                        if (isSuccess) {
                            isSuccess = manageAllBlockedUsersInfo(previousChat, updatedChat)
                        }
                    }
                    onModifyChat(isSuccess)
                } else {
                    onModifyChat(false)
                }
            }
        )
    }
    
    private suspend fun manageAllNonHumanAnimalsInfo(
        previousChat: Chat,
        updatedChat: Chat
    ): Boolean {
        var isSuccess = true

        val previousAllNonHumanAnimalsInfo =
            previousChat.allNonHumanAnimalsInfo.toSet()

        val updatedAllNonHumanAnimalsInfo =
            updatedChat.allNonHumanAnimalsInfo.toSet()

        val nonHumanAnimalsInfoToManage: Set<NonHumanAnimalInfo> =
            (previousAllNonHumanAnimalsInfo - updatedAllNonHumanAnimalsInfo) +
                    (updatedAllNonHumanAnimalsInfo - previousAllNonHumanAnimalsInfo)

        nonHumanAnimalsInfoToManage.forEach { nonHumanAnimalInfoToManage ->
            if (isSuccess) {
                
                if (updatedAllNonHumanAnimalsInfo.contains(nonHumanAnimalInfoToManage)) {
                    
                    val nonHumanAnimalInfoEntity =
                        updatedChat.allNonHumanAnimalsInfo.first {
                            it.nonHumanAnimalId == nonHumanAnimalInfoToManage.nonHumanAnimalId
                        }.toEntity()

                    localChatRepository.insertNonHumanAnimalInfoEntity(
                        nonHumanAnimalInfoEntity,
                        onInsertNonHumanAnimalInfoEntity = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "ModifyChatInLocalRepository",
                                    "manageAllNonHumanAnimalsInfo: inserted the non human animal info ${nonHumanAnimalInfoEntity.nonHumanAnimalId} in the chat ${nonHumanAnimalInfoEntity.chatId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyChatInLocalRepository",
                                    "manageAllNonHumanAnimalsInfo: failed to insert the non human animal info ${nonHumanAnimalInfoEntity.nonHumanAnimalId} in the chat ${nonHumanAnimalInfoEntity.chatId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                } else {

                    val nonHumanAnimalInfo =
                        previousChat.allNonHumanAnimalsInfo.first {
                            it.nonHumanAnimalId == nonHumanAnimalInfoToManage.nonHumanAnimalId
                        }

                    localChatRepository.deleteNonHumanAnimalInfoEntity(
                        nonHumanAnimalInfo.nonHumanAnimalId,
                        onDeleteNonHumanAnimalInfoEntity = { rowsDeleted ->
                            if (rowsDeleted > 0) {
                                log.d(
                                    "ModifyChatInLocalRepository",
                                    "manageAllNonHumanAnimalsInfo: deleted the non human animal info ${nonHumanAnimalInfo.nonHumanAnimalId} in the chat ${nonHumanAnimalInfo.chatId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyChatInLocalRepository",
                                    "manageAllNonHumanAnimalsInfo: failed to delete the non human animal info ${nonHumanAnimalInfo.nonHumanAnimalId} in the chat ${nonHumanAnimalInfo.chatId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                }
            }
        }
        return isSuccess
    }

    private suspend fun manageAllActivistsInfo(
        previousChat: Chat,
        updatedChat: Chat
    ): Boolean {
        var isSuccess = true

        val previousAllActivistsInfo =
            previousChat.allActivistsInfo.map { it.uid }.toSet()

        val updatedAllActivistsInfo =
            updatedChat.allActivistsInfo.map { it.uid }.toSet()

        val activistUidsToManage: Set<String> =
            (previousAllActivistsInfo - updatedAllActivistsInfo) +
                    (updatedAllActivistsInfo - previousAllActivistsInfo)

        activistUidsToManage.forEach { uidToManage ->
            if (isSuccess) {

                if (updatedAllActivistsInfo.contains(uidToManage)) {

                    val userInfoEntity =
                        updatedChat.allActivistsInfo.first {
                            it.uid == uidToManage
                        }.toEntity()

                    localChatRepository.insertActivistInfoEntity(
                        userInfoEntity,
                        onInsertActivistInfoEntity = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "ModifyChatInLocalRepository",
                                    "manageAllActivistsInfo: inserted the activist ${userInfoEntity.uid} in the chat ${userInfoEntity.chatId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyChatInLocalRepository",
                                    "manageAllActivistsInfo: failed to insert the activist ${userInfoEntity.uid} in the chat ${userInfoEntity.chatId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                } else {

                    val userInfo =
                        previousChat.allActivistsInfo.first {
                            it.uid == uidToManage
                        }
                    localChatRepository.deleteActivistInfoEntity(
                        userInfo.uid,
                        onDeleteActivistInfoEntity = { rowsDeleted ->
                            if (rowsDeleted > 0) {
                                log.d(
                                    "ModifyChatInLocalRepository",
                                    "manageAllActivistsInfo: deleted the activist ${userInfo.uid} in the chat ${userInfo.chatId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyChatInLocalRepository",
                                    "manageAllActivistsInfo: failed to delete the activist ${userInfo.uid} in the chat ${userInfo.chatId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                }
            }
        }
        return isSuccess
    }

    private suspend fun manageAllBlockedUsersInfo(
        previousChat: Chat,
        updatedChat: Chat
    ): Boolean {
        var isSuccess = true

        val previousAllBlockedUsersInfo =
            previousChat.allBlockedUsersInfo.toSet()

        val updatedAllBlockedUsersInfo =
            updatedChat.allBlockedUsersInfo.toSet()

        val blockedUsersInfoToManage: Set<BlockedUserInfo> =
            (previousAllBlockedUsersInfo - updatedAllBlockedUsersInfo) +
                    (updatedAllBlockedUsersInfo - previousAllBlockedUsersInfo)

        blockedUsersInfoToManage.forEach { userInfoToManage ->
            if (isSuccess) {

                if (updatedAllBlockedUsersInfo.contains(userInfoToManage)) {

                    val blockedUserInfoEntity =
                        updatedChat.allBlockedUsersInfo.first {
                            it.uid == userInfoToManage.uid
                        }.toEntity()

                    localChatRepository.insertBlockedUserInfoEntity(
                        blockedUserInfoEntity,
                        onInsertBlockedUserInfoEntity = { rowId ->
                            if (rowId > 0) {
                                log.d(
                                    "ModifyChatInLocalRepository",
                                    "manageAllBlockedUsersInfo: inserted the blocked user ${blockedUserInfoEntity.uid} in the chat ${blockedUserInfoEntity.chatId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyChatInLocalRepository",
                                    "manageAllBlockedUsersInfo: failed to insert the blocked user ${blockedUserInfoEntity.uid} in the chat ${blockedUserInfoEntity.chatId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                } else {

                    val blockedUserInfo =
                        previousChat.allBlockedUsersInfo.first {
                            it.uid == userInfoToManage.uid
                        }

                    localChatRepository.deleteBlockedUserInfoEntity(
                        blockedUserInfo.uid,
                        onDeleteBlockedUserInfoEntity = { rowsDeleted ->
                            if (rowsDeleted > 0) {
                                log.d(
                                    "ModifyChatInLocalRepository",
                                    "manageAllBlockedUsersInfo: deleted the blocked user ${blockedUserInfo.uid} in the chat ${blockedUserInfo.chatId} in the local data source"
                                )
                            } else {
                                log.e(
                                    "ModifyChatInLocalRepository",
                                    "manageAllBlockedUsersInfo: failed to delete the blocked user ${blockedUserInfo.uid} in the chat ${blockedUserInfo.chatId} in the local data source"
                                )
                                isSuccess = false
                            }
                        }
                    )
                }
            }
        }
        return isSuccess
    }
}
