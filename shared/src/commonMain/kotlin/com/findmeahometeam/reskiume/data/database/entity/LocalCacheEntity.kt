package com.findmeahometeam.reskiume.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.findmeahometeam.reskiume.data.util.Section

@Entity
data class LocalCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val cachedObjectId: String,
    val savedBy: String,
    val section: Section,
    val timestamp: Long,
)
