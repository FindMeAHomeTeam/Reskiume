package com.findmeahometeam.reskiume.domain.model.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalTypeEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteAcceptedNonHumanAnimalTypeForFosterHome
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

data class AcceptedNonHumanAnimalTypeForFosterHome(
    val acceptedNonHumanAnimalTypeId: Int = 0,
    val fosterHomeId: String,
    val acceptedNonHumanAnimalType: NonHumanAnimalType
) {
    fun toEntity(): AcceptedNonHumanAnimalTypeEntityForFosterHome {
        return AcceptedNonHumanAnimalTypeEntityForFosterHome(
            acceptedNonHumanAnimalTypeId = acceptedNonHumanAnimalTypeId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType
        )
    }

    fun toRemote(): RemoteAcceptedNonHumanAnimalTypeForFosterHome {
        return RemoteAcceptedNonHumanAnimalTypeForFosterHome(
            acceptedNonHumanAnimalTypeId = acceptedNonHumanAnimalTypeId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalType = acceptedNonHumanAnimalType
        )
    }
}
