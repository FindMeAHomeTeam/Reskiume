package com.findmeahometeam.reskiume.data.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import androidx.room3.Update
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NonHumanAnimalDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity): Long

    @Update
    suspend fun modifyNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity): Int

    @Query("DELETE FROM NonHumanAnimalEntity WHERE id = :id")
    suspend fun deleteNonHumanAnimal(id: String): Int

    @Query("DELETE FROM NonHumanAnimalEntity WHERE caregiverId = :caregiverId OR savedBy = :caregiverId OR savedBy = ' ' OR savedBy = '' ")
    suspend fun deleteAllNonHumanAnimals(caregiverId: String): Int

    @Query("SELECT * FROM NonHumanAnimalEntity WHERE id = :id")
    suspend fun getNonHumanAnimal(id: String): NonHumanAnimalEntity?

    @Query("SELECT * FROM NonHumanAnimalEntity WHERE caregiverId = :caregiverId")
    fun getAllMyNonHumanAnimals(caregiverId: String): Flow<List<NonHumanAnimalEntity>>

    @Query("SELECT * FROM NonHumanAnimalEntity")
    fun getAllNonHumanAnimals(): Flow<List<NonHumanAnimalEntity>>
}
