package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import com.findmeahometeam.reskiume.domain.repository.local.LocalFosterHomeRepository
import kotlinx.coroutines.flow.Flow

class LocalFosterHomeRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
) : LocalFosterHomeRepository {

    override suspend fun insertFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onInsertFosterHome: suspend (rowId: Long) -> Unit
    ) {
        onInsertFosterHome(reskiumeDatabase.getFosterHomeDao().insertFosterHome(fosterHomeEntity))
    }

    override suspend fun insertAcceptedNonHumanAnimalForFosterHome(
        acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome,
        onInsertAcceptedNonHumanAnimalType: (rowId: Long) -> Unit
    ) {
        onInsertAcceptedNonHumanAnimalType(
            reskiumeDatabase.getFosterHomeDao()
                .insertAcceptedNonHumanAnimalForFosterHome(acceptedNonHumanAnimal)
        )
    }

    override suspend fun insertResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimal: ResidentNonHumanAnimalIdEntityForFosterHome,
        onInsertResidentNonHumanAnimalId: (rowId: Long) -> Unit
    ) {
        onInsertResidentNonHumanAnimalId(
            reskiumeDatabase.getFosterHomeDao()
                .insertResidentNonHumanAnimalIdForFosterHome(residentNonHumanAnimal)
        )
    }

    override suspend fun modifyFosterHome(
        fosterHomeEntity: FosterHomeEntity,
        onModifyFosterHome: suspend (rowsUpdated: Int) -> Unit
    ) {
        onModifyFosterHome(reskiumeDatabase.getFosterHomeDao().modifyFosterHome(fosterHomeEntity))
    }

    override suspend fun modifyAcceptedNonHumanAnimalForFosterHome(
        acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome,
        onModifyAcceptedNonHumanAnimal: (rowsUpdated: Int) -> Unit
    ) {
        onModifyAcceptedNonHumanAnimal(
            reskiumeDatabase.getFosterHomeDao()
                .modifyAcceptedNonHumanAnimalForFosterHome(acceptedNonHumanAnimal)
        )
    }

    override suspend fun modifyResidentNonHumanAnimalIdForFosterHome(
        residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome,
        onModifyResidentNonHumanAnimalId: (rowsUpdated: Int) -> Unit
    ) {
        onModifyResidentNonHumanAnimalId(
            reskiumeDatabase.getFosterHomeDao()
                .modifyResidentNonHumanAnimalIdForFosterHome(residentNonHumanAnimalId)
        )
    }

    override suspend fun deleteFosterHome(
        id: String,
        onDeleteFosterHome: suspend (rowsDeleted: Int) -> Unit
    ) {
        onDeleteFosterHome(reskiumeDatabase.getFosterHomeDao().deleteFosterHome(id))
    }

    override suspend fun deleteAcceptedNonHumanAnimal(
        acceptedNonHumanAnimalId: Long,
        onDeleteAcceptedNonHumanAnimal: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteAcceptedNonHumanAnimal(reskiumeDatabase.getFosterHomeDao().deleteAcceptedNonHumanAnimalEntityForFosterHome(acceptedNonHumanAnimalId))
    }

    override suspend fun deleteResidentNonHumanAnimal(
        nonHumanAnimalId: String,
        onDeleteResidentNonHumanAnimalId: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteResidentNonHumanAnimalId(reskiumeDatabase.getFosterHomeDao().deleteResidentNonHumanAnimalIdEntityForFosterHome(nonHumanAnimalId))
    }

    override suspend fun deleteAllMyFosterHomes(
        ownerId: String,
        onDeleteAllMyFosterHomes: (rowsDeleted: Int) -> Unit
    ) {
        onDeleteAllMyFosterHomes(
            reskiumeDatabase.getFosterHomeDao().deleteAllMyFosterHomes(ownerId)
        )
    }

    override suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData? =
        reskiumeDatabase.getFosterHomeDao().getFosterHome(id)


    override fun getAllMyFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>> =
        reskiumeDatabase.getFosterHomeDao().getAllMyFosterHomes(ownerId)

    override fun getAllFosterHomesByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>> =
        reskiumeDatabase.getFosterHomeDao().getAllFosterHomesByCountryAndCity(country, city)

    override fun getAllFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>> =
        reskiumeDatabase.getFosterHomeDao().getAllFosterHomesByLocation(
            activistLongitude,
            activistLatitude,
            rangeLongitude,
            rangeLatitude
        )
}
