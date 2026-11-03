package com.example.sistemafarmacia.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmaciaDao {
    @Query("SELECT * FROM medicamentos ORDER BY id ASC")
    fun observeMedicamentos(): Flow<List<MedicamentoEntity>>

    @Query("SELECT COUNT(*) FROM medicamentos")
    suspend fun getMedicamentosCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicamentos(medicamentos: List<MedicamentoEntity>)

    @Query("SELECT * FROM cart_items")
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE medicamentoId = :medicamentoId")
    suspend fun deleteCartItem(medicamentoId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
