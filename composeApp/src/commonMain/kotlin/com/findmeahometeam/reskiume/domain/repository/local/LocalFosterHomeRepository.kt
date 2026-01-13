package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalGenderEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalTypeEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import kotlinx.coroutines.flow.Flow

interface LocalFosterHomeRepository {

    suspend fun insertFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onInsertFosterHome: (rowId: Long) -> Unit
    )

    suspend fun insertAcceptedNonHumanAnimalTypeForFosterHome(
        acceptedNonHumanAnimalType: AcceptedNonHumanAnimalTypeEntityForFosterHome,
        onInsertAcceptedNonHumanAnimalType: (rowId: Long) -> Unit
    )

    suspend fun insertAcceptedNonHumanAnimalGenderForFosterHome(
        acceptedNonHumanAnimalGender: AcceptedNonHumanAnimalGenderEntityForFosterHome,
        onInsertAcceptedNonHumanAnimalGender: (rowId: Long) -> Unit
    )

    suspend fun insertResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome,
        onInsertResidentNonHumanAnimalId: (rowId: Long) -> Unit
    )

    suspend fun modifyFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onModifyFosterHome: (rowsUpdated: Int) -> Unit
    )

    suspend fun modifyAcceptedNonHumanAnimalTypeForFosterHome(
        acceptedNonHumanAnimalType: AcceptedNonHumanAnimalTypeEntityForFosterHome,
        onModifyAcceptedNonHumanAnimalType: (rowsUpdated: Int) -> Unit
    )

    suspend fun modifyAcceptedNonHumanAnimalGenderForFosterHome(
        acceptedNonHumanAnimalGender: AcceptedNonHumanAnimalGenderEntityForFosterHome,
        onModifyAcceptedNonHumanAnimalGender: (rowsUpdated: Int) -> Unit
    )

    suspend fun modifyResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome,
        onModifyResidentNonHumanAnimalId: (rowsUpdated: Int) -> Unit
    )

    suspend fun deleteFosterHome(id: String, onDeleteFosterHome: (rowsDeleted: Int) -> Unit)

    suspend fun deleteAllFosterHomes(
        ownerId: String,
        onDeleteAllFosterHomes: (rowsDeleted: Int) -> Unit
    )

    suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData?

    fun getAllFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>
}
