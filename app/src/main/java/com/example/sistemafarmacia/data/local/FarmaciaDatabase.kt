package com.example.sistemafarmacia.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MedicamentoEntity::class, CartItemEntity::class, UserEntity::class, PedidoEntity::class, AppMetadataEntity::class],
    version = 4,
    exportSchema = true
)
abstract class FarmaciaDatabase : RoomDatabase() {

    abstract fun farmaciaDao(): FarmaciaDao

    companion object {
        // Compatibilidad con las versiones que pueden seguir instaladas.
        val MIGRACION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        username TEXT NOT NULL PRIMARY KEY,
                        passwordHash TEXT NOT NULL, salt TEXT NOT NULL,
                        fullName TEXT NOT NULL, email TEXT NOT NULL,
                        role TEXT NOT NULL, direccionEnvio TEXT NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS pedidos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL, fechaFormatted TEXT NOT NULL,
                        totalFormatted TEXT NOT NULL, itemsSummary TEXT NOT NULL,
                        direccionEnvio TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE cart_items_new (
                        medicamentoId INTEGER NOT NULL PRIMARY KEY,
                        quantity INTEGER NOT NULL,
                        FOREIGN KEY(medicamentoId) REFERENCES medicamentos(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                // Las referencias huérfanas de la base anterior no pasan al nuevo carrito.
                db.execSQL("""
                    INSERT INTO cart_items_new (medicamentoId, quantity)
                    SELECT c.medicamentoId, c.quantity
                    FROM cart_items c INNER JOIN medicamentos m ON m.id = c.medicamentoId
                    WHERE c.quantity > 0
                """.trimIndent())
                db.execSQL("DROP TABLE cart_items")
                db.execSQL("ALTER TABLE cart_items_new RENAME TO cart_items")
                db.execSQL("CREATE TABLE IF NOT EXISTS app_metadata (`key` TEXT NOT NULL PRIMARY KEY, value TEXT NOT NULL)")
                // Un inventario vacío no debe recuperar los productos de demostración.
                db.execSQL("INSERT OR IGNORE INTO app_metadata (`key`, value) VALUES ('initial_data_seeded', 'true')")
            }
        }

        @Volatile
        private var instancia: FarmaciaDatabase? = null

        fun obtenerInstancia(contexto: Context): FarmaciaDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    FarmaciaDatabase::class.java,
                    "farmacia_database"
                )
                    .addMigrations(MIGRACION_1_3, MIGRACION_3_4)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}
