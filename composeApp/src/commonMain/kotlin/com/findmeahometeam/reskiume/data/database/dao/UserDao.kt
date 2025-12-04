package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun modifyUser(user: UserEntity): Int

    @Query("DELETE FROM UserEntity WHERE uid = :uid OR savedBy = :uid OR savedBy = '' ")
    suspend fun deleteUsers(uid: String): Int

    @Query("SELECT * FROM UserEntity WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?
}
