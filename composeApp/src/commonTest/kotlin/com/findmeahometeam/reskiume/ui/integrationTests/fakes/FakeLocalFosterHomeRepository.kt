package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalFosterHomeRepository(
    private val localFosterHomeWithAllNonHumanAnimalDataList: MutableList<FosterHomeWithAllNonHumanAnimalData> = mutableListOf()
) : LocalFosterHomeRepository {

    override suspend fun insertFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onInsertFosterHome: suspend (rowId: Long) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.firstOrNull { it.fosterHomeEntity.id == fosterHomeEntity.id }

        if (fosterHomeWithAllNonHumanAnimalData == null) {
            localFosterHomeWithAllNonHumanAnimalDataList.add(
                FosterHomeWithAllNonHumanAnimalData(
                    fosterHomeEntity,
                    emptyList(),
                    emptyList()
                )
            )
            onInsertFosterHome(1L)
        } else {
            onInsertFosterHome(0)
        }
    }

    override suspend fun insertAcceptedNonHumanAnimalForFosterHome(
        acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome,
        onInsertAcceptedNonHumanAnimalType: (rowId: Long) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.flatMap { fosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimals.mapNotNull {
                    if (it.fosterHomeId == acceptedNonHumanAnimal.fosterHomeId) {
                        it
                    } else {
                        null
                    }
                }
            }

        if (fosterHomeWithAllNonHumanAnimalData.isEmpty()) {

            localFosterHomeWithAllNonHumanAnimalDataList.map {
                it.copy(allAcceptedNonHumanAnimals = it.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal)
            }
            onInsertAcceptedNonHumanAnimalType(1L)
        } else {
            onInsertAcceptedNonHumanAnimalType(0)
        }
    }

    override suspend fun insertResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome,
        onInsertResidentNonHumanAnimalId: (rowId: Long) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.flatMap { fosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.mapNotNull {
                    if (it.fosterHomeId == residentNonHumanAnimalId.fosterHomeId) {
                        it
                    } else {
                        null
                    }
                }
            }

        if (fosterHomeWithAllNonHumanAnimalData.isEmpty()) {

            localFosterHomeWithAllNonHumanAnimalDataList.map {
                it.copy(allResidentNonHumanAnimalIds = it.allResidentNonHumanAnimalIds + residentNonHumanAnimalId)
            }
            onInsertResidentNonHumanAnimalId(1L)
        } else {
            onInsertResidentNonHumanAnimalId(0)
        }
    }

    override suspend fun modifyFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onModifyFosterHome: suspend (rowsUpdated: Int) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.firstOrNull { it.fosterHomeEntity.id == fosterHomeEntity.id }
        if (fosterHomeWithAllNonHumanAnimalData == null) {
            onModifyFosterHome(0)
        } else {
            localFosterHomeWithAllNonHumanAnimalDataList[localFosterHomeWithAllNonHumanAnimalDataList.indexOf(
                fosterHomeWithAllNonHumanAnimalData
            )] = fosterHomeWithAllNonHumanAnimalData.copy(fosterHomeEntity = fosterHomeEntity)
            onModifyFosterHome(1)
        }
    }

    override suspend fun modifyAcceptedNonHumanAnimalForFosterHome(
        acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome,
        onModifyAcceptedNonHumanAnimal: (rowsUpdated: Int) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.flatMap { fosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.allAcceptedNonHumanAnimals.mapNotNull {
                    if (it.fosterHomeId == acceptedNonHumanAnimal.fosterHomeId) {
                        it
                    } else {
                        null
                    }
                }
            }

        if (fosterHomeWithAllNonHumanAnimalData.isEmpty()) {

            onModifyAcceptedNonHumanAnimal(0)
        } else {
            localFosterHomeWithAllNonHumanAnimalDataList.map {
                it.copy(allAcceptedNonHumanAnimals = it.allAcceptedNonHumanAnimals + acceptedNonHumanAnimal)
            }
            onModifyAcceptedNonHumanAnimal(1)
        }
    }

    override suspend fun modifyResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome,
        onModifyResidentNonHumanAnimalId: (rowsUpdated: Int) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.flatMap { fosterHomeWithAllNonHumanAnimalData ->

                fosterHomeWithAllNonHumanAnimalData.allResidentNonHumanAnimalIds.mapNotNull {
                    if (it.fosterHomeId == residentNonHumanAnimalId.fosterHomeId) {
                        it
                    } else {
                        null
                    }
                }
            }

        if (fosterHomeWithAllNonHumanAnimalData.isEmpty()) {

            onModifyResidentNonHumanAnimalId(0)
        } else {
            localFosterHomeWithAllNonHumanAnimalDataList.map {
                it.copy(allResidentNonHumanAnimalIds = it.allResidentNonHumanAnimalIds + residentNonHumanAnimalId)
            }
            onModifyResidentNonHumanAnimalId(1)
        }
    }

    override suspend fun deleteFosterHome(
        id: String,
        onDeleteFosterHome: (rowsDeleted: Int) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalData =
            localFosterHomeWithAllNonHumanAnimalDataList.firstOrNull { it.fosterHomeEntity.id == id }

        if (fosterHomeWithAllNonHumanAnimalData == null) {
            onDeleteFosterHome(0)
        } else {
            localFosterHomeWithAllNonHumanAnimalDataList.remove(fosterHomeWithAllNonHumanAnimalData)
            onDeleteFosterHome(1)
        }
    }

    override suspend fun deleteAllMyFosterHomes(
        ownerId: String,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    ) {
        val fosterHomeWithAllNonHumanAnimalDataList =
            localFosterHomeWithAllNonHumanAnimalDataList.filter { it.fosterHomeEntity.ownerId == ownerId }

        if (fosterHomeWithAllNonHumanAnimalDataList.isEmpty()) {
            onDeleteAllMyFosterHomes(0)
        } else {
            localFosterHomeWithAllNonHumanAnimalDataList.removeAll(
                fosterHomeWithAllNonHumanAnimalDataList
            )
            onDeleteAllMyFosterHomes(1)
        }
    }

    override suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData? =
        localFosterHomeWithAllNonHumanAnimalDataList.firstOrNull { it.fosterHomeEntity.id == id }

    override fun getAllMyFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>> =
        flowOf(localFosterHomeWithAllNonHumanAnimalDataList.filter { it.fosterHomeEntity.ownerId == ownerId })

    override fun getAllFosterHomesByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>> =
        flowOf(localFosterHomeWithAllNonHumanAnimalDataList.filter { it.fosterHomeEntity.country == country && it.fosterHomeEntity.city == city })

    override fun getAllFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>> =
        flowOf(
            localFosterHomeWithAllNonHumanAnimalDataList.filter {
                it.fosterHomeEntity.longitude >= activistLongitude - rangeLongitude
                        && it.fosterHomeEntity.longitude <= activistLongitude + rangeLongitude
                        && it.fosterHomeEntity.latitude >= activistLatitude - rangeLatitude
                        && it.fosterHomeEntity.latitude <= activistLatitude + rangeLatitude
            }
        )
}
