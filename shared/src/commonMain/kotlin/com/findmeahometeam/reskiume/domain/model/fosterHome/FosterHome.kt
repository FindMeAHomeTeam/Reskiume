package com.findmeahometeam.reskiume.domain.model.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteFosterHome
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class FosterHome(
    val id: String,
    val ownerId: String,
    val savedBy: String = "",
    val title: String,
    val description: String,
    val conditions: String,
    val imageUrl: String,
    val allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>,
    val allResidentNonHumanAnimals: List<ResidentNonHumanAnimalForFosterHome>,
    val longitude: Double,
    val latitude: Double,
    val country: String,
    val city: String,
    val available: Boolean
) {

    @OptIn(ExperimentalTime::class)
    private fun setId(): String =
        id.ifBlank { Clock.System.now().epochSeconds.toString() + ownerId }

    fun toEntity(): FosterHomeEntity {
        return FosterHomeEntity(
            id = id.ifBlank { setId() },
            ownerId = ownerId,
            savedBy = savedBy,
            title = title,
            description = description,
            conditions = conditions,
            imageUrl = imageUrl,
            longitude = longitude,
            latitude = latitude,
            country = country,
            city = city,
            available = available
        )
    }

    fun toData(): RemoteFosterHome {
        return RemoteFosterHome(
            id = id.ifBlank { setId() },
            ownerId = ownerId,
            title = title,
            description = description,
            conditions = conditions,
            imageUrl = imageUrl,
            allAcceptedNonHumanAnimals = allAcceptedNonHumanAnimals.map { it.toRemote() },
            allResidentNonHumanAnimalIds = allResidentNonHumanAnimals.map { it.toData() },
            longitude = longitude,
            latitude = latitude,
            country = country,
            city = city,
            available = available
        )
    }
}
