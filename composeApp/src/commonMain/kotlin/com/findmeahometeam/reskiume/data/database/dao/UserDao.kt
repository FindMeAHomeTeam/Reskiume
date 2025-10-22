package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.findmeahometeam.reskiume.data.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun modifyUser(user: UserEntity): Int

    @Query("DELETE FROM UserEntity WHERE uid = :uid")
    suspend fun deleteUser(uid: String): Int

    @Query("SELECT * FROM UserEntity WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?
}
