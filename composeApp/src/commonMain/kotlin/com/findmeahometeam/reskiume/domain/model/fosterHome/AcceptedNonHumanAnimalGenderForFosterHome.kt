package com.findmeahometeam.reskiume.domain.model.fosterHome

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalGenderEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteAcceptedNonHumanAnimalGenderForFosterHome
import com.findmeahometeam.reskiume.domain.model.Gender

data class AcceptedNonHumanAnimalGenderForFosterHome(
    val acceptedNonHumanAnimalGenderId: Int = 0,
    val fosterHomeId: String,
    val acceptedNonHumanAnimalGender: Gender
) {
    fun toEntity(): AcceptedNonHumanAnimalGenderEntityForFosterHome {
        return AcceptedNonHumanAnimalGenderEntityForFosterHome(
            acceptedNonHumanAnimalGenderId = acceptedNonHumanAnimalGenderId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender
        )
    }

    fun toRemote(): RemoteAcceptedNonHumanAnimalGenderForFosterHome {
        return RemoteAcceptedNonHumanAnimalGenderForFosterHome(
            acceptedNonHumanAnimalGenderId = acceptedNonHumanAnimalGenderId,
            fosterHomeId = fosterHomeId,
            acceptedNonHumanAnimalGender = acceptedNonHumanAnimalGender
        )
    }
}
