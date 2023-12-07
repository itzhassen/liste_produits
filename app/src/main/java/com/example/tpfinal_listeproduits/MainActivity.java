package com.example.tpfinal_listeproduits;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    DBMain dBmain;
    SQLiteDatabase sqLiteDatabase;
    EditText libelle, prixVente;
    CheckBox disponible;
    Button submit, display, edit, btnCaptureImage;
    int id = 0;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dBmain = new DBMain(this);
        findid();
        insertData();
        cleardata();
        editdata();

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
    }

    private void showImageDialog() {
        // Implement your logic here to show a dialog or handle the image source choice
        // For simplicity, let's use a Toast message
        Toast.makeText(MainActivity.this, "Choose Image Source", Toast.LENGTH_SHORT).show();

        // You can use an AlertDialog or any other UI component to let the user choose between gallery and camera
        // Handle the chosen option and launch the corresponding intent
        // For example, you can use an AlertDialog with options "Gallery" and "Camera"
        // and handle the chosen option in the respective click listeners.

        // For now, let's assume the user chose Camera
        openCamera();
    }

    private void openCamera() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Continue if the file was successfully created
        if (photoFile != null) {
            // Get the Uri for the file
            imageUri = FileProvider.getUriForFile(this, "com.example.tpfinal_listeproduits.fileprovider", photoFile);
            Uri photoUri = FileProvider.getUriForFile(this,
                    "com.example.tpfinal_listeproduits.fileprovider", photoFile);
            // Launch the camera intent
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = photoFile.getAbsolutePath();
        Log.d("FilePath", currentPhotoPath);
        return photoFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // The user captured an image from the camera
                // Handle the imageUri as needed (e.g., save it to the database)
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    // Do something with the imageUri
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // The user chose an image from the gallery
                // Handle the imageUri as needed (e.g., save it to the database)
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    // Do something with the imageUri
                }
            }
        }
    }

    private void editdata() {
        if (getIntent().getBundleExtra("productData") != null) {
            Bundle bundle = getIntent().getBundleExtra("productData");
            id = bundle.getInt("id");
            libelle.setText(bundle.getString("libelle"));
            prixVente.setText(String.valueOf(bundle.getDouble("prixVente")));
            disponible.setChecked(bundle.getInt("disponible") == 1);

            edit.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
        }
    }

    private void cleardata() {
        libelle.setText("");
        prixVente.setText("");
        disponible.setChecked(false);
    }

    private void insertData() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("libelle", libelle.getText().toString().trim());
                contentValues.put("prixVente", Double.parseDouble(prixVente.getText().toString().trim()));
                contentValues.put("disponible", disponible.isChecked() ? 1 : 0);
                if (imageUri != null) {
                    contentValues.put("imageUri", imageUri.toString()); // Save the imageUri to the database
                    Log.d(TAG, "Image Uri added to contentValues: " + imageUri.toString());
                } else {
                    Log.d(TAG, "Image Uri is null. Not adding to contentValues.");
                }
                long result = dBmain.getWritableDatabase().insert("product_table", null, contentValues);
                if (result != -1) {
                    Toast.makeText(MainActivity.this, "Successfully inserted", Toast.LENGTH_SHORT).show();
                    cleardata();
                } else {
                    Log.e(TAG, "Error inserting data into the database");
                    Toast.makeText(MainActivity.this, "Error inserting data", Toast.LENGTH_SHORT).show();
                }



                dBmain.getWritableDatabase().insert("product_table", null, contentValues);

                Toast.makeText(MainActivity.this, "Successfully inserted", Toast.LENGTH_SHORT).show();
                cleardata();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("libelle", libelle.getText().toString().trim());
                contentValues.put("prixVente", Double.parseDouble(prixVente.getText().toString().trim()));
                contentValues.put("disponible", disponible.isChecked() ? 1 : 0);
                contentValues.put("imageUri", imageUri.toString()); // Save the imageUri to the database

                dBmain.getWritableDatabase().update("product_table", contentValues, "id=" + id, null);

                Toast.makeText(MainActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                submit.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                cleardata();
            }
        });

        // Other code...

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    private void findid() {
        libelle = findViewById(R.id.libelle);
        prixVente = findViewById(R.id.prixVente);
        disponible = findViewById(R.id.disponible);
        submit = findViewById(R.id.submit_btn);
        display = findViewById(R.id.display_btn);
        edit = findViewById(R.id.edit_btn);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
    }
}
