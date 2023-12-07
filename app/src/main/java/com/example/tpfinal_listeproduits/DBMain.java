package com.example.tpfinal_listeproduits;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBMain extends SQLiteOpenHelper {
    private static final String TAG = "DBMain";
    private static final String DB_NAME = "products";
    private static final String TABLE_NAME = "product_table";
    private static final int DB_VERSION = 3; // Change version to a higher number

    public DBMain(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "libelle TEXT," +
                "prixVente REAL," +
                "disponible INTEGER," +
                "photo BLOB" + // Add this line to create the photo column
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
