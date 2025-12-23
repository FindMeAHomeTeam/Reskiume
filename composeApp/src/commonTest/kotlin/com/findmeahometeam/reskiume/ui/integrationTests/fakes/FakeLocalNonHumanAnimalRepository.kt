package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.domain.repository.local.LocalNonHumanAnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalNonHumanAnimalRepository(
    private val localNonHumanAnimalList: MutableList<NonHumanAnimalEntity> = mutableListOf()
) : LocalNonHumanAnimalRepository {

    override suspend fun insertNonHumanAnimal(
        nonHumanAnimalEntity: NonHumanAnimalEntity,
        onInsertNonHumanAnimal: (rowId: Long) -> Unit
    ) {
        val nonHumanAnimal =
            localNonHumanAnimalList.firstOrNull { it.id == nonHumanAnimalEntity.id }
        if (nonHumanAnimal == null) {
            localNonHumanAnimalList.add(nonHumanAnimalEntity)
            onInsertNonHumanAnimal(1L)
        } else {
            onInsertNonHumanAnimal(0)
        }
    }

    override suspend fun modifyNonHumanAnimal(
        nonHumanAnimalEntity: NonHumanAnimalEntity,
        onModifyNonHumanAnimal: (rowsUpdated: Int) -> Unit
    ) {
        val nonHumanAnimal =
            localNonHumanAnimalList.firstOrNull { it.id == nonHumanAnimalEntity.id }
        if (nonHumanAnimal == null) {
            onModifyNonHumanAnimal(0)
        } else {
            localNonHumanAnimalList[localNonHumanAnimalList.indexOf(nonHumanAnimal)] =
                nonHumanAnimalEntity
            onModifyNonHumanAnimal(1)
        }
    }

    override suspend fun deleteNonHumanAnimal(
        id: String,
        onDeleteNonHumanAnimal: (rowsDeleted: Int) -> Unit
    ) {
        val nonHumanAnimal =
            localNonHumanAnimalList.firstOrNull { it.id == id }
        if (nonHumanAnimal == null) {
            onDeleteNonHumanAnimal(0)
        } else {
            localNonHumanAnimalList.remove(nonHumanAnimal)
            onDeleteNonHumanAnimal(1)
        }
    }

    override suspend fun deleteAllNonHumanAnimals(
        caregiverId: String,
        onDeleteAllNonHumanAnimals: (rowsDeleted: Int) -> Unit
    ) {
        val nonHumanAnimalList = localNonHumanAnimalList.filter { it.caregiverId == caregiverId }
        if (nonHumanAnimalList.isEmpty()) {
            onDeleteAllNonHumanAnimals(0)
        } else {
            localNonHumanAnimalList.removeAll(nonHumanAnimalList)
            onDeleteAllNonHumanAnimals(1)
        }
    }

    override suspend fun getNonHumanAnimal(
        id: String,
    ): NonHumanAnimalEntity? = localNonHumanAnimalList.firstOrNull { it.id == id }

    override fun getAllNonHumanAnimals(caregiverId: String): Flow<List<NonHumanAnimalEntity>> =
        flowOf(localNonHumanAnimalList.filter { it.caregiverId == caregiverId })
}
