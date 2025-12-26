package com.findmeahometeam.reskiume.domain.usecases.localCache

import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository

class DeleteCacheFromLocalRepository(private val repository: LocalCacheRepository) {
    suspend operator fun invoke(cachedObjectId: String, onDeleteLocalCache: (rowsDeleted: Int) -> Unit) {
        repository.deleteLocalCacheEntity(cachedObjectId, onDeleteLocalCache)
    }
}
