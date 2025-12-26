package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section

interface LocalCacheRepository {
    suspend fun insertLocalCacheEntity(localCacheEntity: LocalCacheEntity, onInsertLocalCache: (rowId: Long) -> Unit)

    suspend fun getLocalCacheEntity(cachedObjectId: String, section: Section): LocalCacheEntity?

    suspend fun modifyLocalCacheEntity(localCacheEntity: LocalCacheEntity, onModifyUser: (rowsUpdated: Int) -> Unit)

    suspend fun deleteLocalCacheEntity(cachedObjectId: String, onDeleteLocalCache: (rowsDeleted: Int) -> Unit)

    suspend fun deleteAllLocalCacheEntity(uid: String, onDeleteLocalCache: (rowsDeleted: Int) -> Unit)
}
