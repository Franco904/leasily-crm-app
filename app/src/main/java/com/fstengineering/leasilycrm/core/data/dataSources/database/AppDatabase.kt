package com.fstengineering.leasilycrm.core.data.dataSources.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fstengineering.leasilycrm.core.data.dataSources.database.converters.LocalDateTimeTypeConverter
import com.fstengineering.leasilycrm.core.data.dataSources.database.converters.LocalDateTypeConverter

@Database(
    version = AppDatabase.DB_VERSION,
)
@TypeConverters(
    value = [
        LocalDateTimeTypeConverter::class,
        LocalDateTypeConverter::class,
    ]
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "leasily_app_db"

        fun buildDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                context = appContext,
                klass = AppDatabase::class.java,
                name = DB_NAME,
            )
                .build()
        }

        fun buildInMemoryDatabase(appContext: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context = appContext,
                klass = AppDatabase::class.java,
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
