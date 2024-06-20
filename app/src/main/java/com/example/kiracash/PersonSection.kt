package com.example.kiracash

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.kiracash.model.AppDatabase
import java.io.File

data class Person(
    val walletPicture: String, // URL or resource ID
    val name: String,
    val cashIn: Double,
    val cashOut: Double
)

@Composable
fun PersonList(people: List<Person>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(people) { person ->
            PersonRow(person)
        }
    }
}

@Composable
fun PersonRow(person: Person) {
    val context = LocalContext.current
    val bitmap = loadBitmapFromFile(person.walletPicture)
    val resourceId = getDrawableResourceId(context, person.walletPicture)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            } else if (resourceId != null) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Image(
                    painter = rememberImagePainter(data = person.walletPicture, builder = {
                        error(R.drawable.proficon) // Fallback image
                    }),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = person.name, color = Color.White)
                Row {
                    Text(text = "They owe you: ", color = Color.White)
                    Text(text = "RM ${person.cashIn}", color = if (person.cashIn > 0) Color(0xFF008000) else Color.White) // Green color for cashIn if it's greater than 0
                }
                Row {
                    Text(text = "You owe them: ", color = Color.White)
                    Text(text = "RM ${person.cashOut}", color = if (person.cashOut > 0) Color.Red else Color.White) // Red color for cashOut if it's greater than 0
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPersonSectionContent() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val walletDao = db.walletDao()

    // Collect wallets with total amount paid as a state
    val walletsPaidFlow = walletDao.getWalletsWithTotalAmountPaid().collectAsState(initial = emptyList())
    Log.d("PersonSection", "PreviewPersonSectionContent Paid: ${walletsPaidFlow.value}")

    // Collect wallets with total amount owe as a state
    val walletsOweFlow = walletDao.getWalletsWithTotalAmountOwe().collectAsState(initial = emptyList())
    Log.d("PersonSection", "PreviewPersonSectionContent Owe: ${walletsOweFlow.value}")

    // Map wallets to Person data class
    val people = walletsPaidFlow.value.zip(walletsOweFlow.value) { walletPaid, walletOwe ->
        Person(
            walletPicture = walletPaid.walletPicture,
            name = walletPaid.owner,
            cashIn = walletPaid.amountPaid,
            cashOut = walletOwe.amountOwe
        )
    }

    // Display the list of people
    PersonList(people, Modifier.padding(16.dp))
}

class PersonSection {

    @Composable
    fun PersonSectionContent() {
        val context = LocalContext.current
        val db = AppDatabase.getDatabase(context)
        val walletDao = db.walletDao()
        val paidItemDao = db.paidItemDao()

        // Collect wallets with total amount paid as a state
        val walletsPaidFlow = walletDao.getWalletsWithTotalAmountPaid().collectAsState(initial = emptyList())

        // Collect wallets with total amount owe as a state
        val walletsOweFlow = walletDao.getWalletsWithTotalAmountOwe().collectAsState(initial = emptyList())

        // Collect paid items
        val paidItemsFlow = paidItemDao.getPaidItems().collectAsState(initial = emptyList())

        // Map wallets to Person data class
        val people = walletsPaidFlow.value.zip(walletsOweFlow.value) { walletPaid, walletOwe ->
            val paidItems = paidItemsFlow.value.filter { it.walletId == walletPaid.id }
            Person(
                walletPicture = walletPaid.walletPicture, // Use walletPicture from the database
                name = walletPaid.owner,
                cashIn = paidItems.sumOf { it.price },
                cashOut = walletOwe.amountOwe
            )
        }

        // Display the list of people
        PersonList(people, Modifier.padding(16.dp))
    }
}

fun loadBitmapFromFile(filePath: String): ImageBitmap? {
    return try {
        val file = File(filePath)
        if (file.exists()) {
            BitmapFactory.decodeFile(filePath)?.asImageBitmap()
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("PersonSection", "Failed to load image from file: $filePath", e)
        null
    }
}

fun getDrawableResourceId(context: Context, resourceName: String): Int? {
    return try {
        val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resId != 0) resId else null
    } catch (e: Exception) {
        Log.e("PersonSection", "Failed to get resource ID for: $resourceName", e)
        null
    }
}
