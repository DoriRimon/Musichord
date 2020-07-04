package com.example.dorir.musichord_pac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // TAG for the database
    private static final String TAG = "DatabaseHelper";

    // The name of the guitar table
    private static final String GUITAR_TABLE_NAME = "guitarChords";

    // The name of the ukulele chord namer
    private static final String UKE_TABLE_NAME = "ukeChords";

    // The first column - names
    private static final String COL0 = "chordName";

    // The second column - pictures
    private static final String COL1 = "chordPic";

    public DatabaseHelper(@Nullable Context context) {
        super(context, GUITAR_TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the guitar table
        String createGuitarTable = "CREATE TABLE " + GUITAR_TABLE_NAME + " (" + COL0 + " TEXT," + COL1 + " BLOB" + ")";

        // Creating the ukulele table
        String createUkeTable = "CREATE TABLE " + UKE_TABLE_NAME + " (" + COL0 + " TEXT," + COL1 + " BLOB" + ")";

        db.execSQL(createGuitarTable);
        db.execSQL(createUkeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + GUITAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UKE_TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item, byte[] image, String instrument){
        if (instrument.equals("guitar")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL0, item);
            contentValues.put(COL1, image);

            Log.d(TAG, "addData: Adding " + item + " to " + GUITAR_TABLE_NAME);

            long result = db.insert(GUITAR_TABLE_NAME, null, contentValues);
            if (result == -1)
                return false;
            return true;
        }
        if (instrument.equals("uke")){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL0, item);
            contentValues.put(COL1, image);

            Log.d(TAG, "addData: Adding " + item + " to " + UKE_TABLE_NAME);

            long result = db.insert(UKE_TABLE_NAME, null, contentValues);
            if (result == -1)
                return false;
            return true;
        }
        return true;
    }

    public Cursor getData(String instrument){
        if (instrument.equals("guitar")) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + GUITAR_TABLE_NAME;
            Cursor data = db.rawQuery(query, null);
            return data;
        }
        if (instrument.equals("uke")){
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + UKE_TABLE_NAME;
            Cursor data = db.rawQuery(query, null);
            return data;
        }
        return null;
    }
}
