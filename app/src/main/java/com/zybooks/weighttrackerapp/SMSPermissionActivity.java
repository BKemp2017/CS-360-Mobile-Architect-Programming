package com.zybooks.weighttrackerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SMSPermissionActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private static final String[] RECIPIENTS = {"+1XXXXXXXXXX", "+1XXXXXXXXXX"};
    private EditText editTextPhoneNumber;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchNotifications; // Added Switch for notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        // Initialize UI elements
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        Button buttonRequestPermission = findViewById(R.id.buttonRequestSMSPermission);
        switchNotifications = findViewById(R.id.switchNotifications); // Initialize the Switch

        // Set click listener for the permission request button
        buttonRequestPermission.setOnClickListener(v -> {
            if (checkPermission()) {
                saveDataAndSendNotifications();
            } else {
                requestSMSPermission();
            }
        });

        // Set a listener for the Switch to handle notification settings
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Handle the case when notifications are enabled
                Toast.makeText(this, "SMS Notifications Enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case when notifications are disabled
                Toast.makeText(this, "SMS Notifications Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check if SMS permission is granted
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request SMS permission
    private void requestSMSPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveDataAndSendNotifications();
            } else {
                Toast.makeText(this, "SMS permission denied. You may not receive notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save data (phone number and notification preference) and send notifications
    private void saveDataAndSendNotifications() {
        // Save phone number to SharedPreferences
        String phoneNumber = editTextPhoneNumber.getText().toString();

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.putBoolean("allowNotifications", switchNotifications.isChecked()); // Save notification preference
        editor.apply();

        // Check if notifications are allowed
        boolean allowNotifications = preferences.getBoolean("allowNotifications", true);

        // Check if notifications are enabled
        if (allowNotifications) {
            sendSMSNotifications();
            Toast.makeText(this, "SMS Notifications Enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "SMS Notifications Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    // Send SMS notifications to recipients
    private void sendSMSNotifications() {
        // Create an SMS manager
        SmsManager smsManager = SmsManager.getDefault();

        // Define the notification messages
        String goalWeightReachedMessage = "Congratulations! You have reached your goal weight.";

        // Send notifications to each recipient
        for (String recipient : RECIPIENTS) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(), PendingIntent.FLAG_IMMUTABLE);
            smsManager.sendTextMessage(recipient, null, goalWeightReachedMessage, sentIntent, null);
        }

        Toast.makeText(this, "SMS notifications sent.", Toast.LENGTH_SHORT).show();
    }
}


