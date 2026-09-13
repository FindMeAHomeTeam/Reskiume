package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import kotlinx.coroutines.flow.Flow

interface LocalFosterHomeRepository {

    suspend fun upsertAllFosterHomeData(
        fosterHomeEntity: FosterHomeEntity,
        allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalEntityForFosterHome>,
        allResidentNonHumanAnimalIds: List<ResidentNonHumanAnimalIdEntityForFosterHome>
    ): Flow<Boolean>

    suspend fun deleteFosterHome(id: String, onDeleteFosterHome: suspend (rowsDeleted: Int) -> Unit)

    suspend fun deleteAcceptedNonHumanAnimal(
        acceptedNonHumanAnimalId: String,
        onDeleteAcceptedNonHumanAnimal: (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteResidentNonHumanAnimal(
        nonHumanAnimalId: String,
        onDeleteResidentNonHumanAnimalId: (rowsDeleted: Int) -> Unit
    )

    suspend fun deleteAllMyFosterHomes(
        ownerId: String,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    )

    suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData?

    fun getAllMyFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    fun getAllFosterHomes(): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    fun getAllFosterHomesByCountryAndCity(country: String, city: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    fun getAllFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>>
}
