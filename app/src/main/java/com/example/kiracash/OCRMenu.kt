
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kiracash.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRMenu(navController: NavHostController) {
    val context = LocalContext.current

    // Remember the permission request launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can use the camera
            val intent = Intent(context, ScannerActivity::class.java)
            context.startActivity(intent)
        } else {
            // Permission denied, you can show a message to the user
        }
    }

    // Check if the app has camera permission
    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("OCR Menu") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1F1B24)
                )
            )
        },
        bottomBar = { BottomNavBar(navController) } // Add this line
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to the OCR Menu",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                // Check if the app has camera permission
                val hasCameraPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasCameraPermission) {
                    // If the app has camera permission, start the ScannerActivity
                    val intent = Intent(context, ScannerActivity::class.java)
                    context.startActivity(intent)
                } else {
                    // If the app doesn't have camera permission, request it
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Capture Receipt")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOCRScreen() {
    val mockNavController = rememberNavController()
    OCRMenu(navController = mockNavController)
}