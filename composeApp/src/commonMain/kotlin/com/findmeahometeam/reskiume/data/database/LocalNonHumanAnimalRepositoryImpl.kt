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

    override suspend fun deleteNonHumanAnimal(id: String, caregiverId: String, onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit) {
        onDeleteNonHumanAnimal(reskiumeDatabase.getNonHumanAnimalDao().deleteNonHumanAnimal(id, caregiverId))
    }

    override suspend fun deleteAllNonHumanAnimals(id: String, caregiverId: String, onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit) {
        onDeleteAllNonHumanAnimals(reskiumeDatabase.getNonHumanAnimalDao().deleteAllNonHumanAnimals(id, caregiverId))
    }

    override fun getNonHumanAnimal(id: String, caregiverId: String): Flow<NonHumanAnimalEntity?> =
        reskiumeDatabase.getNonHumanAnimalDao().getNonHumanAnimal(id, caregiverId)
}
