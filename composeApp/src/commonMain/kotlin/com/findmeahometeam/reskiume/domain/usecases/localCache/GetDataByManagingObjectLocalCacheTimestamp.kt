package com.findmeahometeam.reskiume.domain.usecases.localCache

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.repository.local.LocalCacheRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


// Use case to manage local cache based on timestamp for a specific managing object.
// It checks if the cache exists and whether it is older than 24 hours,
// and performs actions accordingly.
class GetDataByManagingObjectLocalCacheTimestamp(
    private val repository: LocalCacheRepository,
    private val log: Log
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun <T> invoke(
        uid: String,
        savedBy: String = "", // Indicate who saved the cache. Except for the user and reviews section, this is the same as uid.
        section: Section,
        onCompletionInsertCache: suspend () -> T,
        onCompletionUpdateCache: suspend () -> T,
        onVerifyCacheIsRecent: suspend () -> T
    ): T {
        val localCacheEntity: LocalCacheEntity? =
            repository.getLocalCacheEntity(uid, section)

        return when (localCacheEntity) {
            null -> {
                repository.insertLocalCacheEntity(
                    LocalCache(
                        uid = uid,
                        savedBy = savedBy.ifBlank { uid },
                        section = section,
                        timestamp = Clock.System.now().epochSeconds
                    ).toEntity()
                ) { rowId ->
                    if (rowId > 0) {
                        log.d(
                            "GetDataByManagingObjectLocalCacheTimestamp",
                            "$uid added to local cache in section $section"
                        )
                    } else {
                        log.e(
                            "GetDataByManagingObjectLocalCacheTimestamp",
                            "Error adding $uid to local cache in section $section"
                        )
                    }
                }
                onCompletionInsertCache()
            }

            else -> {

                if (hasPassed24Hours(localCacheEntity.timestamp)) {

                    repository.modifyLocalCacheEntity(
                        LocalCache(
                            id = localCacheEntity.id,
                            uid = uid,
                            savedBy = savedBy.ifBlank { uid },
                            section = section,
                            timestamp = Clock.System.now().epochSeconds
                        ).toEntity()
                    ) { rowsUpdated ->
                        if (rowsUpdated > 0) {
                            log.d(
                                "GetDataByManagingObjectLocalCacheTimestamp",
                                "$uid updated in local cache in section $section"
                            )
                        } else {
                            log.e(
                                "GetDataByManagingObjectLocalCacheTimestamp",
                                "Error updating $uid in local cache in section $section"
                            )
                        }
                    }
                    onCompletionUpdateCache()
                } else {
                    log.d(
                        "GetDataByManagingObjectLocalCacheTimestamp",
                        "Cache for $uid in section $section is up-to-date."
                    )
                    onVerifyCacheIsRecent()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun hasPassed24Hours(savedEpochSeconds: Long): Boolean {
        val nowEpoch: Long = Clock.System.now().epochSeconds
        return (nowEpoch - savedEpochSeconds) >= 24 * 60 * 60
    }
}
