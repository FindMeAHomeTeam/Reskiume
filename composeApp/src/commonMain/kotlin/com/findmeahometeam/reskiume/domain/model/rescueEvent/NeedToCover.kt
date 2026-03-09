package com.findmeahometeam.reskiume.domain.model.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRecueEvent
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteNeedToCover

data class NeedToCover(
    val needToCoverId: Long,
    val rescueNeed: RescueNeed,
    val rescueEventId: String
) {
    fun toEntity(): NeedToCoverEntityForRecueEvent {
        return NeedToCoverEntityForRecueEvent(
            needToCoverId = needToCoverId,
            rescueNeed = rescueNeed,
            rescueEventId = rescueEventId
        )
    }

    fun toData(): RemoteNeedToCover {
        return RemoteNeedToCover(
            needToCoverId = needToCoverId,
            rescueNeed = rescueNeed,
            rescueEventId = rescueEventId
        )
    }
}

enum class RescueNeed {
    UNSELECTED, RESCUERS, FOSTER_HOME
}
