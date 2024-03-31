import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kiracash.ui.theme.KiraCashTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KiraCashTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column {
                        TopAppBar(
                            title = { Text("KiraCash") },
                            navigationIcon = {
                                IconButton(onClick = { }) {
                                    Icon(Icons.Filled.Home, contentDescription = "Home")
                                }
                            }
                        )
                        MainSections()
                        QuickActions()
                        Spacer(modifier = Modifier.weight(1f))
                        BottomNavigation {
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                selected = true,
                                onClick = { }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
                                selected = false,
                                onClick = { }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                                selected = false,
                                onClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainSections() {
    val sections = listOf("Expenses", "Income", "Debt")
    sections.forEach { section ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = section,
                    modifier = Modifier.padding(8.dp)
                )
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                }
            }
        }
    }
}

@Composable
fun QuickActions() {
    // TODO: Implement quick actions
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    KiraCashTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                TopAppBar(
                    title = { Text("KiraCash") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.Home, contentDescription = "Home")
                        }
                    }
                )
                MainSections()
                QuickActions()
                Spacer(modifier = Modifier.weight(1f))
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        selected = true,
                        onClick = { }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
                        selected = false,
                        onClick = { }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        selected = false,
                        onClick = { }
                    )
                }
            }
        }
    }
}