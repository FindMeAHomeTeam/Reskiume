package com.findmeahometeam.reskiume.domain.usecases.localCache

import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository

class DeleteAllCacheFromLocalRepository(private val repository: LocalCacheRepository) {
    suspend operator fun invoke(uid: String, onDeleteLocalCache: (rowsDeleted: Int) -> Unit) {
        repository.deleteAllLocalCacheEntity(uid, onDeleteLocalCache)
    }
}
