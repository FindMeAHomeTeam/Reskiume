package com.findmeahometeam.reskiume.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.data.util.Section

@Entity
data class LocalCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val uid: String,
    val section: Section,
    val timestamp: Long,
)
