package com.example.sistemafarmacia.data.repository

import com.example.sistemafarmacia.data.local.CartItemEntity
import com.example.sistemafarmacia.data.local.FarmaciaDao
import com.example.sistemafarmacia.data.local.MedicamentoEntity
import com.example.sistemafarmacia.model.Medicamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FarmaciaRepository(
    private val dao: FarmaciaDao
) {
    val medicamentos: Flow<List<Medicamento>> = dao.observeMedicamentos().map { entities ->
        entities.map { it.toModel() }
    }

    val cartQuantity: Flow<Map<Int, Int>> = dao.observeCartItems().map { items ->
        items.associate { it.medicamentoId to it.quantity }
    }

    suspend fun ensureInitialData(defaultMedicamentos: List<Medicamento>) {
        if (dao.getMedicamentosCount() == 0) {
            val entities = defaultMedicamentos.map { it.toEntity() }
            dao.insertMedicamentos(entities)
        }
    }

    suspend fun increaseQuantity(medicamentoId: Int, currentQuantity: Int) {
        val newQuantity = currentQuantity + 1
        dao.upsertCartItem(CartItemEntity(medicamentoId, newQuantity))
    }

    suspend fun decreaseQuantity(medicamentoId: Int, currentQuantity: Int) {
        if (currentQuantity > 1) {
            dao.upsertCartItem(CartItemEntity(medicamentoId, currentQuantity - 1))
        } else {
            dao.deleteCartItem(medicamentoId)
        }
    }
}

private fun MedicamentoEntity.toModel(): Medicamento {
    return Medicamento(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        calificacion = calificacion,
        enOferta = enOferta,
        imagenEmoji = imagenEmoji,
        imagenRes = imagenRes,
        categoria = categoria
    )
}

private fun Medicamento.toEntity(): MedicamentoEntity {
    return MedicamentoEntity(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        calificacion = calificacion,
        enOferta = enOferta,
        imagenEmoji = imagenEmoji,
        imagenRes = imagenRes,
        categoria = categoria
    )
}
