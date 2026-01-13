package com.findmeahometeam.reskiume.domain.model.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteResidentNonHumanAnimalIdForFosterHome
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal

data class ResidentNonHumanAnimalForFosterHome(
    val residentNonHumanAnimal: NonHumanAnimal?,
    val fosterHomeId: String
) {
    fun toEntityForId(): ResidentNonHumanAnimalIdEntityForFosterHome {
        return ResidentNonHumanAnimalIdEntityForFosterHome(
            residentNonHumanAnimalId = residentNonHumanAnimal?.id ?: "",
            caregiverId = residentNonHumanAnimal?.caregiverId ?: "",
            fosterHomeId = fosterHomeId
        )
    }

    fun toRemoteForId(): RemoteResidentNonHumanAnimalIdForFosterHome {
        return RemoteResidentNonHumanAnimalIdForFosterHome(
            residentNonHumanAnimalId = residentNonHumanAnimal?.id ?: "",
            caregiverId = residentNonHumanAnimal?.caregiverId ?: "",
            fosterHomeId = fosterHomeId
        )
    }
}
