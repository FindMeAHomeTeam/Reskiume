package com.findmeahometeam.reskiume.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.findmeahometeam.reskiume.data.database.dao.UserDao
import com.findmeahometeam.reskiume.data.database.entity.UserEntity

const val DATABASE_NAME = "reskiume_database.db"

@Database(entities = [UserEntity::class], version = 1)
@ConstructedBy(ReskiumeConstructor::class)
abstract class ReskiumeDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ReskiumeConstructor : RoomDatabaseConstructor<ReskiumeDatabase> {
    override fun initialize(): ReskiumeDatabase
}
