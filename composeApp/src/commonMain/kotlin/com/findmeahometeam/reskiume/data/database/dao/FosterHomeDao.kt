package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeWithAllNonHumanAnimalData
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import kotlinx.coroutines.flow.Flow

@Dao
interface FosterHomeDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertFosterHome(fosterHomeEntity: FosterHomeEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertAcceptedNonHumanAnimalForFosterHome(acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertResidentNonHumanAnimalIdForFosterHome(residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome): Long

    @Update
    suspend fun modifyFosterHome(fosterHomeEntity: FosterHomeEntity): Int

    @Update
    suspend fun modifyAcceptedNonHumanAnimalForFosterHome(acceptedNonHumanAnimal: AcceptedNonHumanAnimalEntityForFosterHome): Int

    @Update
    suspend fun modifyResidentNonHumanAnimalIdForFosterHome(residentNonHumanAnimalId: ResidentNonHumanAnimalIdEntityForFosterHome): Int

    @Query("DELETE FROM FosterHomeEntity WHERE id = :id")
    suspend fun deleteFosterHome(id: String): Int

    @Query("DELETE FROM AcceptedNonHumanAnimalEntityForFosterHome WHERE acceptedNonHumanAnimalId = :acceptedNonHumanAnimalId")
    suspend fun deleteAcceptedNonHumanAnimalEntityForFosterHome(acceptedNonHumanAnimalId: Long): Int

    @Query("DELETE FROM ResidentNonHumanAnimalIdEntityForFosterHome WHERE nonHumanAnimalId = :nonHumanAnimalId")
    suspend fun deleteResidentNonHumanAnimalIdEntityForFosterHome(nonHumanAnimalId: String): Int

    @Query("DELETE FROM FosterHomeEntity WHERE ownerId = :ownerId OR savedBy = :ownerId OR savedBy = '' ")
    suspend fun deleteAllMyFosterHomes(ownerId: String): Int

    @Transaction
    @Query("SELECT * FROM FosterHomeEntity WHERE id = :id")
    suspend fun getFosterHome(id: String): FosterHomeWithAllNonHumanAnimalData?

    @Transaction
    @Query("SELECT * FROM FosterHomeEntity WHERE ownerId = :ownerId")
    fun getAllMyFosterHomes(ownerId: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    @Transaction
    @Query("SELECT * FROM FosterHomeEntity WHERE country = :country AND city = :city")
    fun getAllFosterHomesByCountryAndCity(country: String, city: String): Flow<List<FosterHomeWithAllNonHumanAnimalData>>

    @Transaction
    @Query("SELECT * FROM FosterHomeEntity WHERE " +
            "longitude >= :activistLongitude - :rangeLongitude AND longitude <= :activistLongitude + :rangeLongitude " +
            "AND latitude >= :activistLatitude - :rangeLatitude AND latitude <= :activistLatitude + :rangeLatitude")
    fun getAllFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<FosterHomeWithAllNonHumanAnimalData>>
}
