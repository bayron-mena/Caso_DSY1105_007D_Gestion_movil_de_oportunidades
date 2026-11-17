package com.example.opportunitymanagement

import com.example.opportunitymanagement.data.local.OportunidadEntity
import com.example.opportunitymanagement.data.remote.OportunidadApi
import com.example.opportunitymanagement.data.remote.RetrofitClient
import com.example.opportunitymanagement.data.repository.OportunidadRemoteRepository
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

/** Verifica cada operación REST contra un servidor HTTP simulado. */
class OportunidadRemoteRepositoryTest {
    private val server = MockWebServer()
    private lateinit var repository: OportunidadRemoteRepository

    private val json = """
        {"id":7,"nombre":"Feria de prueba","tipo":"Feria empresarial",
         "descripcion":"Demo","fuente":"Sitio web","prioridad":"Alta",
         "potencial":"Medio","estado":"Evento identificado",
         "fechaDeteccion":1000,"ultimaActualizacion":2000}
    """.trimIndent()

    @Before fun setup() {
        server.start()
        val api = RetrofitClient.crear(server.url("/").toString())
            .create(OportunidadApi::class.java)
        repository = OportunidadRemoteRepository(api)
    }

    @After fun cleanup() { server.shutdown() }

    @Test fun listarUsaGet() = runTest {
        server.enqueue(MockResponse().setBody("[$json]"))

        val items = repository.listar()

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/oportunidades", request.path)
        assertEquals(listOf(7L), items.map { it.id })
        assertEquals("Feria de prueba", items.single().nombre)
        // Los campos opcionales ausentes no deben romper la conversión.
        assertEquals("", items.single().observaciones)
        assertEquals(1000L, items.single().fechaDeteccion)
    }

    @Test fun buscarUsaGetConId() = runTest {
        server.enqueue(MockResponse().setBody(json))

        val item = repository.buscar(7)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/oportunidades/7", request.path)
        assertEquals(7L, item.id)
    }

    @Test fun crearUsaPostSinId() = runTest {
        server.enqueue(MockResponse().setResponseCode(201).setBody(json))

        val creada = repository.crear(oportunidad(id = 0))

        val request = server.takeRequest()
        val body = request.body.readUtf8()
        assertEquals("POST", request.method)
        assertEquals("/api/oportunidades", request.path)
        assertTrue(body.contains("\"nombre\":\"Feria de prueba\""))
        assertFalse(body.contains("\"id\""))
        assertEquals(7L, creada.id)
    }

    @Test fun actualizarUsaPutConId() = runTest {
        server.enqueue(MockResponse().setBody(json))

        repository.actualizar(oportunidad(id = 7))

        val request = server.takeRequest()
        assertEquals("PUT", request.method)
        assertEquals("/api/oportunidades/7", request.path)
        assertTrue(request.body.readUtf8().contains("\"id\":7"))
    }

    @Test fun eliminarUsaDelete() = runTest {
        server.enqueue(MockResponse().setResponseCode(204))

        repository.eliminar(7)

        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/oportunidades/7", request.path)
    }

    @Test fun eliminarInexistenteLanzaHttpException() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))

        try {
            repository.eliminar(99)
            fail("Debía lanzar HttpException")
        } catch (exception: HttpException) {
            assertEquals(404, exception.code())
        }
    }

    private fun oportunidad(id: Long) = OportunidadEntity(
        id = id,
        nombre = "Feria de prueba",
        tipo = "Feria empresarial",
        descripcion = "Demo",
        fuente = "Sitio web",
        prioridad = "Alta",
        potencial = "Medio"
    )
}
