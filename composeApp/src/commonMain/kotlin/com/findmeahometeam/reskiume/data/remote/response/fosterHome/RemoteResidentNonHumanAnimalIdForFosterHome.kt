package com.findmeahometeam.reskiume.data.remote.response.fosterHome

import com.findmeahometeam.reskiume.domain.model.fosterHome.ResidentNonHumanAnimalForFosterHome
import kotlinx.serialization.Serializable

@Serializable
data class RemoteResidentNonHumanAnimalIdForFosterHome(
    val nonHumanAnimalId: String? = "",
    val caregiverId: String? = "",
    val fosterHomeId: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nonHumanAnimalId" to nonHumanAnimalId,
            "caregiverId" to caregiverId,
            "fosterHomeId" to fosterHomeId
        )
    }

    fun toDomain(): ResidentNonHumanAnimalForFosterHome {
        return ResidentNonHumanAnimalForFosterHome(
            nonHumanAnimalId = nonHumanAnimalId ?: "",
            caregiverId = caregiverId ?: "",
            fosterHomeId = fosterHomeId ?: ""
        )
    }
}
