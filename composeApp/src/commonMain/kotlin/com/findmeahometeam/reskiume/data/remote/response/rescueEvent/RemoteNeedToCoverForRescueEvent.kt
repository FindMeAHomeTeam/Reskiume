package com.findmeahometeam.reskiume.data.remote.response.rescueEvent

import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueNeed
import kotlinx.serialization.Serializable

@Serializable
data class RemoteNeedToCoverForRescueEvent(
    val needToCoverId: Long? = 0,
    val rescueNeed: RescueNeed? = RescueNeed.UNSELECTED,
    val rescueEventId: String? = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "needToCoverId" to needToCoverId,
            "rescueNeed" to rescueNeed,
            "rescueEventId" to rescueEventId
        )
    }

    fun toDomain(): NeedToCover {
        return NeedToCover(
            needToCoverId = needToCoverId ?: 0,
            rescueNeed = rescueNeed ?: RescueNeed.UNSELECTED,
            rescueEventId = rescueEventId ?: "",
        )
    }
}
