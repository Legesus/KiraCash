package com.example.kiracash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonalExpenseDialog(
    showDialog: MutableState<Boolean>,
    onSave: (String, String, String) -> Unit
) {
    val personalExpenseName = remember { mutableStateOf("") }
    val personalExpensePrice = remember { mutableStateOf("") }
    val personalExpenseCategory = remember { mutableStateOf("") }
    val categories = listOf(
        "Food & Drink",
        "Entertainment",
        "Health & Fitness"
    ) // Replace with dynamic fetch if needed

    val focusRequester = remember { FocusRequester() }
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Personal Expenses", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextField(
                        value = personalExpenseName.value,
                        onValueChange = { personalExpenseName.value = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = personalExpensePrice.value,
                        onValueChange = { personalExpensePrice.value = it },
                        label = { Text("Price") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { setExpanded(!expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    ) {
                        TextField(
                            readOnly = true,
                            value = personalExpenseCategory.value,
                            onValueChange = {},
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { setExpanded(false) }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        personalExpenseCategory.value = category
                                        setExpanded(false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
