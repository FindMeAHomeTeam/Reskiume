package com.findmeahometeam.reskiume.domain.usecases.localCache

import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class IsCachedObjectNullOrOlderThan24H(private val repository: LocalCacheRepository) {
    suspend operator fun invoke(uid: String, section: Section): Boolean? {
        val timestamp: Long? = repository.getLocalCacheEntity(uid, section)?.timestamp
        return if (timestamp == null) null else hasPassed24Hours(timestamp)
    }

    @OptIn(ExperimentalTime::class)
    private fun hasPassed24Hours(savedEpochSeconds: Long): Boolean {
        val nowEpoch: Long = Clock.System.now().epochSeconds
        return (nowEpoch - savedEpochSeconds) >= 1
    }
}
