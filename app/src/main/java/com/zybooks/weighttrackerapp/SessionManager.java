package com.zybooks.weighttrackerapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    // Constants for preferences
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";

    // SharedPreferences and Editor for managing the session
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Constructor for the SessionManager
    public SessionManager(Context context) {
        // Initialize SharedPreferences and its editor
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Method to set the login status
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply(); // Apply changes to SharedPreferences
    }

    // Method to check if the user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Method to set the logged-in username
    public void setLoggedInUsername(String username) {
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.apply(); // Apply changes to SharedPreferences
    }

    // Method to get the logged-in username
    public String getLoggedInUsername() {
        return sharedPreferences.getString(KEY_LOGGED_IN_USERNAME, "");
    }
}

