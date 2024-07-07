package com.example.kiracash

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kiracash.model.XPEntry

@Composable
fun XPEntryCard(xpEntry: XPEntry) {
    Card(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = xpEntry.source, modifier = Modifier.weight(1f))
            Text(text = "${xpEntry.xpAmount} XP")
        }
    }
}