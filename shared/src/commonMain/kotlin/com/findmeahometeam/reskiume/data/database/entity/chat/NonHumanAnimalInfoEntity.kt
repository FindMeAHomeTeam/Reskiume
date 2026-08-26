package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
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
