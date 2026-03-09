package com.findmeahometeam.reskiume.data.remote.response.rescueEvent

import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueEvent

data class RemoteRescueEvent(
    val id: String? = "",
    val creatorId: String? = "",
    val title: String? = "",
    val description: String? = "",
    val imageUrl: String? = "",
    val allNonHumanAnimalsToRescue: List<RemoteNonHumanAnimalToRescue>? = emptyList(),
    val allNeedsToCover: List<RemoteNeedToCover>? = emptyList(),
    val longitude: Double? = 0.0,
    val latitude: Double? = 0.0,
    val country: String? = "",
    val city: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "creatorId" to creatorId,
            "title" to title,
            "description" to description,
            "imageUrl" to imageUrl,
            "allNonHumanAnimalsToRescue" to allNonHumanAnimalsToRescue,
            "allNeedsToCover" to allNeedsToCover,
            "longitude" to longitude,
            "latitude" to latitude,
            "country" to country,
            "city" to city
        )
    }

    fun toDomain(): RescueEvent {
        return RescueEvent(
            id = id ?: "",
            creatorId = creatorId ?: "",
            title = title ?: "",
            description = description ?: "",
            imageUrl = imageUrl ?: "",
            allNonHumanAnimalsToRescue =
                allNonHumanAnimalsToRescue?.map { it.toDomain() } ?: emptyList(),
            allNeedsToCover =
                allNeedsToCover?.map { it.toDomain() } ?: emptyList(),
            longitude = longitude ?: 0.0,
            latitude = latitude ?: 0.0,
            country = country ?: "",
            city = city ?: ""
        )
    }
}