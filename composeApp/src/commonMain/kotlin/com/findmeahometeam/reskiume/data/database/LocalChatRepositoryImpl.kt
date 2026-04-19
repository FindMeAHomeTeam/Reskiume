package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatMessageEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ActivistInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.BlockedUserInfoEntity
import com.findmeahometeam.reskiume.domain.repository.local.LocalChatRepository
import kotlinx.coroutines.flow.Flow

class LocalChatRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalChatRepository {

    override suspend fun insertChat(
        chatEntity: ChatEntity,
        onInsertChat: suspend (rowId: Long) -> Unit
    ) {
        onInsertChat(reskiumeDatabase.getChatDao().insertChat(chatEntity))
    }

    override suspend fun insertNonHumanAnimalInfoEntity(
        nonHumanAnimalInfoEntity: NonHumanAnimalInfoEntity,
        onInsertNonHumanAnimalInfoEntity: (rowId: Long) -> Unit
    ) {
        onInsertNonHumanAnimalInfoEntity(
            reskiumeDatabase.getChatDao().insertNonHumanAnimalInfoEntity(nonHumanAnimalInfoEntity)
        )
    }

    override suspend fun insertActivistInfoEntity(
        activistInfoEntity: ActivistInfoEntity,
        onInsertActivistInfoEntity: (rowId: Long) -> Unit
    ) {
        onInsertActivistInfoEntity(reskiumeDatabase.getChatDao().insertActivistInfoEntity(activistInfoEntity))
    }

    override suspend fun insertBlockedUserInfoEntity(
        blockedUserInfoEntity: BlockedUserInfoEntity,
        onInsertBlockedUserInfoEntity: (rowId: Long) -> Unit
    ) {
        onInsertBlockedUserInfoEntity(reskiumeDatabase.getChatDao().insertBlockedUserInfoEntity(blockedUserInfoEntity))
    }

    override suspend fun insertChatMessageEntity(
        chatMessageEntity: ChatMessageEntity,
        onInsertChatMessageEntity: suspend (rowId: Long) -> Unit
    ) {
        onInsertChatMessageEntity(
            reskiumeDatabase.getChatDao().insertChatMessageEntity(chatMessageEntity)
        )
    }

    override suspend fun modifyChat(
        chatEntity: ChatEntity,
        onModifyChat: suspend (rowsUpdated: Int) -> Unit
    ) {
        onModifyChat(
            reskiumeDatabase.getChatDao().modifyChat(chatEntity)
        )
    }

    override suspend fun deleteChat(
        id: String,
        onDeleteChat: suspend (rowsDeleted: Int) -> Unit
    ) {
        onDeleteChat(reskiumeDatabase.getChatDao().deleteChat(id))
    }

    override suspend fun deleteNonHumanAnimalInfoEntity(
        nonHumanAnimalId: String,
        onDeleteNonHumanAnimalInfoEntity: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteNonHumanAnimalInfoEntity(
            reskiumeDatabase.getChatDao().deleteNonHumanAnimalInfoEntity(nonHumanAnimalId)
        )
    }

    override suspend fun deleteActivistInfoEntity(
        uid: String,
        onDeleteActivistInfoEntity: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteActivistInfoEntity(reskiumeDatabase.getChatDao().deleteActivistInfoEntity(uid))
    }

    override suspend fun deleteBlockedUserInfoEntity(
        uid: String,
        onDeleteBlockedUserInfoEntity: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteBlockedUserInfoEntity(reskiumeDatabase.getChatDao().deleteBlockedUserInfoEntity(uid))
    }

    override suspend fun deleteAllMyChats(
        uid: String,
        onDeleteAllMyChats: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteAllMyChats(reskiumeDatabase.getChatDao().deleteAllMyChats(uid))
    }

    override fun getChat(id: String): Flow<ChatEntityWithAllData?> =
        reskiumeDatabase.getChatDao().getChat(id)

    override fun getAllMyChats(uid: String): Flow<List<ChatEntityWithAllData>> =
        reskiumeDatabase.getChatDao().getAllMyChats(uid)
}
