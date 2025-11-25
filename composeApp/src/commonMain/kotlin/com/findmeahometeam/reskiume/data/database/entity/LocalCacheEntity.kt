package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.LocalCache

@Entity
data class LocalCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val uid: String,
    val section: Section,
    val timestamp: Long,
) {
    fun toDomain(): LocalCache {
        return LocalCache(
            id = id,
            uid = uid,
            section = section,
            timestamp = timestamp
        )
    }
}
