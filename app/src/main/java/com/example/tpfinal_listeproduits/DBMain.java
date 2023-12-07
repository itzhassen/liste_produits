package com.example.tpfinal_listeproduits;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBMain extends SQLiteOpenHelper {
    private static final String TAG = "DBMain";
    private static final String DBNAME = "products"; // Change database name to "products"
    private static final String TABLE = "product_table"; // Change table name to "product_table"
    private static final int VER = 1;

    public DBMain(Context context) {
        super(context, DBNAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE product_table (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "libelle TEXT, prixVente REAL, disponible INTEGER, imageUri TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // Perform necessary database schema changes here
        }
    }
}
