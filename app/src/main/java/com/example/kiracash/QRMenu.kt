package com.example.kiracash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.model.ReceiptItem
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class OCRActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OCRScreen(navController = rememberNavController())
        }
    }
}

@Composable
fun ReceiptScreen(recognizedText: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = recognizedText ?: "No text recognized",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRScreen(navController: NavHostController) {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(emptyList<ReceiptItem>()) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { imageBitmap ->
            if (imageBitmap != null) {
                val image = InputImage.fromBitmap(imageBitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val resultText = StringBuilder()
                        val tempItems = mutableListOf<ReceiptItem>()


                        // AI-DO Add log.d for the for loops below
                        for (block in visionText.textBlocks) {
                        Log.d("OCRScreen", "Processing block: ${block.text}")
                        for (line in block.lines) {
                            Log.d("OCRScreen", "Processing line: ${line.text}")
                            val lineText = line.text
                            resultText.append(lineText).append("\n")
                            val receiptItem = parseLineToReceiptItem(lineText)
                            if (receiptItem != null) {
                                tempItems.add(receiptItem)
                            }
                        }
                    }
                        recognizedText = resultText.toString()
                        items = tempItems
                    }
                    .addOnFailureListener { e ->
                        Log.e("OCRScreen", "Text recognition failed", e)
                    }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val resultText = StringBuilder()
                        val tempItems = mutableListOf<ReceiptItem>()

                        for (block in visionText.textBlocks) {
                            Log.d("OCRScreen", "Processing block: ${block.text}")
                            for (line in block.lines) {
                                Log.d("OCRScreen", "Processing line: ${line.text}")
                                val lineText = line.text
                                resultText.append(lineText).append("\n")
                                val receiptItem = parseLineToReceiptItem(lineText)
                                if (receiptItem != null) {
                                    tempItems.add(receiptItem)
                                }
                            }
                        }
                        recognizedText = resultText.toString()
                        items = tempItems
                    }
                    .addOnFailureListener { e ->
                        Log.e("OCRScreen", "Text recognition failed", e)
                    }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcher.launch(null)
        } else {
            // Handle permission denial (not implemented here)
        }
    }

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Receipt Scanner") },
                navigationIcon = {
                    IconButton(onClick = { Log.d("OCRScreen", "Menu button clicked") }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1F1B24)
                )
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        launcher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Scan Receipt", color = Color.White)
            }

            // AI-DO Button to upload picture from gallery
            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Upload Picture from Gallery", color = Color.White)
            }

            Button(
                onClick = {

                    // Error handling
                    if (recognizedText.isEmpty()) {
                        return@Button
                    }
                    val route = "receiptScreen/$recognizedText"
                    navController.navigate(route)

                },

                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
            ) {
                Text("Extracted Receipt", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))


            // AI-TO Create a list composable that displays every item in the receipt with its corresponding price

            ReceiptItemsList(items)
        }
    }
}

private fun parseLineToReceiptItem(lineText: String): ReceiptItem? {
    val itemPattern = Regex("""(\d+)\s+(.*?)\s+\(N\)\s+@(\d+\.\d{2})""")
    val matchResult = itemPattern.find(lineText)

    // Log the line of text
    Log.d("parseLineToReceiptItem", "Processing line: $lineText")

    return if (matchResult != null) {
        val (quantity, description, price) = matchResult.destructured

        // Log the parsed values
        Log.d("parseLineToReceiptItem", "Parsed values: quantity=$quantity, description=$description, price=$price")

        ReceiptItem(quantity.toInt(), description, price.toDouble())
    } else {
        // Log that the line could not be parsed
        Log.d("parseLineToReceiptItem", "Could not parse line: $lineText")

        null
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewOCRScreen() {
    val mockNavController = rememberNavController()
    OCRScreen(navController = mockNavController)
}