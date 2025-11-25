package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository

class LocalCacheRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalCacheRepository {
    override suspend fun insertLocalCacheEntity(
        localCacheEntity: LocalCacheEntity,
        onInsertLocalCache: (rowId: Long) -> Unit
    ) {
        onInsertLocalCache(
            reskiumeDatabase.getLocalCacheDao().insertLocalCacheEntity(localCacheEntity)
        )
    }

    override suspend fun getLocalCacheEntity(uid: String, section: Section): LocalCacheEntity? =
        reskiumeDatabase.getLocalCacheDao().getLocalCacheEntity(uid, section)

    override suspend fun modifyLocalCacheEntity(
        localCacheEntity: LocalCacheEntity,
        onModifyUser: (Int) -> Unit
    ) {
        onModifyUser(reskiumeDatabase.getLocalCacheDao().updateLocalCacheEntity(localCacheEntity))
    }

    override suspend fun deleteLocalCacheEntity(
        uid: String,
        onDeleteLocalCache: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteLocalCache(reskiumeDatabase.getLocalCacheDao().deleteLocalCacheEntity(uid))
    }
}
