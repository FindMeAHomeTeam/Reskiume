package com.findmeahometeam.reskiume.domain.model.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteResidentNonHumanAnimalIdForFosterHome

data class ResidentNonHumanAnimalForFosterHome(
    val nonHumanAnimalId: String,
    val caregiverId: String,
    val fosterHomeId: String
) {
    fun toEntity(): ResidentNonHumanAnimalIdEntityForFosterHome {
        return ResidentNonHumanAnimalIdEntityForFosterHome(
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId,
            fosterHomeId = fosterHomeId
        )
    }

    fun toData(): RemoteResidentNonHumanAnimalIdForFosterHome {
        return RemoteResidentNonHumanAnimalIdForFosterHome(
            nonHumanAnimalId = nonHumanAnimalId,
            caregiverId = caregiverId,
            fosterHomeId = fosterHomeId
        )
    }
}
