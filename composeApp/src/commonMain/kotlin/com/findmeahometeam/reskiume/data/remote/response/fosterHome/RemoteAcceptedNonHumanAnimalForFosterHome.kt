package com.findmeahometeam.reskiume.data.remote.response.fosterHome

import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import kotlinx.serialization.Serializable

@Serializable
data class RemoteAcceptedNonHumanAnimalForFosterHome(
    val acceptedNonHumanAnimalId: Int? = 0,
    val fosterHomeId: String? = "",
    val acceptedNonHumanAnimalType: NonHumanAnimalType? = NonHumanAnimalType.UNSELECTED,
    val acceptedNonHumanAnimalGender: Gender? = Gender.UNSELECTED
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "acceptedNonHumanAnimalId" to acceptedNonHumanAnimalId,
            "fosterHomeId" to fosterHomeId,
            "acceptedNonHumanAnimalType" to acceptedNonHumanAnimalType,
            "acceptedNonHumanAnimalGender" to acceptedNonHumanAnimalGender
        )
    }

    fun toDomain(): AcceptedNonHumanAnimalForFosterHome {
        return AcceptedNonHumanAnimalForFosterHome(
            acceptedNonHumanAnimalId = acceptedNonHumanAnimalId ?: 0,
            fosterHomeId = fosterHomeId ?: "",
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType ?: NonHumanAnimalType.UNSELECTED,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender ?: Gender.UNSELECTED
        )
    }
}
