package com.example.kiracash

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ImageProcessor(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val geminiService = GeminiService.create()

    suspend fun processImage(bitmap: Bitmap): String {
        var recognizedText = ""
        CoroutineScope(Dispatchers.IO).launch {
            val encodedImage = encodeImage(bitmap)
            val request = GeminiRequest(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY,
                content = listOf(Content(image = encodedImage, text = "Parse the following receipt and return a JSON list of items with the format: {\"items\": [{\"name\": \"Item1\", \"price\": 5.99}, {\"name\": \"Item2\", \"price\": 3.49}, ...]}"))
            )

            try {
                val response = geminiService.generateContent(request)
                val items = parseResponse(response)
                db.itemDao().insertAll(items)
                recognizedText = items.joinToString("\n") { "${it.name}: ${it.price}" }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.join()
        return recognizedText
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun parseResponse(response: GeminiResponse): List<Item> {
        return response.items.map { geminiItem ->
            Item(name = geminiItem.name, price = geminiItem.price)
        }
    }
}
