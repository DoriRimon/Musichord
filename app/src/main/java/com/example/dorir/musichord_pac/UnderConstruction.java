package com.example.dorir.musichord_pac;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UnderConstruction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_construction);
    }

    // Controls the back button (navigates to the menu page)
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
