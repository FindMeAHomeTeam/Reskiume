package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section

@Dao
interface LocalCacheDao {

    @Insert
    suspend fun insertLocalCacheEntity(localCacheEntity: LocalCacheEntity): Long

    @Query("SELECT * FROM LocalCacheEntity WHERE uid = :uid AND section = :section")
    suspend fun getLocalCacheEntity(uid: String, section: Section): LocalCacheEntity?

    @Update
    suspend fun updateLocalCacheEntity(localCacheEntity: LocalCacheEntity): Int

    @Query("DELETE FROM LocalCacheEntity WHERE savedBy = :uid OR savedBy = '' ")
    suspend fun deleteLocalCacheEntity(uid: String): Int
}
