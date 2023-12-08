package com.example.tpfinal_listeproduits;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Spinner colorSpinner = findViewById(R.id.colorSpinner);
        Button submitButton = findViewById(R.id.submitButton);
        Button showButton = findViewById(R.id.showButton);

        // Set up the color spinner (Assuming you have an array resource for colors)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.colors_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);

        // Submit button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the submission logic, e.g., update preferences
                String selectedColor = colorSpinner.getSelectedItem().toString();
                boolean success = updatePreferences(selectedColor);

                // Show a message based on the result
                if (success) {
                    showSnackbar("Changed successfully");
                } else {
                    showSnackbar("Error in changing preferences");
                }
            }
        });

        // Show button click listener to launch MainActivity2
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreferencesActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    private boolean updatePreferences(String selectedColor) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("text_color_preference", selectedColor);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
