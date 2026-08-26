package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.chat.ActivistInfo

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("chatId")
    ]
)
data class ActivistInfoEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val uid: String
) {
    fun toDomain(): ActivistInfo {
        return ActivistInfo(
            id = id,
            chatId = chatId,
            uid = uid
        )
    }
}
