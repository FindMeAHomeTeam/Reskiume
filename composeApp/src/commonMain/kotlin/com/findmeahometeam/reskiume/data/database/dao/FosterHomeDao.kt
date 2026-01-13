package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalGenderEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalTypeEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import kotlinx.coroutines.flow.Flow

@Dao
interface FosterHomeDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertFosterHome(fosterHomeEntity: FosterHomeEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertAcceptedNonHumanAnimalTypeForFosterHome(acceptedNonHumanAnimalType: AcceptedNonHumanAnimalTypeEntityForFosterHome): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertAcceptedNonHumanAnimalGenderForFosterHome(acceptedNonHumanAnimalGender: AcceptedNonHumanAnimalGenderEntityForFosterHome): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertResidentNonHumanAnimalIdForFosterHome(residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome): Long

    @Update
    suspend fun modifyFosterHome(fosterHomeEntity: FosterHomeEntity): Int

    @Update
    suspend fun modifyAcceptedNonHumanAnimalTypeForFosterHome(acceptedNonHumanAnimalType: AcceptedNonHumanAnimalTypeEntityForFosterHome): Int

    @Update
    suspend fun modifyAcceptedNonHumanAnimalGenderForFosterHome(acceptedNonHumanAnimalGender: AcceptedNonHumanAnimalGenderEntityForFosterHome): Int

    @Update
    suspend fun modifyResidentNonHumanAnimalIdForFosterHome(residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome): Int

    @Query("DELETE FROM FosterHomeEntity WHERE id = :id")
    suspend fun deleteFosterHome(id: String): Int

    @Query("DELETE FROM FosterHomeEntity WHERE ownerId = :ownerId OR savedBy = :ownerId OR savedBy = '' ")
    suspend fun deleteAllFosterHomes(ownerId: String): Int

    @Transaction
    @Query("SELECT * FROM FosterHomeEntity WHERE id = :id")
    suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData?

    @Transaction
    @Query("SELECT * FROM FosterHomeEntity WHERE ownerId = :ownerId")
    fun getAllFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>
}
