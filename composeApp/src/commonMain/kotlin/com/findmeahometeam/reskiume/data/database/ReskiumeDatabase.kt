package com.findmeahometeam.reskiume.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.findmeahometeam.reskiume.data.database.dao.FosterHomeDao
import com.findmeahometeam.reskiume.data.database.dao.LocalCacheDao
import com.findmeahometeam.reskiume.data.database.dao.NonHumanAnimalDao
import com.findmeahometeam.reskiume.data.database.dao.ReviewDao
import com.findmeahometeam.reskiume.data.database.dao.UserDao
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.AcceptedNonHumanAnimalEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.FosterHomeEntity
import com.findmeahometeam.reskiume.data.database.entity.LocalCacheEntity
import com.findmeahometeam.reskiume.data.database.entity.NonHumanAnimalEntity
import com.findmeahometeam.reskiume.data.database.entity.fosterHome.ResidentNonHumanAnimalIdEntityForFosterHome
import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import com.findmeahometeam.reskiume.data.database.entity.UserEntity

const val DATABASE_NAME = "reskiume_database.db"

@Database(
    entities = [
        LocalCacheEntity::class,
        UserEntity::class,
        ReviewEntity::class,
        NonHumanAnimalEntity::class,
        FosterHomeEntity::class,
        AcceptedNonHumanAnimalEntityForFosterHome::class,
        ResidentNonHumanAnimalIdEntityForFosterHome::class
    ], version = 1
)
@ConstructedBy(ReskiumeConstructor::class)
abstract class ReskiumeDatabase : RoomDatabase() {
    abstract fun getLocalCacheDao(): LocalCacheDao
    abstract fun getUserDao(): UserDao
    abstract fun getReviewDao(): ReviewDao
    abstract fun getNonHumanAnimalDao(): NonHumanAnimalDao
    abstract fun getFosterHomeDao(): FosterHomeDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ReskiumeConstructor : RoomDatabaseConstructor<ReskiumeDatabase> {
    override fun initialize(): ReskiumeDatabase
}
