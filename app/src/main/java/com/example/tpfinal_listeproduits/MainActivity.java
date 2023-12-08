package com.example.tpfinal_listeproduits;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Arrays;

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

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dBmain = new DBMain(this);
        imgPreview = findViewById(R.id.imgPreview);



        findids();
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

        // Add the Update button functionality
        Button btnUpdate = findViewById(R.id.edit_btn);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there is data to update
                if (id != 0) {
                    updateData();
                } else {
                    Toast.makeText(MainActivity.this, "No data to update", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    byte[] photoData = getPhotoDataFromUri(imageUri);
                    contentValues.put("photo", photoData);
                    Log.d(TAG, "Photo data added to contentValues");
                } else {
                    Log.d(TAG, "Image Uri is null. Photo data not added to contentValues.");
                }

                long result = dBmain.getWritableDatabase().insert("product_table", null, contentValues);
                if (result != -1) {
                    Toast.makeText(MainActivity.this, "Successfully inserted", Toast.LENGTH_SHORT).show();
                    cleardata();
                } else {
                    Log.e(TAG, "Error inserting data into the database");
                    Toast.makeText(MainActivity.this, "Error inserting data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findids() {
        libelle = findViewById(R.id.libelle);
        prixVente = findViewById(R.id.prixVente);
        disponible = findViewById(R.id.disponible);
        submit = findViewById(R.id.submit_btn);
        display = findViewById(R.id.display_btn);
        edit = findViewById(R.id.edit_btn);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
    }

    private void showImageDialog() {
        Toast.makeText(MainActivity.this, "Choose Image Source", Toast.LENGTH_SHORT).show();
        openCamera();
    }

    private void openCamera() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "com.example.tpfinal_listeproduits.fileprovider", photoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private String currentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = photoFile.getAbsolutePath();
        Log.d("FilePath", currentPhotoPath);
        return photoFile;
    }
    private ImageView imgPreview;

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

                    // Display the preview
                    showImagePreview(imageUri);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // The user chose an image from the gallery
                // Handle the imageUri as needed (e.g., save it to the database)
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    // Do something with the imageUri

                    // Display the preview
                    showImagePreview(imageUri);
                }
            }
        }
    }
    private void showImagePreview(Uri imageUri) {
        imgPreview.setVisibility(View.VISIBLE);

        // Use a placeholder image for testing purposes
        imgPreview.setImageResource(R.drawable.placeholder_image);

        // For loading the actual image, consider using an image loading library like Glide or Picasso.
        // Example using Glide:
        // Glide.with(this).load(imageUri).into(imgPreview);
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

    // Update the data in the database
    private void updateData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("libelle", libelle.getText().toString().trim());
        contentValues.put("prixVente", Double.parseDouble(prixVente.getText().toString().trim()));
        contentValues.put("disponible", disponible.isChecked() ? 1 : 0);

        if (imageUri != null) {
            byte[] photoData = getPhotoDataFromUri(imageUri);
            contentValues.put("photo", photoData);
            Log.d(TAG, "Photo data added to contentValues");
        } else {
            Log.d(TAG, "Image Uri is null. Photo data not added to contentValues.");
        }

        int rowsAffected = sqLiteDatabase.update("product_table", contentValues, "id=" + id, null);
        if (rowsAffected > 0) {
            Toast.makeText(MainActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
            cleardata();
            id = 0; // Reset id after update
            edit.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "Error updating data in the database");
            Toast.makeText(MainActivity.this, "Error updating data", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getPhotoDataFromUri(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
