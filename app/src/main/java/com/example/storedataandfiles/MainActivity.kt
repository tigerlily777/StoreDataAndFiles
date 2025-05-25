package com.example.storedataandfiles

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.storedataandfiles.ui.UserScreen
import com.example.storedataandfiles.ui.UserViewModel

class MainActivity : ComponentActivity() {
    private val fileName = "my_internal_file.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            InternalStorageScreen(
//                context = this,
//                fileName = fileName
//            )
//        }
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my-db"
        ).build()

        val userDao = db.userDao()
        val viewModel = UserViewModel(userDao)

        setContent {
            UserScreen(viewModel)
        }
    }
}

@Composable
fun InternalStorageScreen(context: Context, fileName: String) {
    var inputText by remember { mutableStateOf("") }
    var fileContent by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Please add content here") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Button(onClick = {
                // write to file
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(inputText.toByteArray())
                }
            }) {
                Text("Save")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(onClick = {
                // Read from file
                fileContent = try {
                    context.openFileInput(fileName).bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    "Loading failed: ${e.message}"
                }
            }) {
                Text("Load")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("📄 Content of the file:")
        Text(fileContent)
    }
}