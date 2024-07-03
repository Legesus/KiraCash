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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class GeminiResponse(
    val items: List<Item>
)

class ImageProcessor(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val itemDao = db.itemDao()
    private var jsonString = ""
    private val apiKey: String

    init {
        val appContext = context.applicationContext
        val sharedPreferences = appContext.getSharedPreferences("KiraCashPreferences", Context.MODE_PRIVATE)
        if (sharedPreferences.contains(GEMINI_API_KEY_PREF)) {
            Log.d("ImageProcessor", "API Key exists")
        } else {
            Log.d("ImageProcessor", "API Key does not exist")
        }
        apiKey = sharedPreferences.getString(GEMINI_API_KEY_PREF, BuildConfig.GEMINI_API_KEY) ?: BuildConfig.GEMINI_API_KEY
        Log.d("ImageProcessor", "API Key retrieved: $apiKey")
    }

    val bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.testreceipt)
    val itemsState = mutableStateOf<List<Item>>(emptyList())

    suspend fun processImage(bitmap: Bitmap): List<Item> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ImageProcessor", "Starting image processing")
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-pro",
                    apiKey = apiKey
                )

                val inputContent = content {
                    image(bitmap)
                    text("Parse the following receipt and return a JSON list of items with the format: {\"items\": [{\"name\": \"Item1\", \"price\": 5.99}, {\"name\": \"Item2\", \"price\": 3.49}, ...]}. Ensure to handle item quantity correctly: if an item appears with a quantity greater than 1, list the item that many times with the individual price. Quantity is usually represented by 'Qty' or appears at the beginning of the line. Ignore long strings of numbers as they may be inventory IDs. If there's any text following a '#' symbol, it should be ignored as it usually refers to item details. Comments left by the cashier, often marked with '**', are not important and should be ignored. If there's an '@' symbol followed by a price, use that instead of dividing the total price by quantity.\n" +
                            "\n" +
                            "Here are examples of different receipt formats and how they should be interpreted:\n" +
                            "\n" +
                            "Format 1:\n" +
                            "4 Roast C.Rice (N) @8.00\n" +
                            "1 Lemon C.Rice 9.00\n" +
                            "1 Steam C.RIce (N) 8.00\n" +
                            "1 Cola 2.90\n" +
                            "1 Barley Tang Shui 2.10\n" +
                            "1 Steam Wing-1Pcs 3.00\n" +
                            "1 100 Plus 2.90\n" +
                            "1 Green Bean Tang Shui 2.10\n" +
                            "\n" +
                            "Should be interpreted as:\n" +
                            "\n" +
                            "{\n" +
                            "\"items\": [\n" +
                            "{\"name\": \"Roast C.Rice (N)\", \"price\": 8.00},\n" +
                            "{\"name\": \"Roast C.Rice (N)\", \"price\": 8.00},\n" +
                            "{\"name\": \"Roast C.Rice (N)\", \"price\": 8.00},\n" +
                            "{\"name\": \"Roast C.Rice (N)\", \"price\": 8.00},\n" +
                            "{\"name\": \"Lemon C.Rice\", \"price\": 9.00},\n" +
                            "{\"name\": \"Steam C.RIce (N)\", \"price\": 8.00},\n" +
                            "{\"name\": \"Cola\", \"price\": 2.90},\n" +
                            "{\"name\": \"Barley Tang Shui\", \"price\": 2.10},\n" +
                            "{\"name\": \"Steam Wing-1Pcs\", \"price\": 3.00},\n" +
                            "{\"name\": \"100 Plus\", \"price\": 2.90},\n" +
                            "{\"name\": \"Green Bean Tang Shui\", \"price\": 2.10}\n" +
                            "]\n" +
                            "}\n" +
                            "\n" +
                            "Format 2:\n" +
                            "NO PARTICULARS QUANTITY AMOUNT\n" +
                            "1 Chicken Chop 1 14.90\n" +
                            "2 Kuey Tiaw Ayam Goreng 1 8.00\n" +
                            "3 N. G. Pattaya 1 8.00\n" +
                            "4 C.K.T Ayam 1 7.30\n" +
                            "5 Sky Juice 4 0.80 (0.20 each)\n" +
                            "6 Sprite 1 2.30\n" +
                            "7 Ice Lemon Tea 1 2.80\n" +
                            "\n" +
                            "Should be interpreted as:\n" +
                            "\n" +
                            "{\n" +
                            "\"items\": [\n" +
                            "{\"name\": \"Chicken Chop\", \"price\": 14.90},\n" +
                            "{\"name\": \"Kuey Tiaw Ayam Goreng\", \"price\": 8.00},\n" +
                            "{\"name\": \"N. G. Pattaya\", \"price\": 8.00},\n" +
                            "{\"name\": \"C.K.T Ayam\", \"price\": 7.30},\n" +
                            "{\"name\": \"Sky Juice\", \"price\": 0.20},\n" +
                            "{\"name\": \"Sky Juice\", \"price\": 0.20},\n" +
                            "{\"name\": \"Sky Juice\", \"price\": 0.20},\n" +
                            "{\"name\": \"Sky Juice\", \"price\": 0.20},\n" +
                            "{\"name\": \"Sprite\", \"price\": 2.30},\n" +
                            "{\"name\": \"Ice Lemon Tea\", \"price\": 2.80}\n" +
                            "]\n" +
                            "}")
                }

                Log.d("ImageProcessor", "Sending request to generative model")
                val response = generativeModel.generateContent(inputContent)

                Log.d("ImageProcessor", "Received raw response: ${response.toString()}")
                val jsonString = response.text
                Log.d("ImageProcessor", "Extracted JSON string: $jsonString")
                val cleanJson = jsonString?.replace("```json|```".toRegex(), "")
                Log.d("ImageProcessor", "Clean JSON string: $cleanJson")

                val geminiResponse = Gson().fromJson(cleanJson, GeminiResponse::class.java)

                Log.d("ImageProcessor", "Parsed response: $geminiResponse")
                val items = geminiResponse.items.map { geminiItem ->
                    val item = Item(name = geminiItem.name, price = geminiItem.price)
                    val id = itemDao.insert(item)
                    item.copy(id = id.toInt())
                }

                Log.d("ImageProcessor", "AI Items: $items")
                itemsState.value = items
                Log.d("ImageProcessor", "AI ItemState: ${itemsState.value}")

                items
            } catch (e: Exception) {
                Log.e("ImageProcessor", "Error during image processing", e)
                emptyList<Item>()
            }
        }
    }

    fun getJsonString(): String {
        return try {
            jsonString
        } catch (e: Exception) {
            Log.e("ImageProcessor", "Failed to generate JSON string", e)
            "{}"
        }
    }
}
