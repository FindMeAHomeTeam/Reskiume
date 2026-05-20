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
        manageAllNonHumanAnimalsInfo(
            previousChat,
            updatedChat
        ) {
            manageAllActivistsInfo(
                previousChat,
                updatedChat
            ) {
                manageAllBlockedUsersInfo(
                    previousChat,
                    updatedChat
                ) {
                    localChatRepository.modifyChat(
                        updatedChat.toEntity(),
                        onModifyChat = { rowsUpdated ->

                            onModifyChat(rowsUpdated > 0)
                        }
                    )
                }
            }
        }
    }

    private suspend fun manageAllNonHumanAnimalsInfo(
        previousChat: Chat,
        updatedChat: Chat,
        onComplete: suspend () -> Unit
    ) {
        val previousAllNonHumanAnimalsInfo =
            previousChat.allNonHumanAnimalsInfo.toSet()

        val updatedAllNonHumanAnimalsInfo =
            updatedChat.allNonHumanAnimalsInfo.toSet()

        val nonHumanAnimalsInfoToManage: Set<NonHumanAnimalInfo> =
            (previousAllNonHumanAnimalsInfo - updatedAllNonHumanAnimalsInfo) +
                    (updatedAllNonHumanAnimalsInfo - previousAllNonHumanAnimalsInfo)

        if (nonHumanAnimalsInfoToManage.isEmpty()) {
            onComplete()
            return
        }
        nonHumanAnimalsInfoToManage.forEach { nonHumanAnimalInfoToManage ->

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
                        }
                    }
                )
            }
        }
        onComplete()
    }

    private suspend fun manageAllActivistsInfo(
        previousChat: Chat,
        updatedChat: Chat,
        onComplete: suspend () -> Unit
    ) {
        val previousAllActivistsInfo =
            previousChat.allActivistsInfo.map { it.uid }.toSet()

        val updatedAllActivistsInfo =
            updatedChat.allActivistsInfo.map { it.uid }.toSet()

        val activistUidsToManage: Set<String> =
            (previousAllActivistsInfo - updatedAllActivistsInfo) +
                    (updatedAllActivistsInfo - previousAllActivistsInfo)

        if (activistUidsToManage.isEmpty()) {
            onComplete()
            return
        }
        activistUidsToManage.forEach { uidToManage ->

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
                        }
                    }
                )
            }
        }
        onComplete()
    }

    private suspend fun manageAllBlockedUsersInfo(
        previousChat: Chat,
        updatedChat: Chat,
        onComplete: suspend () -> Unit
    ) {
        val previousAllBlockedUsersInfo =
            previousChat.allBlockedUsersInfo.toSet()

        val updatedAllBlockedUsersInfo =
            updatedChat.allBlockedUsersInfo.toSet()

        val blockedUsersInfoToManage: Set<BlockedUserInfo> =
            (previousAllBlockedUsersInfo - updatedAllBlockedUsersInfo) +
                    (updatedAllBlockedUsersInfo - previousAllBlockedUsersInfo)

        if (blockedUsersInfoToManage.isEmpty()) {
            onComplete()
            return
        }
        blockedUsersInfoToManage.forEach { userInfoToManage ->

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
                        }
                    }
                )
            }
        }
        onComplete()
    }
}
