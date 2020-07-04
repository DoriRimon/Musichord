package com.example.dorir.musichord_pac;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UkuleleListData extends AppCompatActivity {

    private static final String TAG = "UkuleleListData";

    // The database
    DatabaseHelper mdb;

    // The list view
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ukulele_list_layout);
        lv = findViewById(R.id.uke_list_view);
        mdb = new DatabaseHelper(this);

        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying Data in the ListView");

        // Getting the data from the ukulele table
        Cursor data = mdb.getData("uke");

        // List for the names
        ArrayList<String> listDataNames = new ArrayList<>();

        // List for the images
        ArrayList<byte[]> listDataImages = new ArrayList<>();
        while (data.moveToNext()){
            listDataNames.add(data.getString(0));
            listDataImages.add(data.getBlob(1));
        }

        // We want only the last 10 chords
        int len = Math.min(10, listDataNames.size());

        // An array of the names
        String[] last_used_names = new String[len];

        // An array of the images
        Bitmap[] last_used_images = new Bitmap[len];

        for (int i = 0; i < len; i++) {
            last_used_names[i] = listDataNames.get(listDataNames.size() - i - 1);
            last_used_images[i] = getImage(listDataImages.get(listDataImages.size() - i - 1));
        }

        // Settings the adapter
        ChordAdapter adapter = new ChordAdapter(this, last_used_names, last_used_images);
        lv.setAdapter(adapter);
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Controls the back button (navigates to the menu page)
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
        //CustomIntent.customType(this, "left-to-right");
    }
}