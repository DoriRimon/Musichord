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
    private static final String TABLE_NAME = "chords";

    // The first column - type
    private static final String COL0 = "chordType";

    // The second column - names
    private static final String COL1 = "chordName";

    // The third column - pictures
    private static final String COL2 = "chordPic";

    public DatabaseHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the guitar table
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " INTEGER," + COL1 + " TEXT," + COL2 + " BLOB" + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item, byte[] image, String instrument){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < Globe.instruments.length; i++)
            if (instrument.equals(Globe.instruments[i])) {
                contentValues.put(COL0, i);
                break;
            }
        contentValues.put(COL1, item);
        contentValues.put(COL2, image);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        return true;
    }

    public Cursor getData(String instrument){
        int x = -1;
        for (int i = 0; i < Globe.instruments.length; i++)
            if (instrument.equals(Globe.instruments[i])) {
                x = i;
                break;
            }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL0 + "= " + x;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
