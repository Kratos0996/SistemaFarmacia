package com.example.sistemafarmacia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sistemafarmacia.data.repository.FarmaciaRepository
import com.example.sistemafarmacia.model.Medicamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FarmaciaViewModel(
    private val repository: FarmaciaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val cartQuantity: StateFlow<Map<Int, Int>> = repository.cartQuantity.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

    val listaMedicamentos: StateFlow<List<Medicamento>> = repository.medicamentos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val medicamentosFiltrados: StateFlow<List<Medicamento>> = combine(
        listaMedicamentos,
        _searchQuery,
        _selectedCategory
    ) { list, query, category ->
        list.filter { med ->
            val matchesSearch = med.nombre.contains(query, ignoreCase = true) ||
                    med.descripcion.contains(query, ignoreCase = true)
            val matchesCategory = category == null || med.categoria.equals(category, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun initDefaultData(defaultList: List<Medicamento>) {
        viewModelScope.launch {
            repository.ensureInitialData(defaultList)
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelect(categoryName: String) {
        _selectedCategory.value = if (_selectedCategory.value == categoryName) null else categoryName
    }

    fun increaseQuantity(medicamentoId: Int) {
        viewModelScope.launch {
            val current = cartQuantity.value[medicamentoId] ?: 0
            repository.increaseQuantity(medicamentoId, current)
        }
    }

    fun decreaseQuantity(medicamentoId: Int) {
        viewModelScope.launch {
            val current = cartQuantity.value[medicamentoId] ?: 0
            repository.decreaseQuantity(medicamentoId, current)
        }
    }
}

class FarmaciaViewModelFactory(
    private val repository: FarmaciaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(FarmaciaViewModel::class.java))
        return FarmaciaViewModel(repository) as T
    }
}
