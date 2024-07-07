package com.example.kiracash

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kiracash.R.drawable.wheat_1
import com.example.kiracash.R.drawable.wheat_2
import com.example.kiracash.R.drawable.wheat_3
import com.example.kiracash.R.drawable.wheat_4
import com.example.kiracash.R.drawable.wheat_5
import com.example.kiracash.R.drawable.wheat_6
import com.example.kiracash.R.drawable.wheat_7
import com.example.kiracash.model.XPEntry

@Composable
fun PixelPlant(initialProgress: Double, plantName: String, xpHistory: List<XPEntry>) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(plantName) }
    val mutableXPHistory = remember { mutableStateListOf<XPEntry>().apply { addAll(xpHistory) } }
    var totalXP by remember { mutableStateOf(calculateTotalXP(xpHistory)) }
    var progress by remember { mutableStateOf(initialProgress) } // Use initialProgress here

    // Calculate progress based on totalXP
    fun updateProgress() {
        progress = totalXP / 300.0 // 350 XP for full growth
        if (progress > 1.0) progress = 1.0 // Cap progress at 100%
    }

    // Call updateProgress whenever totalXP changes
    LaunchedEffect(totalXP) {
        updateProgress()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pixelpanel),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .padding(16.dp)
        ) {
            val imageResource = when {
                progress < 1.0 / 7.0 -> wheat_1
                progress < 2.0 / 7.0 -> wheat_2
                progress < 3.0 / 7.0 -> wheat_3
                progress < 4.0 / 7.0 -> wheat_4
                progress < 5.0 / 7.0 -> wheat_5
                progress < 6.0 / 7.0 -> wheat_6
                else -> wheat_7
            }

            val originalBitmap = BitmapFactory.decodeResource(context.resources, imageResource)
            val scaledBitmap = scalePixelArt(originalBitmap, 5) // Scale 5 times

            Image(
                bitmap = scaledBitmap.asImageBitmap(),
                contentDescription = "Pixel Plant",
                modifier = Modifier
                    .fillMaxWidth() // Fill available width
                    .heightIn(max = 300.dp), // Optional: Limit maximum height
                contentScale = ContentScale.Fit // Maintain aspect ratio
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Plant Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Growth Progress", style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("XP History", style = MaterialTheme.typography.headlineSmall)

            Button(
                onClick = {
                    mutableXPHistory.add(XPEntry("Debug Add", 10))
                    totalXP += 10 // Update totalXP when adding XP
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Add 10 XP")
            }

            LazyColumn {
                items(mutableXPHistory) { xpEntry ->
                    XPEntryCard(xpEntry)
                }
            }
        }


    }
}

// Function to calculate total XP from xpHistory
fun calculateTotalXP(xpHistory: List<XPEntry>): Int {
    return xpHistory.sumOf { it.xpAmount }
}


fun scalePixelArt(bitmap: Bitmap, scaleFactor: Int): Bitmap {
    val width = bitmap.width * scaleFactor
    val height = bitmap.height * scaleFactor
    val scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(scaledBitmap)
    // Ensure no bitmap filtering is applied; FILTER_BITMAP_FLAG is not set
    val paint = Paint().apply {
        // This flag can be omitted or explicitly set to false to ensure "pixelated" effect
        isFilterBitmap = false
    }
    canvas.scale(scaleFactor.toFloat(), scaleFactor.toFloat())
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    return scaledBitmap
}

@Preview
@Composable
fun PreviewPixelPlant() {
    val samplePlantName = "Green Buddy"
    val sampleXPHistory = listOf(
        XPEntry("Watered", 10),
        XPEntry("Fertilized", 20)
    )
    PixelPlant(initialProgress = 30.0 / 350.0, plantName = samplePlantName, xpHistory = sampleXPHistory)
}
