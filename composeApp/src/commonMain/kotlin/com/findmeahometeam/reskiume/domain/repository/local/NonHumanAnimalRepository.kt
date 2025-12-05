package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import kotlinx.coroutines.flow.Flow

interface NonHumanAnimalRepository {

    suspend fun insertNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity, onInsertNonHumanAnimal: (rowId: Long) -> Unit)

    suspend fun modifyNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity, onModifyNonHumanAnimal: (rowsUpdated: Int) -> Unit)

    suspend fun deleteNonHumanAnimal(id: String, caregiverId: String, onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit)

    suspend fun deleteAllNonHumanAnimals(id: String, caregiverId: String, onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit)

    fun getNonHumanAnimal(id: String, caregiverId: String): Flow<NonHumanAnimalEntity?>
}
