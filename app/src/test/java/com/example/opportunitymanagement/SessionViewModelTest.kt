package com.example.opportunitymanagement

import com.example.opportunitymanagement.data.local.Role
import com.example.opportunitymanagement.data.local.SesionDao
import com.example.opportunitymanagement.data.local.SesionEntity
import com.example.opportunitymanagement.data.local.UsuarioEntity
import com.example.opportunitymanagement.data.repository.SessionRepository
import com.example.opportunitymanagement.domain.usecase.CerrarSesionUseCase
import com.example.opportunitymanagement.domain.usecase.ObservarSesionUseCase
import com.example.opportunitymanagement.viewmodel.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val ana = UsuarioEntity(7, "Ana", "ana@ejemplo.cl", Role.VENDEDOR, true, 0, 0)

    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun cleanup() = Dispatchers.resetMain()

    private fun crear(dao: SesionDao): SessionViewModel {
        val repo = SessionRepository(dao)
        return SessionViewModel(ObservarSesionUseCase(repo), CerrarSesionUseCase(repo))
    }

    @Test
    fun verificaHastaQueRoomResponde() = runTest(dispatcher) {
        val emisiones = MutableSharedFlow<UsuarioEntity?>()
        val vm = crear(FakeSesionDao(emisiones))
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertTrue(vm.uiState.value.verificando)
        emisiones.emit(ana)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.verificando)
        assertEquals("Ana", vm.uiState.value.usuario?.nombre)
    }

    @Test
    fun sinSesionGuardadaVaAlLogin() = runTest(dispatcher) {
        val vm = crear(FakeSesionDao(MutableStateFlow(null)))
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertFalse(vm.uiState.value.verificando)
        assertNull(vm.uiState.value.usuario)
    }

    @Test
    fun cerrarSesionEmiteSinUsuario() = runTest(dispatcher) {
        val vm = crear(FakeSesionDao(MutableStateFlow(ana)))
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertNotNull(vm.uiState.value.usuario)
        vm.cerrarSesion()
        advanceUntilIdle()
        assertNull(vm.uiState.value.usuario)
        assertFalse(vm.uiState.value.verificando)
    }

    @Test
    fun errorAlLeerLaSesionNoBloqueaLaApp() = runTest(dispatcher) {
        val vm = crear(FakeSesionDao(flow { error("disco") }))
        backgroundScope.launch { vm.uiState.collect {} }
        advanceUntilIdle()
        assertFalse(vm.uiState.value.verificando)
        assertNull(vm.uiState.value.usuario)
    }

    private class FakeSesionDao(private val usuario: Flow<UsuarioEntity?>) : SesionDao {
        override fun usuarioActual() = usuario
        override suspend fun guardar(sesion: SesionEntity) = error("no se usa")
        override suspend fun cerrar() {
            (usuario as? MutableStateFlow<UsuarioEntity?>)?.value = null
        }
    }
}
