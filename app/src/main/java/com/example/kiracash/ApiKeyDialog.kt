package com.example.kiracash

import android.content.SharedPreferences
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

const val GEMINI_API_KEY_PREF = "GEMINI_API_KEY"

@Composable
fun ApiKeyDialog(
    sharedPreferences: SharedPreferences,
    onDismiss: () -> Unit,
    onApiKeyChanged: () -> Unit
) {
    var apiKey by remember { mutableStateOf(sharedPreferences.getString(GEMINI_API_KEY_PREF, "") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Gemini API") },
        text = {
            Column {
                TextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Button(
                        onClick = {
                            sharedPreferences.edit().putString(GEMINI_API_KEY_PREF, apiKey).apply()
                            onApiKeyChanged()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("Save", color = Color.White)
                    }
                    Button(
                        onClick = {
                            sharedPreferences.edit().remove(GEMINI_API_KEY_PREF).apply()
                            apiKey = ""
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
