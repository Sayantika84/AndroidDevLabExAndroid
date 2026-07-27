package com.example.messagealertapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var tvStatus: TextView
    private val PERMISSION_REQUEST_CODE = 101
    private val CHANNEL_ID = "SMS_ALERT_CHANNEL"

    // 1. Define the BroadcastReceiver to listen for SMS
    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                // Extract the SMS message from the intent
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                for (sms in messages) {
                    val sender = sms.displayOriginatingAddress
                    val messageBody = sms.displayMessageBody

                    // Update UI
                    tvStatus.text = "Last Message Received:\n\nFrom: $sender\nMessage: $messageBody"

                    // Trigger Alert and Notification
                    showAlertDialog(sender, messageBody)
                    generateNotification(sender, messageBody)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        createNotificationChannel()
        requestAppPermissions()
    }

    // Register the receiver when the app is active
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, filter)
    }

    // Unregister the receiver when the app goes to the background to prevent memory leaks
    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsReceiver)
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

        tvStatus = TextView(this).apply {
            text = "Waiting for incoming messages...\n(Ensure permissions are granted)"
            textSize = 18f
            gravity = Gravity.CENTER
        }

        layout.addView(tvStatus)
        setContentView(layout)
    }

    private fun showAlertDialog(sender: String?, message: String?) {
        AlertDialog.Builder(this)
            .setTitle("New Message Received!")
            .setMessage("From: $sender\n\n$message")
            .setPositiveButton("Dismiss") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun generateNotification(sender: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Build the notification using the native Builder
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        builder.setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Message from $sender")
            .setContentText(message)
            .setStyle(Notification.BigTextStyle().bigText(message)) // Allows multi-line text
            .setAutoCancel(true)

        // Use a unique ID for each notification based on current time
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        // Notification Channels are required on Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Message Alerts"
            val descriptionText = "Notifications for incoming SMS messages"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestAppPermissions() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )

        // Add Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missingPermissions = requiredPermissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissions(missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions Granted. Ready to receive SMS.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions Denied. App cannot function.", Toast.LENGTH_LONG).show()
                tvStatus.text = "Permissions Denied.\nPlease grant SMS and Notification permissions in settings."
            }
        }
    }
}