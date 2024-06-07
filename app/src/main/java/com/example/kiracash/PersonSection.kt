package com.example.kiracash

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.kiracash.model.AppDatabase

data class Person(
    val profilePicture: String, // URL or resource ID
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
            if (person.profilePicture == "person_icon") {
                Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(50.dp))
            } else {
                Image(
                    painter = rememberImagePainter(data = person.profilePicture),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${person.name}")
                Text(text = "Paid: ${person.cashIn}")
                Text(text = "Owe: ${person.cashOut}")
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

    // Collect wallets as a state
    val walletsFlow = walletDao.getAllWallets().collectAsState(initial = emptyList())

    // Map wallets to Person data class
    val people = walletsFlow.value.map { wallet ->
        Person(
            profilePicture = "person_icon", // Using person icon from extended icons library
            name = wallet.owner,
            cashIn = wallet.amountPaid,
            cashOut = wallet.amountOwe
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

        // Collect wallets as a state
        val walletsFlow = walletDao.getAllWallets().collectAsState(initial = emptyList())

        // Map wallets to Person data class
        val people = walletsFlow.value.map { wallet ->
            Person(
                profilePicture = "person_icon", // Using person icon from extended icons library
                name = wallet.owner,
                cashIn = wallet.amountPaid,
                cashOut = wallet.amountOwe
            )
        }

        // Display the list of people
        PersonList(people, Modifier.padding(16.dp))
    }
}
