package com.findmeahometeam.reskiume.ui.unitTestsWithFakes.fakes

import com.findmeahometeam.reskiume.data.database.entity.chat.ActivistInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.BlockedUserInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatMessageEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalChatRepository(
    private val localChatEntityWithAllData: MutableList<ChatEntityWithAllData> = mutableListOf()
) : LocalChatRepository {

    override suspend fun insertChat(
        chatEntity: ChatEntity,
        onInsertChat: suspend (rowId: Long) -> Unit
    ) {
        val chatEntityWithAllData =
            localChatEntityWithAllData.firstOrNull { it.chatEntity.id == chatEntity.id }

        if (chatEntityWithAllData == null) {
            localChatEntityWithAllData.add(
                ChatEntityWithAllData(
                    chatEntity,
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList()
                )
            )
            onInsertChat(1L)
        } else {
            onInsertChat(0)
        }
    }

    override suspend fun insertNonHumanAnimalInfoEntity(
        nonHumanAnimalInfoEntity: NonHumanAnimalInfoEntity,
        onInsertNonHumanAnimalInfoEntity: suspend (rowId: Long) -> Unit
    ) {
        val allNonHumanAnimalsInfoEntity =
            localChatEntityWithAllData.flatMap { chatEntityWithAllData ->

                chatEntityWithAllData.allNonHumanAnimalsInfo.filter {
                    it.nonHumanAnimalId == nonHumanAnimalInfoEntity.nonHumanAnimalId
                }
            }

        if (allNonHumanAnimalsInfoEntity.isEmpty()) {

            val result = localChatEntityWithAllData.map {
                it.copy(allNonHumanAnimalsInfo = it.allNonHumanAnimalsInfo + nonHumanAnimalInfoEntity)
            }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onInsertNonHumanAnimalInfoEntity(1L)
        } else {
            onInsertNonHumanAnimalInfoEntity(0)
        }
    }

    override suspend fun insertActivistInfoEntity(
        activistInfoEntity: ActivistInfoEntity,
        onInsertActivistInfoEntity: suspend (rowId: Long) -> Unit
    ) {
        val allActivistsInfoEntity =
            localChatEntityWithAllData.flatMap { chatEntityWithAllData ->

                chatEntityWithAllData.allActivistsInfo.filter {
                    it.uid == activistInfoEntity.uid
                }
            }

        if (allActivistsInfoEntity.isEmpty()) {

            val result = localChatEntityWithAllData.map {
                it.copy(allActivistsInfo = it.allActivistsInfo + activistInfoEntity)
            }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onInsertActivistInfoEntity(1L)
        } else {
            onInsertActivistInfoEntity(0)
        }
    }

    override suspend fun insertBlockedUserInfoEntity(
        blockedUserInfoEntity: BlockedUserInfoEntity,
        onInsertBlockedUserInfoEntity: suspend (rowId: Long) -> Unit
    ) {
        val allBlockedUsersInfoEntity =
            localChatEntityWithAllData.flatMap { chatEntityWithAllData ->

                chatEntityWithAllData.allBlockedUsersInfo.filter {
                    it.uid == blockedUserInfoEntity.uid
                }
            }

        if (allBlockedUsersInfoEntity.isEmpty()) {

            val result = localChatEntityWithAllData.map {
                it.copy(allBlockedUsersInfo = it.allBlockedUsersInfo + blockedUserInfoEntity)
            }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onInsertBlockedUserInfoEntity(1L)
        } else {
            onInsertBlockedUserInfoEntity(0)
        }
    }

    override suspend fun insertChatMessageEntity(
        chatMessageEntity: ChatMessageEntity,
        onInsertChatMessageEntity: suspend (rowId: Long) -> Unit
    ) {
        val allChatMessagesEntity =
            localChatEntityWithAllData.flatMap { chatEntityWithAllData ->

                chatEntityWithAllData.allChatMessages.filter {
                    it.id == chatMessageEntity.id
                }
            }

        if (allChatMessagesEntity.isEmpty()) {

            val result = localChatEntityWithAllData.map {
                it.copy(allChatMessages = it.allChatMessages + allChatMessagesEntity)
            }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onInsertChatMessageEntity(1L)
        } else {
            onInsertChatMessageEntity(0)
        }
    }

    override suspend fun modifyChat(
        chatEntity: ChatEntity,
        onModifyChat: suspend (rowsUpdated: Int) -> Unit
    ) {
        val chatEntityWithAllData =
            localChatEntityWithAllData.firstOrNull { it.chatEntity.id == chatEntity.id }
        if (chatEntityWithAllData == null) {
            onModifyChat(0)
        } else {
            localChatEntityWithAllData[localChatEntityWithAllData.indexOf(
                chatEntityWithAllData
            )] = chatEntityWithAllData.copy(chatEntity = chatEntity)
            onModifyChat(1)
        }
    }

    override suspend fun deleteChat(
        id: String,
        onDeleteChat: suspend (rowsDeleted: Int) -> Unit
    ) {
        val chatEntityWithAllData =
            localChatEntityWithAllData.firstOrNull { it.chatEntity.id == id }

        if (chatEntityWithAllData == null) {
            onDeleteChat(0)
        } else {
            localChatEntityWithAllData.remove(
                chatEntityWithAllData
            )
            onDeleteChat(1)
        }
    }

    override suspend fun deleteNonHumanAnimalInfoEntity(
        nonHumanAnimalId: String,
        onDeleteNonHumanAnimalInfoEntity: suspend (rowsDeleted: Int) -> Unit
    ) {
        val nonHumanAnimalInfoEntity =
            localChatEntityWithAllData.firstNotNullOfOrNull { chatEntityWithAllData ->
                chatEntityWithAllData.allNonHumanAnimalsInfo.firstOrNull { it.nonHumanAnimalId == nonHumanAnimalId }
            }

        if (nonHumanAnimalInfoEntity == null) {
            onDeleteNonHumanAnimalInfoEntity(0)
        } else {
            val result: List<ChatEntityWithAllData> =
                localChatEntityWithAllData.map { chatEntityWithAllData ->

                    if (chatEntityWithAllData.allNonHumanAnimalsInfo.contains(
                            nonHumanAnimalInfoEntity
                        )
                    ) {
                        chatEntityWithAllData.copy(
                            allNonHumanAnimalsInfo = chatEntityWithAllData.allNonHumanAnimalsInfo.minus(
                                nonHumanAnimalInfoEntity
                            )
                        )
                    } else {
                        chatEntityWithAllData
                    }
                }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onDeleteNonHumanAnimalInfoEntity(1)
        }
    }

    override suspend fun deleteActivistInfoEntity(
        uid: String,
        onDeleteActivistInfoEntity: suspend (rowsDeleted: Int) -> Unit
    ) {
        val activistInfoEntity =
            localChatEntityWithAllData.firstNotNullOfOrNull { chatEntityWithAllData ->
                chatEntityWithAllData.allActivistsInfo.firstOrNull { it.uid == uid }
            }

        if (activistInfoEntity == null) {
            onDeleteActivistInfoEntity(0)
        } else {
            val result: List<ChatEntityWithAllData> =
                localChatEntityWithAllData.map { chatEntityWithAllData ->

                    if (chatEntityWithAllData.allActivistsInfo.contains(
                            activistInfoEntity
                        )
                    ) {
                        chatEntityWithAllData.copy(
                            allActivistsInfo = chatEntityWithAllData.allActivistsInfo.minus(
                                activistInfoEntity
                            )
                        )
                    } else {
                        chatEntityWithAllData
                    }
                }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onDeleteActivistInfoEntity(1)
        }
    }

    override suspend fun deleteBlockedUserInfoEntity(
        uid: String,
        onDeleteBlockedUserInfoEntity: suspend (rowsDeleted: Int) -> Unit
    ) {
        val blockedUserInfoEntity =
            localChatEntityWithAllData.firstNotNullOfOrNull { chatEntityWithAllData ->
                chatEntityWithAllData.allBlockedUsersInfo.firstOrNull { it.uid == uid }
            }

        if (blockedUserInfoEntity == null) {
            onDeleteBlockedUserInfoEntity(0)
        } else {
            val result: List<ChatEntityWithAllData> =
                localChatEntityWithAllData.map { chatEntityWithAllData ->

                    if (chatEntityWithAllData.allBlockedUsersInfo.contains(
                            blockedUserInfoEntity
                        )
                    ) {
                        chatEntityWithAllData.copy(
                            allBlockedUsersInfo = chatEntityWithAllData.allBlockedUsersInfo.minus(
                                blockedUserInfoEntity
                            )
                        )
                    } else {
                        chatEntityWithAllData
                    }
                }
            localChatEntityWithAllData.removeAll(
                localChatEntityWithAllData
            )
            localChatEntityWithAllData.addAll(result)
            onDeleteBlockedUserInfoEntity(1)
        }
    }

    override suspend fun deleteAllMyChats(
        uid: String,
        onDeleteAllMyChats: (rowsDeleted: Int) -> Unit
    ) {
        val allChatEntitiesWithAllData =
            localChatEntityWithAllData.filter { it.chatEntity.chatHolderId == uid || it.chatEntity.savedBy == uid }

        if (allChatEntitiesWithAllData.isEmpty()) {
            onDeleteAllMyChats(0)
        } else {
            localChatEntityWithAllData.removeAll(
                allChatEntitiesWithAllData
            )
            onDeleteAllMyChats(1)
        }
    }

    override fun getChat(id: String): Flow<ChatEntityWithAllData?> =
        flowOf(localChatEntityWithAllData.firstOrNull { it.chatEntity.id == id })

    override fun getAllMyChatMessages(chatId: String): Flow<List<ChatMessageEntity>> =
        flowOf(
            localChatEntityWithAllData.firstOrNull { it.chatEntity.id == chatId }?.allChatMessages
                ?: emptyList()
        )

    override fun getAllMyChats(uid: String): Flow<List<ChatEntityWithAllData>> =
        flowOf(localChatEntityWithAllData.filter { it.chatEntity.chatHolderId == uid || it.chatEntity.savedBy == uid })

    override fun getNonHumanAnimalInfo(nonHumanAnimalId: String): Flow<NonHumanAnimalInfoEntity?> =
        flowOf(localChatEntityWithAllData.flatMap { it.allNonHumanAnimalsInfo }
            .firstOrNull { it.nonHumanAnimalId == nonHumanAnimalId })

    override suspend fun isFosterHomeChat(fosterHomeId: String): Boolean =
        localChatEntityWithAllData.firstOrNull { it.chatEntity.fosterHomeId == fosterHomeId } != null
}
