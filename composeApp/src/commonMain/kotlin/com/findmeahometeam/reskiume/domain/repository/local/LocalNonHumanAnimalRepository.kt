package com.findmeahometeam.reskiume.domain.repository.local

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import kotlinx.coroutines.flow.Flow

interface LocalNonHumanAnimalRepository {

    suspend fun insertNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity, onInsertNonHumanAnimal: (rowId: Long) -> Unit)

    suspend fun modifyNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity, onModifyNonHumanAnimal: (rowsUpdated: Int) -> Unit)

    suspend fun deleteNonHumanAnimal(id: Int, caregiverId: String, onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit)

    suspend fun deleteAllNonHumanAnimals(caregiverId: String, onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit)

    fun getNonHumanAnimal(id: Int, caregiverId: String): Flow<NonHumanAnimalEntity?>

    fun getAllNonHumanAnimals(caregiverId: String): Flow<List<NonHumanAnimalEntity>>
}
