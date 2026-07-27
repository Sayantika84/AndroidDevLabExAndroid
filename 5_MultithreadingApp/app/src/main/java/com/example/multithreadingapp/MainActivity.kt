package com.example.multithreadingapp // Replace with your actual package name if different

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup Programmatic UI
        setupUI()
    }

    private fun setupUI() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        statusText = TextView(this).apply {
            text = "Status: Ready to start"
            textSize = 20f
            setPadding(0, 0, 0, 50)
            gravity = Gravity.CENTER
        }

        // Use a horizontal progress bar to show actual percentage progress
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 0
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 50)
            }
        }

        startButton = Button(this).apply {
            text = "Start Background Task"
        }

        layout.addView(statusText)
        layout.addView(progressBar)
        layout.addView(startButton)

        setContentView(layout)

        // 2. Set Button Click Listener
        startButton.setOnClickListener {
            startBackgroundTask()
        }
    }

    private fun startBackgroundTask() {
        // Disable the button to prevent multiple simultaneous background tasks
        startButton.isEnabled = false
        progressBar.progress = 0
        statusText.text = "Processing... 0%"

        // 3. Create and start a new Background Thread
        Thread {
            // --- THIS BLOCK RUNS IN THE BACKGROUND ---
            // Simulate a time-consuming operation (e.g., downloading a file, complex math)
            for (i in 1..100) {
                try {
                    // Sleep for 50 milliseconds to simulate hard work
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // 4. Update the UI from the Background Thread
                // You CANNOT touch UI elements directly from a background thread.
                // You must post the update back to the Main (UI) Thread.
                runOnUiThread {
                    progressBar.progress = i
                    statusText.text = "Processing... $i%"
                }
            }

            // 5. Task Completed!
            // Update UI one final time on the Main Thread
            runOnUiThread {
                statusText.text = "Task Completed!"
                startButton.isEnabled = true
                Toast.makeText(this@MainActivity, "Operation finished successfully", Toast.LENGTH_SHORT).show()
            }
        }.start() // Don't forget to start the thread!
    }
}