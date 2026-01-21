package com.findmeahometeam.reskiume.data.remote.response.fosterHome

import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import kotlinx.serialization.Serializable

@Serializable
data class RemoteResidentNonHumanAnimalIdForFosterHome(
    val residentNonHumanAnimalId: String? = "",
    val caregiverId: String? = "",
    val fosterHomeId: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "residentNonHumanAnimalId" to residentNonHumanAnimalId,
            "caregiverId" to caregiverId,
            "fosterHomeId" to fosterHomeId
        )
    }

    suspend fun toDomain(onFetchNonHumanAnimal: suspend (nonHumanAnimalId: String, caregiverId: String) -> NonHumanAnimal?): ResidentNonHumanAnimalForFosterHome {
        return ResidentNonHumanAnimalForFosterHome(
            residentNonHumanAnimal = if (residentNonHumanAnimalId == null || caregiverId == null) {
                null
            } else {
                onFetchNonHumanAnimal(residentNonHumanAnimalId, caregiverId)
            },
            fosterHomeId = fosterHomeId ?: ""
        )
    }
}
