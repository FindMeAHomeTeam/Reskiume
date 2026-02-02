package com.findmeahometeam.reskiume.data.remote.response.fosterHome

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import kotlinx.serialization.Serializable

@Serializable
data class RemoteFosterHome(
    val id: String? = "",
    val ownerId: String? = "",
    val title: String? = "",
    val description: String? = "",
    val conditions: String? = "",
    val imageUrl: String? = "",
    val allAcceptedNonHumanAnimals: List<RemoteAcceptedNonHumanAnimalForFosterHome>? = emptyList(),
    val allResidentNonHumanAnimalIds: List<RemoteResidentNonHumanAnimalIdForFosterHome>? = emptyList(),
    val longitude: Double? = 0.0,
    val latitude: Double? = 0.0,
    val country: String? = "",
    val city: String? = "",
    val available: Boolean? = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "ownerId" to ownerId,
            "title" to title,
            "description" to description,
            "conditions" to conditions,
            "imageUrl" to imageUrl,
            "allAcceptedNonHumanAnimals" to allAcceptedNonHumanAnimals,
            "allResidentNonHumanAnimalIds" to allResidentNonHumanAnimalIds,
            "longitude" to longitude,
            "latitude" to latitude,
            "country" to country,
            "city" to city,
            "available" to available
        )
    }

    suspend fun toDomain(onFetchNonHumanAnimal: suspend (nonHumanAnimalId: String, caregiverId: String) -> NonHumanAnimal?): FosterHome {
        return FosterHome(
            id = id ?: "",
            ownerId = ownerId ?: "",
            title = title ?: "",
            description = description ?: "",
            conditions = conditions ?: "",
            imageUrl = imageUrl ?: "",
            allAcceptedNonHumanAnimals =
                allAcceptedNonHumanAnimals?.map { it.toDomain() } ?: emptyList(),
            allResidentNonHumanAnimals =
                allResidentNonHumanAnimalIds?.map { it.toDomain(onFetchNonHumanAnimal) } ?: emptyList(),
            longitude = longitude ?: 0.0,
            latitude = latitude ?: 0.0,
            country = country ?: "",
            city = city ?: "",
            available = available ?: false
        )
    }
}