package com.zybooks.weighttrackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeightTracker.db";
    private static final int DATABASE_VERSION = 4; // Increment the version due to schema changes

    // Define table names and column names
    public static final String TABLE_NAME = "Weight_Table";
    public static final String _ID = BaseColumns._ID;
    public static final String COLUMN_USERNAME = "username"; // Add a username column
    public static final String COLUMN_DATA1 = "data1";
    public static final String COLUMN_DATA2 = "data2";
    public static final String COLUMN_GOAL_WEIGHT = "goal_weight";

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Weight_Table
        String SQL_CREATE_WEIGHT_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USERNAME + " TEXT, " + // Store the username with the weight data
                COLUMN_DATA1 + " TEXT, " +
                COLUMN_DATA2 + " TEXT, " +
                COLUMN_GOAL_WEIGHT + " TEXT)";

        db.execSQL(SQL_CREATE_WEIGHT_TABLE);

        // Create the users table
        String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";

        db.execSQL(SQL_CREATE_USERS_TABLE);
    }

    // Insert weight data into the Weight_Table for a specific user
    public long insertWeightData(String username, String data1, String data2, String goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_DATA1, data1);
        values.put(COLUMN_DATA2, data2);
        values.put(COLUMN_GOAL_WEIGHT, goalWeight);
        return db.insert(TABLE_NAME, null, values);
    }

    // Delete weight data with the specified IDs
    public void deleteWeightData(List<Long> idsToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Long id : idsToDelete) {
            db.delete(TABLE_NAME, _ID + "=?", new String[]{String.valueOf(id)});
        }
    }

    // Retrieve all weight data from the Weight_Table for a specific user
    public Cursor getAllWeightDataForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {_ID, COLUMN_DATA1, COLUMN_DATA2, COLUMN_GOAL_WEIGHT};
        String selection = COLUMN_USERNAME + "=?";
        String[] selectionArgs = {username};
        return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
    }

    // Insert a user into the users table
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    // Check if the user with given username and password exists in the users table
    public boolean isUserValid(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades if needed
        if (oldVersion < 2) {
            // Apply schema changes for version 2
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN new_column_name TEXT");
        }

        // Handle schema changes for other versions if necessary
        if (oldVersion < 3) {
            // Apply schema changes for version 3
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN another_new_column TEXT");
        }

        // Increment the version number when making schema changes
    }
}
