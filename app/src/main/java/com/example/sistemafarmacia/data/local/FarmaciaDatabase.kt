package com.example.sistemafarmacia.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MedicamentoEntity::class, CartItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FarmaciaDatabase : RoomDatabase() {

    abstract fun farmaciaDao(): FarmaciaDao

    companion object {
        @Volatile
        private var INSTANCE: FarmaciaDatabase? = null

        fun getDatabase(context: Context): FarmaciaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FarmaciaDatabase::class.java,
                    "farmacia_database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
