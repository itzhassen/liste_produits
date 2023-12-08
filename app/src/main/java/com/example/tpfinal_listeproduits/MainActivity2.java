package com.example.tpfinal_listeproduits;

import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {

    DBMain dBmain;
    SQLiteDatabase sqLiteDatabase;
    String[] libelle, prixVente;
    boolean[] disponible;
    int[] id;
    byte[][] photo;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        dBmain = new DBMain(this);
        findid();
        displaydata();

        // Set up the color spinner
        Spinner colorSpinner = findViewById(R.id.colorSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.colors_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);

        // Submit button click listener
        MaterialButton submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the submission logic, e.g., update preferences
                String selectedColor = (String) colorSpinner.getSelectedItem();
                int textColorResId = getTextColorResId(selectedColor);
                updateProductColors(selectedColor);
                Toast.makeText(MainActivity2.this, "Changed successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Show button click listener to launch MainActivity
        MaterialButton btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }





    private int getTextColorResId(String textColorKey) {
        switch (textColorKey) {
            case "red":
                return R.color.red;
            case "blue":
                return R.color.blue;
            case "green":
                return R.color.green;
            default:
                return R.color.black;
        }
    }

    private void updateProductColors(String selectedColor) {
        // Logic to update the product colors in the database or adapt as per your requirement
        // You may need to iterate through your products and update their colors based on the selectedColor
    }

    @SuppressLint("Range")
    private void displaydata() {
        sqLiteDatabase = dBmain.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from product_table", null);
        if (cursor != null && cursor.getCount() > 0) {
            id = new int[cursor.getCount()];
            libelle = new String[cursor.getCount()];
            prixVente = new String[cursor.getCount()];
            disponible = new boolean[cursor.getCount()];
            photo = new byte[cursor.getCount()][];

            int i = 0;

            while (cursor.moveToNext()) {
                id[i] = cursor.getInt(0);
                libelle[i] = cursor.getString(cursor.getColumnIndex("libelle"));
                prixVente[i] = String.valueOf(cursor.getDouble(cursor.getColumnIndex("prixVente")));
                disponible[i] = Boolean.parseBoolean(String.valueOf(cursor.getInt(cursor.getColumnIndex("disponible")) == 1));

                photo[i] = cursor.getBlob(cursor.getColumnIndex("photo"));
                Log.d("MainActivity2", "Photo for ID " + id[i] + ": " + Arrays.toString(photo[i]));

                i++;
            }

            CustAdapter custAdapter = new CustAdapter();
            lv.setAdapter(custAdapter);
        } else {
            Log.d("MainActivity2", "No data found in the database");
            Toast.makeText(this, "No data found in the database", Toast.LENGTH_SHORT).show();
        }
    }

    private void findid() {
        lv = findViewById(R.id.lv);
    }

    private class CustAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return libelle.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity2.this).inflate(R.layout.singledata, parent, false);
                holder = new ViewHolder();
                holder.txtLibelle = convertView.findViewById(R.id.txt_libelle);
                holder.txtPrixVente = convertView.findViewById(R.id.txt_prixVente);
                holder.imgProduct = convertView.findViewById(R.id.imgProduct);
                holder.btnUpdate = convertView.findViewById(R.id.btnUpdate);
                holder.btnDelete = convertView.findViewById(R.id.btnDelete);
                holder.cbDisponible = convertView.findViewById(R.id.cbDisponible);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            boolean isDisponible = true;
            holder.cbDisponible.setChecked(isDisponible);

            holder.txtLibelle.setText(libelle[position]);
            holder.txtPrixVente.setText(prixVente[position]);

            if (photo[position] != null) {
                Glide.with(MainActivity2.this)
                        .load(photo[position])
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.placeholder_image);
            }

            int colorResId = getTextColorForPosition(position);
            holder.txtLibelle.setTextColor(ContextCompat.getColor(MainActivity2.this, colorResId));

            holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] currentPhoto = photo[position];

                    Bundle bundle = new Bundle();
                    bundle.putInt("id", id[position]);
                    bundle.putString("libelle", libelle[position]);
                    bundle.putString("prixVente", prixVente[position]);
                    bundle.putString("disponible", String.valueOf(disponible[position]));
                    bundle.putByteArray("photo", currentPhoto);

                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    intent.putExtra("productData", bundle);
                    startActivity(intent);
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDatabase = dBmain.getReadableDatabase();
                    long recRemove = sqLiteDatabase.delete("product_table", "id=" + id[position], null);
                    if (recRemove != -1) {
                        Toast.makeText(MainActivity2.this, "Suppression réussie", Toast.LENGTH_SHORT).show();
                        displaydata();
                    }
                }
            });

            return convertView;
        }

        private int getTextColorForPosition(int position) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity2.this);
            String textColorKey = preferences.getString("text_color_preference", "red");

            int textColorResId;
            switch (textColorKey) {
                case "red":
                    textColorResId = R.color.red;
                    break;
                case "blue":
                    textColorResId = R.color.blue;
                    break;
                case "green":
                    textColorResId = R.color.green;
                    break;
                default:
                    // Make sure this is a valid color resource ID
                    textColorResId = R.color.black;
                    break;
            }

            return textColorResId;
        }

        private class ViewHolder {
            TextView txtLibelle, txtPrixVente;
            ImageView imgProduct;
            CheckBox cbDisponible;
            ImageButton btnUpdate, btnDelete;
        }
    }
}
