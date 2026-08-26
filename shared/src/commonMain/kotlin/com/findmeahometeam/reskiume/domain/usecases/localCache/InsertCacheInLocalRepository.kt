package com.findmeahometeam.reskiume.domain.usecases.localCache

import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository

class InsertCacheInLocalRepository(private val repository: LocalCacheRepository) {
    suspend operator fun invoke(localCache: LocalCache, onInsertLocalCache: (rowId: Long) -> Unit) {
        repository.insertLocalCacheEntity(localCache.toEntity(), onInsertLocalCache)
    }
}
