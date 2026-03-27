package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NeedToCoverEntityForRescueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.NonHumanAnimalToRescueEntityForRescueEvent
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventEntity
import com.findmeahometeam.reskiume.data.database.entity.rescueEvent.RescueEventWithAllNeedsAndNonHumanAnimalData
import kotlinx.coroutines.flow.Flow

@Dao
interface RescueEventDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertRescueEvent(rescueEventEntity: RescueEventEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertNonHumanAnimalToRescueEntityForRescueEvent(
        nonHumanAnimalToRescueEntityForRescueEvent: NonHumanAnimalToRescueEntityForRescueEvent
    ): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertNeedToCoverEntityForRescueEvent(needToCoverEntityForRescueEvent: NeedToCoverEntityForRescueEvent): Long

    @Update
    suspend fun modifyRescueEvent(rescueEventEntity: RescueEventEntity): Int

    @Query("DELETE FROM RescueEventEntity WHERE id = :id")
    suspend fun deleteRescueEvent(id: String): Int

    @Query("DELETE FROM NonHumanAnimalToRescueEntityForRescueEvent WHERE nonHumanAnimalId = :nonHumanAnimalId")
    suspend fun deleteNonHumanAnimalToRescueEntityForRescueEvent(nonHumanAnimalId: String): Int

    @Query("DELETE FROM NeedToCoverEntityForRescueEvent WHERE needToCoverId = :needToCoverId")
    suspend fun deleteNeedToCoverEntityForRescueEvent(needToCoverId: String): Int

    @Query("DELETE FROM RescueEventEntity WHERE creatorId = :creatorId OR savedBy = :creatorId OR savedBy = ' ' OR savedBy = '' ")
    suspend fun deleteAllMyRescueEvents(creatorId: String): Int

    @Transaction
    @Query("SELECT * FROM RescueEventEntity WHERE id = :id")
    suspend fun getRescueEvent(id: String): RescueEventWithAllNeedsAndNonHumanAnimalData?

    @Transaction
    @Query("SELECT * FROM RescueEventEntity WHERE creatorId = :creatorId")
    fun getAllMyRescueEvents(creatorId: String): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>

    @Transaction
    @Query("SELECT * FROM RescueEventEntity")
    fun getAllRescueEvents(): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>

    @Transaction
    @Query("SELECT * FROM RescueEventEntity WHERE country = :country AND city = :city")
    fun getAllRescueEventsByCountryAndCity(
        country: String,
        city: String
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>

    @Transaction
    @Query(
        "SELECT * FROM RescueEventEntity WHERE " +
                "longitude >= :activistLongitude - :rangeLongitude AND longitude <= :activistLongitude + :rangeLongitude " +
                "AND latitude >= :activistLatitude - :rangeLatitude AND latitude <= :activistLatitude + :rangeLatitude"
    )
    fun getAllRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double
    ): Flow<List<RescueEventWithAllNeedsAndNonHumanAnimalData>>
}
