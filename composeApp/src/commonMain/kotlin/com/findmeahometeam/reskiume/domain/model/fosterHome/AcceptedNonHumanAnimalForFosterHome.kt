package com.findmeahometeam.reskiume.domain.model.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteAcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

data class AcceptedNonHumanAnimalForFosterHome(
    val acceptedNonHumanAnimalId: Int = 0,
    val fosterHomeId: String,
    val acceptedNonHumanAnimalType: NonHumanAnimalType,
    val acceptedNonHumanAnimalGender: Gender
) {
    fun toEntity(): AcceptedNonHumanAnimalEntityForFosterHome {
        return AcceptedNonHumanAnimalEntityForFosterHome(
            acceptedNonHumanAnimalId = acceptedNonHumanAnimalId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender
        )
    }

    fun toRemote(): RemoteAcceptedNonHumanAnimalForFosterHome {
        return RemoteAcceptedNonHumanAnimalForFosterHome(
            acceptedNonHumanAnimalId = acceptedNonHumanAnimalId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender
        )
    }
}
