package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Using standard MaterialTheme. If you have a custom theme in ui.theme,
            // you can wrap this in CalculatorTheme { ... } instead.
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Result will appear here") }

    // Helper function to handle the calculation and validation
    fun calculate(operation: String) {
        val val1 = input1.toDoubleOrNull()
        val val2 = input2.toDoubleOrNull()

        // 1. Input Validation
        if (val1 == null || val2 == null) {
            resultText = "Error: Please enter valid numbers."
            return
        }

        // 2. Perform Operation & Check for Division by Zero
        val res = when (operation) {
            "+" -> val1 + val2
            "-" -> val1 - val2
            "*" -> val1 * val2
            "/" -> {
                if (val2 == 0.0) {
                    resultText = "Error: Cannot divide by zero."
                    return
                } else {
                    val1 / val2
                }
            }
            else -> 0.0
        }

        // Display valid result
        resultText = "Result: $res"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = input1,
            onValueChange = { input1 = it },
            label = { Text("Enter first number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = input2,
            onValueChange = { input2 = it },
            label = { Text("Enter second number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { calculate("+") }) { Text("+") }
            Button(onClick = { calculate("-") }) { Text("-") }
            Button(onClick = { calculate("*") }) { Text("*") }
            Button(onClick = { calculate("/") }) { Text("/") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = resultText,
            fontSize = 20.sp,
            color = if (resultText.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}