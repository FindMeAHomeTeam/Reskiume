package com.findmeahometeam.reskiume.domain.usecases.localCache

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository

class ModifyCacheInLocalRepository(private val repository: LocalCacheRepository) {
    suspend operator fun invoke(localCache: LocalCache, onModifyUser: (rowsUpdated: Int) -> Unit) {
        val localCacheEntity: LocalCacheEntity? = repository.getLocalCacheEntity(localCache.cachedObjectId, localCache.section)
        repository.modifyLocalCacheEntity(localCacheEntity!!.copy(timestamp = localCache.timestamp), onModifyUser)
    }
}
