package com.findmeahometeam.reskiume.data.remote.response.fosterHome

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalTypeForFosterHome
import kotlinx.serialization.Serializable

@Serializable
data class RemoteAcceptedNonHumanAnimalTypeForFosterHome(
    val acceptedNonHumanAnimalTypeId: Int? = 0,
    val fosterHomeId: String? = "",
    val acceptedNonHumanAnimalType: NonHumanAnimalType? = NonHumanAnimalType.UNSELECTED
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "acceptedNonHumanAnimalTypeId" to acceptedNonHumanAnimalTypeId,
            "fosterHomeId" to fosterHomeId,
            "acceptedNonHumanAnimalType" to acceptedNonHumanAnimalType
        )
    }

    fun toDomain(): AcceptedNonHumanAnimalTypeForFosterHome {
        return AcceptedNonHumanAnimalTypeForFosterHome(
            acceptedNonHumanAnimalTypeId = acceptedNonHumanAnimalTypeId ?: 0,
            fosterHomeId = fosterHomeId ?: "",
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType ?: NonHumanAnimalType.UNSELECTED
        )
    }
}
