package com.zybooks.weighttrackerapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    private EditText editTextData1, editTextData2, editTextGoalWeight;
    private TableLayout tableLayoutData;
    private DatabaseHelper databaseHelper;
    private String loggedInUsername; // Store the logged-in username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        editTextData1 = findViewById(R.id.editTextData1);
        editTextData2 = findViewById(R.id.editTextData2);
        editTextGoalWeight = findViewById(R.id.editTextGoalWeight);
        tableLayoutData = findViewById(R.id.tableLayoutData);

        databaseHelper = new DatabaseHelper(this);
        // Add SessionManager
        SessionManager sessionManager = new SessionManager(this); // Initialize SessionManager

        loggedInUsername = sessionManager.getLoggedInUsername(); // Get the logged-in username

        Button buttonAddData = findViewById(R.id.buttonAddData);
        Button buttonDeleteData = findViewById(R.id.buttonDeleteData);
        Button buttonEditData = findViewById(R.id.buttonEditData);

        buttonAddData.setOnClickListener(v -> {
            // Check if the user is logged in
            if (loggedInUsername != null) {
                String data1 = editTextData1.getText().toString();
                String data2 = editTextData2.getText().toString();
                String goalWeight = editTextGoalWeight.getText().toString();

                if (!data1.isEmpty() && !data2.isEmpty() && !goalWeight.isEmpty()) {
                    long result = databaseHelper.insertWeightData(loggedInUsername, data1, data2, goalWeight);

                    if (result != -1) {
                        addDataToTableLayout(data1, data2, goalWeight, result);
                        editTextData1.getText().clear();
                        editTextData2.getText().clear();
                        editTextGoalWeight.getText().clear();
                    } else {
                        Toast.makeText(DataDisplayActivity.this, "Failed to add data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DataDisplayActivity.this, "Please enter data in all fields.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the user is not logged in
                Toast.makeText(DataDisplayActivity.this, "Please log in to add data.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDeleteData.setOnClickListener(v -> {
            // Check if the user is logged in
            if (loggedInUsername != null) {
                List<Long> idsToDelete = new ArrayList<>();
                List<View> rowsToDelete = new ArrayList<>(); // Store rows to be deleted

                for (int i = 1; i < tableLayoutData.getChildCount(); i++) {
                    TableRow row = (TableRow) tableLayoutData.getChildAt(i);
                    CheckBox checkBox = (CheckBox) row.getChildAt(0);

                    if (checkBox.isChecked()) {
                        long id = (long) row.getTag();
                        idsToDelete.add(id);
                        rowsToDelete.add(row); // Add the row to the list
                    }
                }

                if (!idsToDelete.isEmpty()) {
                    databaseHelper.deleteWeightData(idsToDelete);

                    // Remove the rows from the table layout
                    for (View row : rowsToDelete) {
                        tableLayoutData.removeView(row);
                    }

                } else {
                    Toast.makeText(DataDisplayActivity.this, "No rows selected for deletion.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the user is not logged in
                Toast.makeText(DataDisplayActivity.this, "Please log in to delete data.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonEditData.setOnClickListener(v -> {
            // Check if the user is logged in
            if (loggedInUsername != null) {
                List<TableRow> rowsToEdit = new ArrayList<>();

                for (int i = 1; i < tableLayoutData.getChildCount(); i++) {
                    TableRow row = (TableRow) tableLayoutData.getChildAt(i);
                    CheckBox checkBox = (CheckBox) row.getChildAt(0);

                    if (checkBox.isChecked()) {
                        rowsToEdit.add(row);
                    }
                }

                if (rowsToEdit.isEmpty()) {
                    Toast.makeText(DataDisplayActivity.this, "No rows selected for editing.", Toast.LENGTH_SHORT).show();
                } else if (rowsToEdit.size() == 1) {
                    TableRow rowToEdit = rowsToEdit.get(0);

                    // Extract the data from the selected row
                    String data1 = ((TextView) rowToEdit.getChildAt(1)).getText().toString();
                    String data2 = ((TextView) rowToEdit.getChildAt(2)).getText().toString();

                    // Create and show a dialog for editing
                    showEditDialog(data1, data2, rowToEdit);
                } else {
                    Toast.makeText(DataDisplayActivity.this, "Select one row for editing.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the user is not logged in
                Toast.makeText(DataDisplayActivity.this, "Please log in to edit data.", Toast.LENGTH_SHORT).show();
            }
        });

        displayWeightData();
    }

    private void displayWeightData() {
        Cursor cursor = databaseHelper.getAllWeightDataForUser(loggedInUsername); // Retrieve data for the logged-in user

        if (cursor != null) {
            try {
                int idColumnIndex = cursor.getColumnIndex(DatabaseHelper._ID);
                int dateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATA1);
                int weightColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATA2);
                int goalWeightColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_GOAL_WEIGHT);

                if (idColumnIndex != -1 && dateColumnIndex != -1 && weightColumnIndex != -1 && goalWeightColumnIndex != -1) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumnIndex);
                        String date = cursor.getString(dateColumnIndex);
                        String weight = cursor.getString(weightColumnIndex);
                        String goalWeight = cursor.getString(goalWeightColumnIndex);

                        // Ensure that none of the retrieved values are null
                        if (date != null && weight != null && goalWeight != null) {
                            addDataToTableLayout(date, weight, goalWeight, id);
                        } else {
                            Log.e("DisplayWeightData", "One or more columns have null values.");
                        }
                    }
                } else {
                    Log.e("DisplayWeightData", "One or more columns are missing in the cursor.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        } else {
            Log.e("DisplayWeightData", "Cursor is null.");
        }
    }

    private void addDataToTableLayout(String data1, String data2, String goalWeight, long id) {
        TableRow row = new TableRow(this);

        CheckBox checkBox = new CheckBox(this);

        TextView textViewData1 = new TextView(this);
        textViewData1.setText(data1);
        textViewData1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        TextView textViewData2 = new TextView(this);
        textViewData2.setText(data2);
        textViewData2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        TextView textViewGoalWeight = new TextView(this);
        textViewGoalWeight.setText(goalWeight); // Add the goalWeight value
        textViewGoalWeight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        row.addView(checkBox);
        row.addView(textViewData1);
        row.addView(textViewData2);
        row.addView(textViewGoalWeight); // Add the goalWeight TextView

        // Set the row's tag to the database id for later reference
        row.setTag(id);

        tableLayoutData.addView(row);
    }

    private void refreshTableLayout() {
        tableLayoutData.removeAllViews();
        displayWeightData();
    }

    private void showEditDialog(final String data1, final String data2, final TableRow rowToEdit) {
        // Retrieve data from the selected row
        // Create a dialog for editing the data
        AlertDialog.Builder builder = new AlertDialog.Builder(DataDisplayActivity.this);
        builder.setTitle("Edit Data");

        // Inflate the custom layout containing both EditText fields
        View dialogView = getLayoutInflater().inflate(R.layout.edit_dialog_layout, null);

        // Find the EditText fields in the custom layout
        final EditText editTextData1 = dialogView.findViewById(R.id.editTextEditDate);
        final EditText editTextData2 = dialogView.findViewById(R.id.editTextEditWeight);

        // Set the current data into the EditText fields
        editTextData1.setText(data1);
        editTextData2.setText(data2);

        // Add the custom layout to the dialog
        builder.setView(dialogView);

        // Set up the buttons for the dialog
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get the edited data from the EditText fields
            String newData1 = editTextData1.getText().toString();
            String newData2 = editTextData2.getText().toString();

            // Update the corresponding row in the table
            // Update the TextViews in the selected row with the new data
            TextView textViewData1 = (TextView) rowToEdit.getChildAt(1);
            TextView textViewData2 = (TextView) rowToEdit.getChildAt(2);
            textViewData1.setText(newData1);
            textViewData2.setText(newData2);

            // Refresh the table to reflect the changes
            refreshTableLayout();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Cancel the dialog
            dialog.cancel();
        });

        builder.show();
    }
}
