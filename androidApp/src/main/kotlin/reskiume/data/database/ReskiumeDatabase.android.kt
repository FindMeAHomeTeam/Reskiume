package reskiume.data.database

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.findmeahometeam.reskiume.data.database.DATABASE_NAME
import com.findmeahometeam.reskiume.data.database.ReskiumeDatabase
import kotlinx.coroutines.Dispatchers

fun getDatabase(context: Context): ReskiumeDatabase {
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<ReskiumeDatabase>(context = context, name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations()
        .build()
}
