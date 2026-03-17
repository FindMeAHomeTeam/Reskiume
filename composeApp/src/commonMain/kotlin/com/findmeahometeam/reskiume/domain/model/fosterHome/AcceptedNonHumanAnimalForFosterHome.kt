package com.findmeahometeam.reskiume.domain.model.fosterHome

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteAcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

data class AcceptedNonHumanAnimalForFosterHome(
    val acceptedNonHumanAnimalId: Long = 0,
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

private fun AcceptedNonHumanAnimalForFosterHome.toSaveableList(): List<Any?> = listOf(
    acceptedNonHumanAnimalId,
    fosterHomeId,
    acceptedNonHumanAnimalType,
    acceptedNonHumanAnimalGender
)

private fun List<Any?>.fromSaveableList(): AcceptedNonHumanAnimalForFosterHome = AcceptedNonHumanAnimalForFosterHome(
    acceptedNonHumanAnimalId = this[0] as Long,
    fosterHomeId = this[1] as String,
    acceptedNonHumanAnimalType = this[2] as NonHumanAnimalType,
    acceptedNonHumanAnimalGender = this[3] as Gender
)

val AcceptedNonHumanAnimalForFosterHomeListSaver: Saver<List<AcceptedNonHumanAnimalForFosterHome>, Any> = listSaver(
    save = { acceptedNonHumanAnimals -> acceptedNonHumanAnimals.map { it.toSaveableList() } },
    restore = { savedList -> savedList.map { it.fromSaveableList() } }
)
