package com.zybooks.weighttrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount); // Initialize "Create Account" button

        // Initialize your database helper (use your database solution)
        dbHelper = new DatabaseHelper(this);

        // Set click listener for the login button
        buttonLogin.setOnClickListener(v -> {
            // Get the entered username and password
            String enteredUsername = editTextUsername.getText().toString();
            String enteredPassword = editTextPassword.getText().toString();

            // Check if the entered credentials are correct
            if (isUserAuthenticated(enteredUsername, enteredPassword)) {
                // Successful login, you can navigate to another activity here
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                // Initialize the SessionManager and set the login status to true
                SessionManager sessionManager = new SessionManager(LoginActivity.this);
                sessionManager.setLogin(true);

                // Set the logged-in username
                sessionManager.setLoggedInUsername(enteredUsername);

                // Start the DataDisplayActivity
                startActivity(new Intent(LoginActivity.this, DataDisplayActivity.class));

                // Finish the LoginActivity to remove it from the back stack
                finish();
            } else {
                // Incorrect credentials, show an error message
                Toast.makeText(LoginActivity.this, "Login Failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for the "Create Account" button
        buttonCreateAccount.setOnClickListener(v -> {
            // Get the entered username and password
            String enteredUsername = editTextUsername.getText().toString();
            String enteredPassword = editTextPassword.getText().toString();

            // Check if the entered credentials are not the correct ones
            if (!isUserAuthenticated(enteredUsername, enteredPassword)) {
                // Save the new user to the database (or your data storage solution)
                boolean isUserCreated = dbHelper.insertUser(enteredUsername, enteredPassword);

                if (isUserCreated) {
                    // Inform the user that the account has been created
                    Toast.makeText(LoginActivity.this, "User account created!", Toast.LENGTH_SHORT).show();
                } else {
                    // Inform the user if there was an issue creating the account
                    Toast.makeText(LoginActivity.this, "Failed to create user account.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If the entered credentials are correct, inform the user
                Toast.makeText(LoginActivity.this, "User account already exists.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to check if the entered credentials are correct
    private boolean isUserAuthenticated(String username, String password) {
        // You can implement your authentication logic here, such as checking against a database
        // For now, let's use a simple check with the default username and password
        return dbHelper.isUserValid(username, password);
    }
}
