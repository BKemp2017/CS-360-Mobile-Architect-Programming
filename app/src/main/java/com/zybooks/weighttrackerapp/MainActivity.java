package com.zybooks.weighttrackerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
public class MainActivity extends AppCompatActivity {

    private TextView titleTextView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager to manage user login sessions
        sessionManager = new SessionManager(this);

        // Initialize UI elements
        Button smsPermissionButton = findViewById(R.id.smsPermissionButton);
        Button dataDisplayButton = findViewById(R.id.dataDisplayButton);
        Button loginButton = findViewById(R.id.loginButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set click listener for SMS Permission button
        smsPermissionButton.setOnClickListener(view -> openSMSPermissionActivity());

        // Set click listener for Data Display button
        dataDisplayButton.setOnClickListener(view -> {
            // Check if the user is logged in; if yes, open DataDisplayActivity, else open LoginActivity
            if (sessionManager.isLoggedIn()) {
                openDataDisplayActivity();
            } else {
                openLoginActivity();
            }
        });

        // Set click listener for Login button
        loginButton.setOnClickListener(view -> openLoginActivity());

        // Set click listener for Logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log out the user by setting the login status to false
                sessionManager.setLogin(false);
                // Display "Logout Successful" Toast message
                Toast.makeText(MainActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize light/dark mode switch and title text view
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch lightDarkModeSwitch = findViewById(R.id.lightDarkModeSwitch);
        titleTextView = findViewById(R.id.titleTextView);

        // Check and set the switch based on the current night mode
        lightDarkModeSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        // Set a listener for the light/dark mode switch
        lightDarkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                // Set title text color to white in dark mode
                titleTextView.setTextColor(getResources().getColor(R.color.white));
            } else {
                // Disable dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                // Set title text color to black in light mode
                titleTextView.setTextColor(getResources().getColor(R.color.black));
            }

            // Recreate the activity to apply the selected mode
            recreate();
        });
    }

    // Method to open SMSPermissionActivity
    public void openSMSPermissionActivity() {
        Intent intent = new Intent(this, SMSPermissionActivity.class);
        startActivity(intent);
    }

    // Method to open DataDisplayActivity
    public void openDataDisplayActivity() {
        Intent intent = new Intent(this, DataDisplayActivity.class);
        startActivity(intent);
    }

    // Method to open LoginActivity
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}




