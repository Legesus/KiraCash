package com.example.kiracash

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ApiKeyDialog(onDismiss: () -> Unit, onApiKeyChanged: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val apiKey = remember { mutableStateOf(sharedPreferences.getString(BuildConfig.GEMINI_API_KEY, "") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Gemini API") },
        text = {
            Column {
                TextField(
                    value = apiKey.value,
                    onValueChange = { apiKey.value = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Button(
                        onClick = {
                            sharedPreferences.edit().putString(BuildConfig.GEMINI_API_KEY, apiKey.value).apply()
                            onApiKeyChanged()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("Save", color = Color.White)
                    }
                    Button(
                        onClick = {
                            sharedPreferences.edit().remove(BuildConfig.GEMINI_API_KEY).apply()
                            apiKey.value = ""
                            onApiKeyChanged()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
