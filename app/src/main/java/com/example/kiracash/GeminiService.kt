package com.example.kiracash

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeminiService {
    @Headers("Content-Type: application/json")
    @POST("generateContent")
    suspend fun generateContent(@Body request: GeminiRequest): GeminiResponse

    companion object {
        private const val BASE_URL = "https://api.gemini.example.com/"

        fun create(): GeminiService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val client = OkHttpClient.Builder().addInterceptor(logger).build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeminiService::class.java)
        }
    }
}

data class GeminiRequest(val modelName: String, val apiKey: String, val content: List<Content>)
data class Content(val image: String?, val text: String?)
data class GeminiResponse(val items: List<GeminiItem>)
data class GeminiItem(val name: String, val price: Double)
