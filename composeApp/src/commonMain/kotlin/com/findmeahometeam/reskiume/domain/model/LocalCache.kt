package com.findmeahometeam.reskiume.domain.model

import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.util.Section

data class LocalCache(
    val id: Int = 0,
    val uid: String,
    val savedBy: String,
    val section: Section,
    val timestamp: Long,
) {
    fun toEntity(): LocalCacheEntity {
        return LocalCacheEntity(
            id = id,
            uid = uid,
            savedBy = savedBy,
            section = section,
            timestamp = timestamp
        )
    }
}
