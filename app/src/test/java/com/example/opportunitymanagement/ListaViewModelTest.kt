package com.example.opportunitymanagement

import androidx.lifecycle.viewModelScope
import com.example.opportunitymanagement.data.local.OportunidadConResponsable
import com.example.opportunitymanagement.data.local.OportunidadDao
import com.example.opportunitymanagement.data.local.OportunidadEntity
import com.example.opportunitymanagement.data.repository.OportunidadRepository
import com.example.opportunitymanagement.viewmodel.ListaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private var vm: ListaViewModel? = null

    @Before fun setup() { Dispatchers.setMain(dispatcher) }

    @After fun cleanup() { vm?.viewModelScope?.cancel(); Dispatchers.resetMain() }

    @Test fun empiezaCargando() {
        vm = ListaViewModel(OportunidadRepository(FakeDao(MutableStateFlow(emptyList()))))
        assertTrue(vm!!.uiState.value.cargando)
    }

    @Test fun muestraLosItemsYSeActualiza() = runTest(dispatcher) {
        val items = MutableStateFlow(listOf(oportunidad(1, "Feria")))
        val viewModel = ListaViewModel(OportunidadRepository(FakeDao(items))).also { vm = it }
        val recolector = backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(listOf("Feria"), viewModel.uiState.value.items.map { it.oportunidad.nombre })

        items.value = items.value + oportunidad(2, "Evento")
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.items.size)
        recolector.cancel()
    }

    @Test fun errorDeLecturaMuestraMensaje() = runTest(dispatcher) {
        val viewModel = ListaViewModel(OportunidadRepository(FakeDao(flow { error("disco") }))).also { vm = it }
        val recolector = backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cargando)
        assertNotNull(viewModel.uiState.value.error)
        recolector.cancel()
    }

    @Test fun eliminarQuitaElItemDeLaLista() = runTest(dispatcher) {
        val items = MutableStateFlow(listOf(oportunidad(1, "Feria"), oportunidad(2, "Evento")))
        val dao = FakeDao(items) { id ->
            val antes = items.value.size
            items.value = items.value.filterNot { it.id == id }
            antes - items.value.size
        }
        val viewModel = ListaViewModel(OportunidadRepository(dao)).also { vm = it }
        val recolector = backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.eliminar(1)
        advanceUntilIdle()

        assertEquals(listOf("Evento"), viewModel.uiState.value.items.map { it.oportunidad.nombre })
        assertNull(viewModel.uiState.value.eliminandoId)
        assertNull(viewModel.uiState.value.mensaje)
        recolector.cancel()
    }

    @Test fun dobleToqueEliminaUnaVez() = runTest(dispatcher) {
        val dao = FakeDao(MutableStateFlow(listOf(oportunidad(1, "Feria"))))
        val viewModel = ListaViewModel(OportunidadRepository(dao)).also { vm = it }

        viewModel.eliminar(1); viewModel.eliminar(1)
        advanceUntilIdle()

        assertEquals(1, dao.eliminaciones)
    }

    @Test fun eliminarInexistenteMuestraMensaje() = runTest(dispatcher) {
        val dao = FakeDao(MutableStateFlow(emptyList())) { 0 }
        val viewModel = ListaViewModel(OportunidadRepository(dao)).also { vm = it }
        val recolector = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.eliminar(99)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.mensaje)
        assertNull(viewModel.uiState.value.eliminandoId)
        recolector.cancel()
    }

    private fun oportunidad(id: Long, nombre: String) =
        OportunidadEntity(id, nombre, "Feria empresarial", "Desc", "Fuente", "Alta", "Alto")

    private class FakeDao(
        private val items: Flow<List<OportunidadEntity>>,
        private val alEliminar: (Long) -> Int = { 1 }
    ) : OportunidadDao {
        var eliminaciones = 0
        override fun observarTodas() =
            items.map { lista -> lista.map { OportunidadConResponsable(it, responsableNombre = null) } }
        override suspend fun buscar(id: Long): OportunidadEntity? = null
        override suspend fun insertar(item: OportunidadEntity): Long = 1
        override suspend fun actualizar(item: OportunidadEntity): Int = 1
        override suspend fun eliminar(id: Long): Int { eliminaciones++; return alEliminar(id) }
    }
}
