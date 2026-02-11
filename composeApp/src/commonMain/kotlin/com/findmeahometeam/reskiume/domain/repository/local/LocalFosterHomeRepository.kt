package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import kotlinx.coroutines.flow.Flow

interface LocalFosterHomeRepository {

    suspend fun insertFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onInsertFosterHome: suspend (rowId: Long) -> Unit
    )

    suspend fun insertAcceptedNonHumanAnimalForFosterHome(
        acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome,
        onInsertAcceptedNonHumanAnimalType: (rowId: Long) -> Unit
    )

    suspend fun insertResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimal: ResidentNonHumanAnimalIdEntityForFosterHome,
        onInsertResidentNonHumanAnimalId: (rowId: Long) -> Unit
    )

    suspend fun modifyFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onModifyFosterHome: suspend (rowsUpdated: Int) -> Unit
    )

    suspend fun modifyAcceptedNonHumanAnimalForFosterHome(
        acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome,
        onModifyAcceptedNonHumanAnimal: (rowsUpdated: Int) -> Unit
    )

    suspend fun modifyResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome,
        onModifyResidentNonHumanAnimalId: (rowsUpdated: Int) -> Unit
    )

    suspend fun deleteFosterHome(id: String, onDeleteFosterHome: suspend (rowsDeleted: Int) -> Unit)

    suspend fun deleteAcceptedNonHumanAnimal(
        acceptedNonHumanAnimalId: Long,
        onDeleteAcceptedNonHumanAnimal: (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteResidentNonHumanAnimal(
        residentNonHumanAnimalId: String,
        onDeleteResidentNonHumanAnimalId: (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteAllMyFosterHomes(
        ownerId: String,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    )

    suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData?

    fun getAllMyFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    fun getAllFosterHomesByCountryAndCity(country: String, city: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    fun getAllFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>>
}
