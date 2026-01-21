package com.findmeahometeam.reskiume.data.remote.response.fosterHome

import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalGenderForFosterHome
import kotlinx.serialization.Serializable

@Serializable
data class RemoteAcceptedNonHumanAnimalGenderForFosterHome(
    val acceptedNonHumanAnimalGenderId: Int? = 0,
    val fosterHomeId: String? = "",
    val acceptedNonHumanAnimalGender: Gender? = Gender.UNSELECTED
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "acceptedNonHumanAnimalGenderId" to acceptedNonHumanAnimalGenderId,
            "fosterHomeId" to fosterHomeId,
            "acceptedNonHumanAnimalGender" to acceptedNonHumanAnimalGender
        )
    }

    fun toDomain(): AcceptedNonHumanAnimalGenderForFosterHome {
        return AcceptedNonHumanAnimalGenderForFosterHome(
            acceptedNonHumanAnimalGenderId = acceptedNonHumanAnimalGenderId ?: 0,
            fosterHomeId = fosterHomeId ?: "",
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender ?: Gender.UNSELECTED
        )
    }
}
