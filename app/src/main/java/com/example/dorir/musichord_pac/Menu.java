package com.example.dorir.musichord_pac;

import android.content.Intent;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Menu extends AppCompatActivity implements View.OnClickListener{

    // A button to open the settings page
    private Button settings;

    // A button to open the intro pages
    private Button data;

    // A button to open the scales page
    private Button scales;

    // A button to open the chord search page
    private Button chord_search;

    // A button to open the chord namer page
    private Button chord_namer;

    // A TextView for the title of this activity
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // ----- This section is made so the intro pages will show only at the first time the app is opened -----
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            startActivity(new Intent(this, Intro.class));
        }


        // No longer the first run of the app
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

        // ----- Adding on click listeners -----
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);
        data = findViewById(R.id.data);
        data.setOnClickListener(this);
        scales = findViewById(R.id.scales);
        scales.setOnClickListener(this);
        chord_namer = findViewById(R.id.chord_namer);
        chord_namer.setOnClickListener(this);
        chord_search = findViewById(R.id.chord_search);
        chord_search.setOnClickListener(this);
        title = findViewById(R.id.title);

        // ----- Setting the font to be a custom one -----
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        title.setTypeface(tf);
        settings.setTypeface(tf);
        data.setTypeface(tf);
        scales.setTypeface(tf);
        chord_search.setTypeface(tf);
        chord_namer.setTypeface(tf);
    }

    // The on click method
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.data:
                openIntro();
                break;
            case R.id.chord_namer:
                openChordNamer();
                break;
            default:
                openUnderConstruction();
        }
    }

    // Controls the back button (navigates to the menu page)
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    // Opens the intro pages
    public void openIntro(){
        Intent intent = new Intent(this, Intro.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Opens the chord namer activity
    public void openChordNamer(){
        Intent intent = new Intent(this, ChordNamer.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Opens the under construction activity
    public void openUnderConstruction(){
        Intent intent = new Intent(this, UnderConstruction.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
