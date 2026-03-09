package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRecueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NonHumanAnimalToRescueEntityForRescueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import kotlinx.coroutines.flow.Flow

class LocalRescueEventRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalRescueEventRepository {

    override suspend fun insertRescueEvent(
        rescueEventEntity: RescueEventEntity,
        onInsertRescueEvent: suspend (rowId: Long) -> Unit
    ) {
        onInsertRescueEvent(
            reskiumeDatabase.getRescueEventDao().insertRescueEvent(rescueEventEntity)
        )
    }

    override suspend fun insertNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalToRescueEntityForRescueEvent: NonHumanAnimalToRescueEntityForRescueEvent,
        onInsertNonHumanAnimalToRescueEntityForRescueEvent: (rowId: Long) -> Unit
    ) {
        onInsertNonHumanAnimalToRescueEntityForRescueEvent(
            reskiumeDatabase.getRescueEventDao()
                .insertNonHumanAnimalToRescueEntityForRescueEvent(
                    nonHumanAnimalToRescueEntityForRescueEvent
                )
        )
    }

    override suspend fun insertNeedToCoverEntityForRecueEvent(
        needToCoverEntityForRecueEvent: NeedToCoverEntityForRecueEvent,
        onNeedToCoverEntityForRecueEvent: (rowId: Long) -> Unit
    ) {
        onNeedToCoverEntityForRecueEvent(
            reskiumeDatabase.getRescueEventDao()
                .insertNeedToCoverEntityForRecueEvent(needToCoverEntityForRecueEvent)
        )
    }

    override suspend fun modifyRescueEvent(
        rescueEventEntity: RescueEventEntity,
        onModifyRescueEvent: suspend (rowsUpdated: Int) -> Unit
    ) {
        onModifyRescueEvent(
            reskiumeDatabase.getRescueEventDao().modifyRescueEvent(rescueEventEntity)
        )
    }

    override suspend fun deleteRescueEvent(
        id: String,
        onDeleteRescueEvent: suspend (rowsDeleted: Int) -> Unit
    ) {
        onDeleteRescueEvent(reskiumeDatabase.getRescueEventDao().deleteRescueEvent(id))
    }

    override suspend fun deleteNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalId: String,
        onDeleteNonHumanAnimalToRescueEntityForRescueEvent: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteNonHumanAnimalToRescueEntityForRescueEvent(
            reskiumeDatabase.getRescueEventDao()
                .deleteNonHumanAnimalToRescueEntityForRescueEvent(nonHumanAnimalId)
        )
    }

    override suspend fun deleteNeedToCoverEntityForRecueEvent(
        needToCoverId: Long,
        onDeleteNeedToCoverEntityForRecueEvent: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteNeedToCoverEntityForRecueEvent(
            reskiumeDatabase.getRescueEventDao()
                .deleteNeedToCoverEntityForRecueEvent(needToCoverId)
        )
    }

    override suspend fun deleteAllMyRescueEvents(
        creatorId: String,
        onDeleteAllMyRescueEvents: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteAllMyRescueEvents(
            reskiumeDatabase.getRescueEventDao().deleteAllMyRescueEvents(creatorId)
        )
    }

    override suspend fun getRescueEvent(id: String): RescueEventWithAllNeedsAndNonHumanAnimalData? =
        reskiumeDatabase.getRescueEventDao().getRescueEvent(id)


    override fun getAllMyRescueEvents(creatorId: String): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        reskiumeDatabase.getRescueEventDao().getAllMyRescueEvents(creatorId)

    override fun getAllRescueEvents(): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        reskiumeDatabase.getRescueEventDao().getAllRescueEvents()

    override fun getAllRescueEventsByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        reskiumeDatabase.getRescueEventDao().getAllRescueEventsByCountryAndCity(country, city)

    override fun getAllRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        reskiumeDatabase.getRescueEventDao().getAllRescueEventsByLocation(
            activistLongitude,
            activistLatitude,
            rangeLongitude,
            rangeLatitude
        )
}
