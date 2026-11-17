package com.example.opportunitymanagement.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * Microservicio Spring Boot ejecutándose en el computador de desarrollo.
     * 10.0.2.2 equivale a localhost desde el emulador de Android.
     * En un teléfono físico, reemplazar por la IP del computador en la red local
     * y agregarla a res/xml/network_security_config.xml.
     */
    const val BASE_URL = "http://10.0.2.2:8080/"

    /** [baseUrl] debe terminar en "/". Las pruebas lo apuntan a un servidor simulado. */
    fun crear(baseUrl: String = BASE_URL): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val oportunidadApi: OportunidadApi by lazy {
        crear().create(OportunidadApi::class.java)
    }
}
