package com.example.kiracash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ExtendedButton(onScanClick: () -> Unit, onUploadClick: () -> Unit, onSaveToGoalsClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            //Increase bottom and right padding
            .padding(bottom = 72.dp, end = 16.dp),

        verticalArrangement = Arrangement.Bottom, // Align to bottom
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSaveToGoalsClick,
                    // Align on the right side without max width
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Add expense towards Goals", color = Color.White)
                }

                Button(
                    onClick = onUploadClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload Receipt", color = Color.White)
                }

                Button(
                    onClick = onScanClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Receipt", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space above the FAB

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = Color(0xFF1DB954),
            modifier = Modifier.padding(bottom = 16.dp) // Adjust bottom padding as needed
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}