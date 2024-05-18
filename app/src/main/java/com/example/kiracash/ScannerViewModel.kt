package com.example.kiracash

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    val recognizedText = MutableLiveData<String>()
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun recognizeTextFromImage(imageBitmap: Bitmap, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val image = InputImage.fromBitmap(imageBitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                onSuccess(visionText.text)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun updateRecognizedText(text: String) {
        recognizedText.value = text
    }
}