package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.chat.ActivistInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.BlockedUserInfoEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntityWithAllData
import com.findmeahometeam.reskiume.data.database.entity.chat.ChatMessageEntity
import com.findmeahometeam.reskiume.data.database.entity.chat.NonHumanAnimalInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertChat(chatEntity: ChatEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertNonHumanAnimalInfoEntity(nonHumanAnimalInfoEntity: NonHumanAnimalInfoEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertActivistInfoEntity(activistInfoEntity: ActivistInfoEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertBlockedUserInfoEntity(blockedUserInfoEntity: BlockedUserInfoEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertChatMessageEntity(chatMessageEntity: ChatMessageEntity): Long

    @Update
    suspend fun modifyChat(chatEntity: ChatEntity): Int

    @Query("DELETE FROM ChatEntity WHERE id = :id")
    suspend fun deleteChat(id: String): Int

    @Query("DELETE FROM NonHumanAnimalInfoEntity WHERE nonHumanAnimalId = :nonHumanAnimalId")
    suspend fun deleteNonHumanAnimalInfoEntity(nonHumanAnimalId: String): Int

    @Query("DELETE FROM ActivistInfoEntity WHERE uid = :uid")
    suspend fun deleteActivistInfoEntity(uid: String): Int

    @Query("DELETE FROM BlockedUserInfoEntity WHERE uid = :uid")
    suspend fun deleteBlockedUserInfoEntity(uid: String): Int

    @Query("DELETE FROM ChatEntity WHERE chatHolderId = :uid OR savedBy = :uid ")
    suspend fun deleteAllMyChats(uid: String): Int

    @Transaction
    @Query("SELECT * FROM ChatEntity WHERE id = :id")
    fun getChat(id: String): Flow<ChatEntityWithAllData?>

    @Transaction
    @Query("SELECT * FROM ChatEntity WHERE chatHolderId = :uid OR savedBy = :uid")
    fun getAllMyChats(uid: String): Flow<List<ChatEntityWithAllData>>
}
