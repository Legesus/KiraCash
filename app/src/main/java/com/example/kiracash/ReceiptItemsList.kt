package com.example.kiracash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kiracash.model.ReceiptItem

@Composable
fun ReceiptItemsList(items: List<ReceiptItem>) {
    Column {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "${index + 1}. ${item.quantity}x ${item.description}", modifier = Modifier.weight(1f))
                Text(text = "RM ${item.price}", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }
    }
}
