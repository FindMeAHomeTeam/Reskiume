package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRescueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NonHumanAnimalToRescueEntityForRescueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import com.findmeahometeam.reskiume.domain.repository.local.LocalRescueEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalRescueEventRepository(
    private val localRescueEventWithAllNeedsAndNonHumanAnimalDataList: MutableList<RescueEventWithAllNeedsAndNonHumanAnimalData> = mutableListOf()
) : LocalRescueEventRepository {

    override suspend fun insertRescueEvent(
        rescueEventEntity: RescueEventEntity,
        onInsertRescueEvent: suspend (rowId: Long) -> Unit
    ) {
        val rescueEventWithAllNonHumanAnimalData =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.firstOrNull { it.rescueEventEntity.id == rescueEventEntity.id }

        if (rescueEventWithAllNonHumanAnimalData == null) {
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.add(
                RescueEventWithAllNeedsAndNonHumanAnimalData(
                    rescueEventEntity,
                    emptyList(),
                    emptyList()
                )
            )
            onInsertRescueEvent(1L)
        } else {
            onInsertRescueEvent(0)
        }
    }

    override suspend fun insertNeedToCoverEntityForRescueEvent(
        needToCoverEntityForRescueEvent: NeedToCoverEntityForRescueEvent,
        onInsertNeedToCoverEntityForRescueEvent: (rowId: Long) -> Unit
    ) {
        val allNeedsToCover =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.flatMap { rescueEventWithAllNonHumanAnimalData ->

                rescueEventWithAllNonHumanAnimalData.allNeedsToCover.filter {
                    it.needToCoverId == needToCoverEntityForRescueEvent.needToCoverId
                }
            }

        if (allNeedsToCover.isEmpty()) {

            val result = localRescueEventWithAllNeedsAndNonHumanAnimalDataList.map {
                it.copy(allNeedsToCover = it.allNeedsToCover + needToCoverEntityForRescueEvent)
            }
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.removeAll(
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList
            )
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.addAll(result)
            onInsertNeedToCoverEntityForRescueEvent(1L)
        } else {
            onInsertNeedToCoverEntityForRescueEvent(0)
        }
    }

    override suspend fun insertNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalToRescueEntityForRescueEvent: NonHumanAnimalToRescueEntityForRescueEvent,
        onInsertNonHumanAnimalToRescueEntityForRescueEvent: (rowId: Long) -> Unit
    ) {
        val allNonHumanAnimalsToRescue =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.flatMap { rescueEventWithAllNonHumanAnimalData ->

                rescueEventWithAllNonHumanAnimalData.allNonHumanAnimalsToRescue.filter {
                    it.nonHumanAnimalId == nonHumanAnimalToRescueEntityForRescueEvent.nonHumanAnimalId
                }
            }

        if (allNonHumanAnimalsToRescue.isEmpty()) {

            val result = localRescueEventWithAllNeedsAndNonHumanAnimalDataList.map {
                it.copy(allNonHumanAnimalsToRescue = it.allNonHumanAnimalsToRescue + nonHumanAnimalToRescueEntityForRescueEvent)
            }
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.removeAll(
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList
            )
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.addAll(result)
            onInsertNonHumanAnimalToRescueEntityForRescueEvent(1L)
        } else {
            onInsertNonHumanAnimalToRescueEntityForRescueEvent(0)
        }
    }

    override suspend fun modifyRescueEvent(
        rescueEventEntity: RescueEventEntity,
        onModifyRescueEvent: suspend (rowsUpdated: Int) -> Unit
    ) {
        val rescueEventWithAllNonHumanAnimalData =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.firstOrNull { it.rescueEventEntity.id == rescueEventEntity.id }
        if (rescueEventWithAllNonHumanAnimalData == null) {
            onModifyRescueEvent(0)
        } else {
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList[localRescueEventWithAllNeedsAndNonHumanAnimalDataList.indexOf(
                rescueEventWithAllNonHumanAnimalData
            )] = rescueEventWithAllNonHumanAnimalData.copy(rescueEventEntity = rescueEventEntity)
            onModifyRescueEvent(1)
        }
    }

    override suspend fun deleteRescueEvent(
        id: String,
        onDeleteRescueEvent: suspend (rowsDeleted: Int) -> Unit
    ) {
        val rescueEventWithAllNonHumanAnimalData =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.firstOrNull { it.rescueEventEntity.id == id }

        if (rescueEventWithAllNonHumanAnimalData == null) {
            onDeleteRescueEvent(0)
        } else {
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.remove(
                rescueEventWithAllNonHumanAnimalData
            )
            onDeleteRescueEvent(1)
        }
    }

    override suspend fun deleteNeedToCoverEntityForRescueEvent(
        needToCoverId: Long,
        onDeleteNeedToCoverEntityForRescueEvent: (rowsDeleted: Int) -> Unit
    ) {
        val needToCover =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.firstNotNullOfOrNull { rescueEventWithAllNeedsToCoverAndNonHumanAnimalData ->
                rescueEventWithAllNeedsToCoverAndNonHumanAnimalData.allNeedsToCover.firstOrNull { it.needToCoverId == needToCoverId }
            }

        if (needToCover == null) {
            onDeleteNeedToCoverEntityForRescueEvent(0)
        } else {
            val result: List<RescueEventWithAllNeedsAndNonHumanAnimalData> =
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList.map { rescueEventWithAllNeedsAndNonHumanAnimalData: RescueEventWithAllNeedsAndNonHumanAnimalData ->

                    if (rescueEventWithAllNeedsAndNonHumanAnimalData.allNeedsToCover.contains(
                            needToCover
                        )
                    ) {
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            allNeedsToCover = rescueEventWithAllNeedsAndNonHumanAnimalData.allNeedsToCover.minus(
                                needToCover
                            )
                        )
                    } else {
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    }
                }
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.removeAll(
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList
            )
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.addAll(result)
            onDeleteNeedToCoverEntityForRescueEvent(1)
        }
    }

    override suspend fun deleteNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalId: String,
        onDeleteNonHumanAnimalToRescueEntityForRescueEvent: (rowsDeleted: Int) -> Unit
    ) {
        val residentNonHumanAnimalIdEntity =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.firstNotNullOfOrNull { rescueEventWithAllNeedsAndNonHumanAnimalData ->
                rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue.firstOrNull { it.nonHumanAnimalId == nonHumanAnimalId }
            }

        if (residentNonHumanAnimalIdEntity == null) {
            onDeleteNonHumanAnimalToRescueEntityForRescueEvent(0)
        } else {
            val result: List<RescueEventWithAllNeedsAndNonHumanAnimalData> =
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList.map { rescueEventWithAllNeedsAndNonHumanAnimalData: RescueEventWithAllNeedsAndNonHumanAnimalData ->

                    if (rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue.contains(
                            residentNonHumanAnimalIdEntity
                        )
                    ) {
                        rescueEventWithAllNeedsAndNonHumanAnimalData.copy(
                            allNonHumanAnimalsToRescue = rescueEventWithAllNeedsAndNonHumanAnimalData.allNonHumanAnimalsToRescue.minus(
                                residentNonHumanAnimalIdEntity
                            )
                        )
                    } else {
                        rescueEventWithAllNeedsAndNonHumanAnimalData
                    }
                }
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.removeAll(
                localRescueEventWithAllNeedsAndNonHumanAnimalDataList
            )
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.addAll(result)
            onDeleteNonHumanAnimalToRescueEntityForRescueEvent(1)
        }
    }

    override suspend fun deleteAllMyRescueEvents(
        creatorId: String,
        onDeleteAllMyRescueEvents: (rowsDeleted: Int) -> Unit
    ) {
        val rescueEventWithAllNonHumanAnimalDataList =
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.filter { it.rescueEventEntity.creatorId == creatorId }

        if (rescueEventWithAllNonHumanAnimalDataList.isEmpty()) {
            onDeleteAllMyRescueEvents(0)
        } else {
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.removeAll(
                rescueEventWithAllNonHumanAnimalDataList
            )
            onDeleteAllMyRescueEvents(1)
        }
    }

    override suspend fun getRescueEvent(id: String): RescueEventWithAllNeedsAndNonHumanAnimalData? =
        localRescueEventWithAllNeedsAndNonHumanAnimalDataList.firstOrNull { it.rescueEventEntity.id == id }

    override fun getAllMyRescueEvents(creatorId: String): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        flowOf(localRescueEventWithAllNeedsAndNonHumanAnimalDataList.filter { it.rescueEventEntity.creatorId == creatorId })

    override fun getAllRescueEvents(): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        flowOf(localRescueEventWithAllNeedsAndNonHumanAnimalDataList)

    override fun getAllRescueEventsByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        flowOf(localRescueEventWithAllNeedsAndNonHumanAnimalDataList.filter { it.rescueEventEntity.country == country && it.rescueEventEntity.city == city })

    override fun getAllRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>> =
        flowOf(
            localRescueEventWithAllNeedsAndNonHumanAnimalDataList.filter {
                it.rescueEventEntity.longitude >= activistLongitude - rangeLongitude
                        && it.rescueEventEntity.longitude <= activistLongitude + rangeLongitude
                        && it.rescueEventEntity.latitude >= activistLatitude - rangeLatitude
                        && it.rescueEventEntity.latitude <= activistLatitude + rangeLatitude
            }
        )
}
