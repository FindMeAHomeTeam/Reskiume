package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository

class FakeLocalCacheRepository(
    private var localCacheList: MutableList<LocalCacheEntity> = mutableListOf()
): LocalCacheRepository {

    override suspend fun insertLocalCacheEntity(
        localCacheEntity: LocalCacheEntity,
        onInsertLocalCache: (rowId: Long) -> Unit
    ) {
        val localCache = localCacheList.firstOrNull{ it.id == localCacheEntity.id }
        if (localCache == null) {
            localCacheList.add(localCacheEntity)
            onInsertLocalCache(1L)
        } else {
            onInsertLocalCache(0)
        }
    }

    override suspend fun getLocalCacheEntity(
        uid: String,
        section: Section
    ): LocalCacheEntity? =
        localCacheList.firstOrNull{ it.uid == uid && it.section == section }

    override suspend fun modifyLocalCacheEntity(
        localCacheEntity: LocalCacheEntity,
        onModifyUser: (rowsUpdated: Int) -> Unit
    ) {
        val localCache = localCacheList.firstOrNull{ it.id == localCacheEntity.id }
        if (localCache == null) {
            onModifyUser(0)
        } else {
            localCacheList[localCacheList.indexOf(localCache)] = localCacheEntity
            onModifyUser(1)
        }
    }

    override suspend fun deleteLocalCacheEntity(
        uid: String,
        onDeleteLocalCache: (rowsDeleted: Int) -> Unit
    ) {
        val cacheList =
            localCacheList.filter { it.uid == uid || it.savedBy == uid || it.savedBy == "" }
        if (cacheList.isEmpty()) {
            onDeleteLocalCache(0)
        } else {
            localCacheList.removeAll(cacheList)
            onDeleteLocalCache(1)
        }
    }
}
