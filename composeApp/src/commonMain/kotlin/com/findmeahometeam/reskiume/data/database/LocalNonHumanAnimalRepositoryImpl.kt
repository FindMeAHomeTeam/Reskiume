package com.findmeahometeam.reskiume.data.database

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow

class LocalNonHumanAnimalRepositoryImpl(
    private val reskiumeDatabase: ReskiumeDatabase
): LocalNonHumanAnimalRepository {

    override suspend fun insertNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity, onInsertNonHumanAnimal: (rowId: Long) -> Unit) {
        onInsertNonHumanAnimal(reskiumeDatabase.getNonHumanAnimalDao().insertNonHumanAnimal(nonHumanAnimalEntity))
    }

    override suspend fun modifyNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity, onModifyNonHumanAnimal: (rowsUpdated: Int) -> Unit) {
        onModifyNonHumanAnimal(reskiumeDatabase.getNonHumanAnimalDao().modifyNonHumanAnimal(nonHumanAnimalEntity))
    }

    override suspend fun deleteNonHumanAnimal(id: String, onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit) {
        onDeleteNonHumanAnimal(reskiumeDatabase.getNonHumanAnimalDao().deleteNonHumanAnimal(id))
    }

    override suspend fun deleteAllNonHumanAnimals(caregiverId: String, onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit) {
        onDeleteAllNonHumanAnimals(reskiumeDatabase.getNonHumanAnimalDao().deleteAllNonHumanAnimals(caregiverId))
    }

    override suspend fun getNonHumanAnimal(id: String): NonHumanAnimalEntity? =
        reskiumeDatabase.getNonHumanAnimalDao().getNonHumanAnimal(id)

    override fun getAllMyNonHumanAnimals(caregiverId: String): Flow<List<NonHumanAnimalEntity>> =
        reskiumeDatabase.getNonHumanAnimalDao().getAllMyNonHumanAnimals(caregiverId)

    override fun getAllNonHumanAnimals(): Flow<List<NonHumanAnimalEntity>> =
        reskiumeDatabase.getNonHumanAnimalDao().getAllNonHumanAnimals()
}
