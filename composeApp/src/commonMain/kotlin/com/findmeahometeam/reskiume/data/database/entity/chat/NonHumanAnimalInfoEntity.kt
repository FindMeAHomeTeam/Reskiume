package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo

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
data class NonHumanAnimalInfoEntity(
    @PrimaryKey val nonHumanAnimalId: String,
    val chatId: String,
    val caregiverId: String
) {
    fun toDomain(): NonHumanAnimalInfo {
        return NonHumanAnimalInfo(
            nonHumanAnimalId = nonHumanAnimalId,
            chatId = chatId,
            caregiverId = caregiverId
        )
    }
}
