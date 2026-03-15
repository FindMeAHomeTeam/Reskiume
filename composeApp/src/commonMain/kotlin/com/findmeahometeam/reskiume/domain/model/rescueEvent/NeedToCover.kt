package com.findmeahometeam.reskiume.domain.model.rescueEvent

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRecueEvent
import com.findmeahometeam.reskiume.data.remote.response.rescueEvent.RemoteNeedToCoverForRescueEvent
import org.jetbrains.compose.resources.StringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.need_to_cover_foster_home
import reskiume.composeapp.generated.resources.need_to_cover_rescuers
import reskiume.composeapp.generated.resources.need_to_cover_unselected

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

    fun toData(): RemoteNeedToCoverForRescueEvent {
        return RemoteNeedToCoverForRescueEvent(
            needToCoverId = needToCoverId,
            rescueNeed = rescueNeed,
            rescueEventId = rescueEventId
        )
    }
}

enum class RescueNeed {
    UNSELECTED, RESCUERS, FOSTER_HOME
}

fun RescueNeed.toStringResource(): StringResource {
    return when (this) {
        RescueNeed.UNSELECTED -> Res.string.need_to_cover_unselected
        RescueNeed.RESCUERS -> Res.string.need_to_cover_rescuers
        RescueNeed.FOSTER_HOME -> Res.string.need_to_cover_foster_home
    }
}
