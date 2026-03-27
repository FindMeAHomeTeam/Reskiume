package com.findmeahometeam.reskiume.domain.model.fosterHome

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.remote.response.fosterHome.RemoteAcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType

data class AcceptedNonHumanAnimalForFosterHome(
    val acceptedNonHumanAnimalId: String,
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
    acceptedNonHumanAnimalType.name,
    acceptedNonHumanAnimalGender.name
)

private fun List<Any?>.fromSaveableList(): AcceptedNonHumanAnimalForFosterHome = AcceptedNonHumanAnimalForFosterHome(
    acceptedNonHumanAnimalId = this[0] as String,
    fosterHomeId = this[1] as String,
    acceptedNonHumanAnimalType = NonHumanAnimalType.valueOf(this[2] as String),
    acceptedNonHumanAnimalGender = Gender.valueOf(this[3] as String)
)

val AcceptedNonHumanAnimalForFosterHomeListSaver: Saver<List<AcceptedNonHumanAnimalForFosterHome>, Any> =
    listSaver(
        save = { acceptedNonHumanAnimals ->
            listOf(acceptedNonHumanAnimals.map { it.toSaveableList() })
        },
        restore = { savedList ->
            val innerList = savedList[0] as List<Any>
            innerList.map { (it as List<Any?>).fromSaveableList() }
        }
    )
