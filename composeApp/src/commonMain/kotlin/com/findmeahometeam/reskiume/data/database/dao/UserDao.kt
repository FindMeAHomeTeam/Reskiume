package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.user.SubscriptionEntityForUser
import com.findmeahometeam.reskiume.data.database.entity.user.UserEntity
import com.findmeahometeam.reskiume.data.database.entity.user.UserWithAllSubscriptionData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertSubscription(subscriptionEntityForUser: SubscriptionEntityForUser): Long

    @Update
    suspend fun modifyUser(user: UserEntity): Int

    @Query("DELETE FROM UserEntity WHERE uid = :uid OR savedBy = :uid OR savedBy = ' ' OR savedBy = '' ")
    suspend fun deleteUsers(uid: String): Int

    @Query("DELETE FROM SubscriptionEntityForUser WHERE subscriptionId = :subscriptionId ")
    suspend fun deleteSubscription(subscriptionId: String): Int

    @Transaction
    @Query("SELECT * FROM UserEntity WHERE uid = :uid")
    fun getUser(uid: String): Flow<UserWithAllSubscriptionData?>

    @Transaction
    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flow<List<UserWithAllSubscriptionData>>
}
