package com.example.studentregistration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentregistration.ui.theme.StudentRegistrationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentRegistrationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudentRegistrationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class StudentData(
    val name: String,
    val rollNo: String,
    val email: String,
    val department: String,
    val gender: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(modifier: Modifier = Modifier) {
    // Form Inputs State
    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Dropdown State for Department
    val departments = listOf("Computer Science", "Information Technology", "Electrical", "Mechanical", "Civil")
    var expanded by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf("") }

    // Radio State for Gender
    val genderOptions = listOf("Male", "Female", "Other")
    var selectedGender by remember { mutableStateOf("") }

    // Validation & Result State
    var errorMessage by remember { mutableStateOf("") }
    var submittedStudent by remember { mutableStateOf<StudentData?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Header Title ---
        Text(
            text = "Student Registration",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // --- Name Input ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // --- Roll Number Input ---
        OutlinedTextField(
            value = rollNo,
            onValueChange = { rollNo = it },
            label = { Text("Roll Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // --- Email Input ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // --- Department Dropdown ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedDepartment,
                onValueChange = {},
                readOnly = true,
                label = { Text("Department") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                departments.forEach { department ->
                    DropdownMenuItem(
                        text = { Text(department) },
                        onClick = {
                            selectedDepartment = department
                            expanded = false
                        }
                    )
                }
            }
        }

        // --- Gender Selection ---
        Text(
            text = "Gender",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            genderOptions.forEach { gender ->
                Row(
                    Modifier
                        .selectable(
                            selected = (gender == selectedGender),
                            onClick = { selectedGender = gender },
                            role = Role.RadioButton
                        )
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (gender == selectedGender),
                        onClick = null
                    )
                    Text(
                        text = gender,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // --- Error Display ---
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // --- Action Buttons ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    // Validation Logic
                    when {
                        name.isBlank() -> errorMessage = "Please enter full name."
                        rollNo.isBlank() -> errorMessage = "Please enter roll number."
                        email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                            errorMessage = "Please enter a valid email address."
                        }
                        selectedDepartment.isBlank() -> errorMessage = "Please select a department."
                        selectedGender.isBlank() -> errorMessage = "Please select gender."
                        else -> {
                            errorMessage = ""
                            submittedStudent = StudentData(
                                name = name,
                                rollNo = rollNo,
                                email = email,
                                department = selectedDepartment,
                                gender = selectedGender
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Register")
            }

            OutlinedButton(
                onClick = {
                    name = ""
                    rollNo = ""
                    email = ""
                    selectedDepartment = ""
                    selectedGender = ""
                    errorMessage = ""
                    submittedStudent = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        // --- Submitted Information Display ---
        submittedStudent?.let { student ->
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Submitted Registration Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "Name: ${student.name}")
                    Text(text = "Roll No: ${student.rollNo}")
                    Text(text = "Email: ${student.email}")
                    Text(text = "Department: ${student.department}")
                    Text(text = "Gender: ${student.gender}")
                }
            }
        }
    }
}