package com.example.storedataandfiles.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserScreen(viewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    val users by viewModel.userList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (name.isNotBlank() && age.isNotBlank()) {
                viewModel.insertUser(name, age.toIntOrNull() ?: 0)
                name = ""
                age = ""
            }
        }) {
            Text("Add User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("User List:")
        users.forEach {
            Text("- ${it.name}, ${it.age}")
        }
    }
}