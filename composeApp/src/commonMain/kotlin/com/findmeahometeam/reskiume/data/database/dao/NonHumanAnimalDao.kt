package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NonHumanAnimalDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity): Long

    @Update
    suspend fun modifyNonHumanAnimal(nonHumanAnimalEntity: NonHumanAnimalEntity): Int

    @Query("DELETE FROM NonHumanAnimalEntity WHERE id = :id AND caregiverId = :caregiverId")
    suspend fun deleteNonHumanAnimal(id: String, caregiverId: String): Int

    @Query("DELETE FROM NonHumanAnimalEntity WHERE caregiverId = :caregiverId OR savedBy = :caregiverId OR savedBy = '' ")
    suspend fun deleteAllNonHumanAnimals(caregiverId: String): Int

    @Query("SELECT * FROM NonHumanAnimalEntity WHERE id = :id")
    suspend fun getNonHumanAnimal(id: String): NonHumanAnimalEntity?

    @Query("SELECT * FROM NonHumanAnimalEntity WHERE caregiverId = :caregiverId")
    fun getAllNonHumanAnimals(caregiverId: String): Flow<List<NonHumanAnimalEntity>>
}
