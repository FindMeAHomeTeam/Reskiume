package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRecueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NonHumanAnimalToRescueEntityForRescueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import kotlinx.coroutines.flow.Flow

interface LocalRescueEventRepository {

    suspend fun insertRescueEvent(
        rescueEventEntity: RescueEventEntity,
        onInsertRescueEvent: suspend (rowId: Long) -> Unit
    )

    suspend fun insertNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalToRescueEntityForRescueEvent: NonHumanAnimalToRescueEntityForRescueEvent,
        onInsertNonHumanAnimalToRescueEntityForRescueEvent: (rowId: Long) -> Unit
    )

    suspend fun insertNeedToCoverEntityForRecueEvent(
        needToCoverEntityForRecueEvent: NeedToCoverEntityForRecueEvent,
        onInsertNeedToCoverEntityForRecueEvent: (rowId: Long) -> Unit
    )

    suspend fun modifyRescueEvent(
        rescueEventEntity: RescueEventEntity,
        onModifyRescueEvent: suspend (rowsUpdated: Int) -> Unit
    )

    suspend fun deleteRescueEvent(
        id: String,
        onDeleteRescueEvent: suspend (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalId: String,
        onDeleteNonHumanAnimalToRescueEntityForRescueEvent: (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteNeedToCoverEntityForRecueEvent(
        needToCoverId: Long,
        onDeleteNeedToCoverEntityForRecueEvent: (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteAllMyRescueEvents(
        creatorId: String,
        onDeleteAllMyRescueEvents: (rowsDeleted: Int) -> Unit
    )

    suspend fun getRescueEvent(id: String): RescueEventWithAllNeedsAndNonHumanAnimalData?

    fun getAllMyRescueEvents(creatorId: String): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>

    fun getAllRescueEvents(): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>

    fun getAllRescueEventsByCountryAndCity(country: String, city: String): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>

    fun getAllRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>
}
