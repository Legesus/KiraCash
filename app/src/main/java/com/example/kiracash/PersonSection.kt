package com.example.kiracash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

data class Person(
    val profilePicture: String, // URL or resource ID
    val debtAmount: Double,
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
            Text(text = "${person.debtAmount}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Cash In: ${person.cashIn}")
                Text(text = "Cash Out: ${person.cashOut}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPersonSectionContent() {
    val people = listOf(
        Person(
            profilePicture = "", // Add a valid URL or resource ID here
            debtAmount = 100.0,
            cashIn = 200.0,
            cashOut = 50.0
        )
        // Add more Person objects here if needed
    )
    PersonSection().PersonSectionContent(people)
}

class PersonSection {

    @Composable
    fun PersonSectionContent(people: List<Person>) {
        PersonList(people, Modifier.padding(16.dp))
    }
}