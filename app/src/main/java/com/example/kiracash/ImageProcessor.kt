package com.example.kiracash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.kiracash.model.AppDatabase
import com.example.kiracash.model.Item
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


data class GeminiResponse(
    val items: List<Item>
)

class ImageProcessor(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val itemDao = db.itemDao()
    private var jsonString = ""

    val bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.testreceipt)
    val itemsState = mutableStateOf<List<Item>>(emptyList())

    suspend fun processImage(bitmap: Bitmap): List<Item> {
        return CoroutineScope(Dispatchers.IO).async {
            Log.d("ImageProcessor", "Starting image processing")
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )

            val inputContent = content {
                image(bitmap)
                text("Parse the following receipt and return a JSON list of items with the format: {\"items\": [{\"name\": \"Item1\", \"price\": 5.99}, {\"name\": \"Item2\", \"price\": 3.49}, ...]}. You also require to keep track of item quantity, if there is 3 quantities  for an item, then you must list down the item 3 times, as if it were only 1 item while also dividing the price but its quantity.")
            }

            try {
                Log.d("ImageProcessor", "Sending request to generative model")
                val response = generativeModel.generateContent(inputContent)
                // Log the raw response for debugging
                Log.d("ImageProcessor", "Received raw response: ${response.toString()}")
                val jsonString = response.text
                Log.d("ImageProcessor", "Extracted JSON string: $jsonString")
                val cleanJson = jsonString?.replace("```json|```".toRegex(), "")
                Log.d("ImageProcessor", "Clean JSON string: $cleanJson")

                // Parse the response
                Log.d("ImageProcessor", "Parsing response")
                val geminiResponse = Gson().fromJson(cleanJson, GeminiResponse::class.java)

                // Log the parsed response for debugging
                Log.d("ImageProcessor", "Parsed response: $geminiResponse")

                // Convert the GeminiItems to Items and insert them into the database
                Log.d("ImageProcessor", "Converting and inserting items into database")

                // Log geminiItems for debugging
                Log.d("ImageProcessor", "GeminiItems: ${geminiResponse.items}")

                // Log geminiItem.name and geminiItem.price for debugging
                geminiResponse.items.forEach { geminiItem ->
                    Log.d("ImageProcessor", "GeminiItem: ${geminiItem.name}, ${geminiItem.price}")
                }

                val items = geminiResponse.items.map { geminiItem -> Item(name = geminiItem.name, price = geminiItem.price) }

                // Log items for debugging
                Log.d("ImageProcessor", "AI Items: $items")

                itemsState.value = items

                // Log itemState for debugging
                Log.d("ImageProcessor", "AI ItemState: ${itemsState.value}")

                items // Return items from the async block

            } catch (e: Exception) {
                Log.e("ImageProcessor", "Error during image processing", e)
                e.printStackTrace()
                emptyList<Item>() // Return an empty list in case of an error
            }
        }.await() // Await the result of the async block
    }

    fun getJsonString(): String {
        try {
            // Your code to generate the JSON string goes here
            // Let's assume you have a variable jsonString that holds the JSON string
            // You would return it like this:
            return jsonString
        } catch (e: Exception) {
            Log.e("ImageProcessor", "Failed to generate JSON string", e)
            return "{}" // Return an empty JSON object as a fallback
        }
    }
}
