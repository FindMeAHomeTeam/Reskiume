package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatMessageEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ActivistInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.BlockedUserInfoEntity
import kotlinx.coroutines.flow.Flow

interface LocalChatRepository {

    suspend fun insertChat(
        chatEntity: ChatEntity,
        onInsertChat: suspend (rowId: Long) -> Unit
    )

    suspend fun insertNonHumanAnimalInfoEntity(
        nonHumanAnimalInfoEntity: NonHumanAnimalInfoEntity,
        onInsertNonHumanAnimalInfoEntity: suspend (rowId: Long) -> Unit
    )

    suspend fun insertActivistInfoEntity(
        activistInfoEntity: ActivistInfoEntity,
        onInsertActivistInfoEntity: suspend (rowId: Long) -> Unit
    )

    suspend fun insertBlockedUserInfoEntity(
        blockedUserInfoEntity: BlockedUserInfoEntity,
        onInsertBlockedUserInfoEntity: suspend (rowId: Long) -> Unit
    )

    suspend fun insertChatMessageEntity(
        chatMessageEntity: ChatMessageEntity,
        onInsertChatMessageEntity: suspend (rowId: Long) -> Unit
    )

    suspend fun modifyChat(
        chatEntity: ChatEntity,
        onModifyChat: suspend (rowsUpdated: Int) -> Unit
    )

    suspend fun deleteChat(
        id: String,
        onDeleteChat: suspend (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteNonHumanAnimalInfoEntity(
        nonHumanAnimalId: String,
        onDeleteNonHumanAnimalInfoEntity: suspend (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteActivistInfoEntity(
        uid: String,
        onDeleteActivistInfoEntity: suspend (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteBlockedUserInfoEntity(
        uid: String,
        onDeleteBlockedUserInfoEntity: suspend (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteAllMyChats(
        uid: String,
        onDeleteAllMyChats: (rowsDeleted: Int) -> Unit
    )

    fun getChat(id: String): Flow<ChatEntityWithAllData?>

    fun getAllMyChatMessages(chatId: String): Flow<List<ChatMessageEntity>>

    fun getAllMyChats(uid: String): Flow<List<ChatEntityWithAllData>>

    suspend fun isNonHumanAnimalInChat(nonHumanAnimalId: String): Boolean

    suspend fun isFosterHomeChat(fosterHomeId: String): Boolean
}
