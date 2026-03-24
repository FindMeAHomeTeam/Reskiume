package com.findmeahometeam.reskiume.domain.model.rescueEvent

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRescueEvent
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
    fun toEntity(): NeedToCoverEntityForRescueEvent {
        return NeedToCoverEntityForRescueEvent(
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

private fun NeedToCover.toSaveableList(): List<Any?> = listOf(
    needToCoverId,
    rescueNeed.name,
    rescueEventId
)

private fun List<Any?>.fromSaveableList(): NeedToCover = NeedToCover(
    needToCoverId = this[0] as Long,
    rescueNeed = RescueNeed.valueOf(this[1] as String),
    rescueEventId = this[2] as String
)

val NeedToCoverListSaver: Saver<List<NeedToCover>, Any> = listSaver(
    save = { allNeedsToCover ->
        listOf(allNeedsToCover.map { it.toSaveableList() })
    },
    restore = { savedList ->
        val innerList = savedList[0] as List<Any>
        innerList.map { (it as List<Any?>).fromSaveableList() }
    }
)
