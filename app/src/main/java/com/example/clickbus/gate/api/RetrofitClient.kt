package com.example.clickbus.gate.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 é o alias especial do emulador Android para o "localhost"
    // da maquina onde o emulador esta rodando (onde a API Java esta no ar).
    // Se for testar em CELULAR FISICO na mesma rede Wi-Fi, troque pelo IP
    // da sua maquina (ex: "http://192.168.0.15:8080/").
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ClickBusApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClickBusApiService::class.java)
    }
}