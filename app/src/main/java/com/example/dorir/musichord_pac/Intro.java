package com.example.dorir.musichord_pac;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class Intro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ----- Creating the first page in the intro pages -----
        SliderPage s1 = new SliderPage();
        s1.setTitle("Welcome to Musichord");
        s1.setDescription("A musician's best friend");
        s1.setImageDrawable(R.drawable.icon_3);
        s1.setBgColor(ContextCompat.getColor(this, R.color.s1_bg_color));


        setColorSkipButton(ContextCompat.getColor(this, R.color.colorPrimary));
        setColorDoneText(ContextCompat.getColor(this, R.color.colorPrimary));
        setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK);
        setNextArrowColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setDoneTextTypeface("fonts/Quicksand-Regular.otf");
        setSkipTextTypeface("fonts/Quicksand-Regular.otf");


        s1.setDescTypeface("fonts/Quicksand-Regular.otf");
        s1.setTitleTypeface("fonts/Quicksand-Regular.otf");
        s1.setTitleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        s1.setDescColor(ContextCompat.getColor(this, R.color.colorPrimary));

        addSlide(AppIntroFragment.newInstance(s1));

        // ----- Creating the 2nd page in the intro pages -----
        SliderPage s2 = new SliderPage();
        s2.setTitle("So What is it All About?");
        String line1 = "Musichord is made to make your life easier as a musician. ";
        String line2 = "The app has two main options - Chord Search & Chord Namer";
        s2.setDescription(line1 + line2);
        s2.setBgColor(ContextCompat.getColor(this, R.color.s2_bg_color));

        s2.setDescTypeface("fonts/Quicksand-Regular.otf");
        s2.setTitleTypeface("fonts/Quicksand-Regular.otf");
        s2.setTitleColor(Color.WHITE);
        s2.setDescColor(Color.WHITE);
        s2.setImageDrawable(R.drawable.musical_notes);

        addSlide(AppIntroFragment.newInstance(s2));

        // ----- Creating the 3rd page in the intro pages -----
        SliderPage s3 = new SliderPage();
        s3.setTitle("Chord Search");
        s3.setDescription("Chord Search translates a name you give it to its form");

        s3.setDescTypeface("fonts/Quicksand-Regular.otf");
        s3.setTitleTypeface("fonts/Quicksand-Regular.otf");
        s3.setTitleColor(Color.WHITE);
        s3.setDescColor(Color.WHITE);
        s3.setBgColor(ContextCompat.getColor(this, R.color.s3_bg_color));
        s3.setImageDrawable(R.drawable.magnifying_glass);

        addSlide(AppIntroFragment.newInstance(s3));

        // ----- Creating the 4th page in the intro pages -----
        SliderPage s4 = new SliderPage();
        s4.setTitle("Chord Namer");
        s4.setDescription("Chord Namer translates a chord you give it to its name");

        s4.setDescTypeface("fonts/Quicksand-Regular.otf");
        s4.setTitleTypeface("fonts/Quicksand-Regular.otf");
        s4.setTitleColor(Color.WHITE);
        s4.setDescColor(Color.WHITE);
        s4.setBgColor(ContextCompat.getColor(this, R.color.s4_bg_color));
        s4.setImageDrawable(R.drawable.open_book);

        addSlide(AppIntroFragment.newInstance(s4));

        // ----- Creating the 5th page in the intro pages -----
        SliderPage s5 = new SliderPage();

        s5.setDescTypeface("fonts/Quicksand-Regular.otf");
        s5.setTitleTypeface("fonts/Quicksand-Regular.otf");
        s5.setTitleColor(Color.WHITE);
        s5.setDescColor(Color.WHITE);
        s5.setBgColor(ContextCompat.getColor(this, R.color.s5_bg_color));
        s5.setImageDrawable(R.drawable.lets_get_started);

        addSlide(AppIntroFragment.newInstance(s5));

        showSkipButton(true);
        setVisible(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        openMenu();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        openMenu();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if (newFragment == null) {
            return;
        }
        String s_1 = newFragment.getTag();
        if (s_1 == null) {
            return;
        }

        if (s_1.substring(s_1.length() - 1).equals("0")){ //first sliderPage

            setColorSkipButton(ContextCompat.getColor(this, R.color.colorPrimary));
            setColorDoneText(ContextCompat.getColor(this, R.color.colorPrimary));
            setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary), Color.BLACK);
            setNextArrowColor(ContextCompat.getColor(this, R.color.colorPrimary));

        }
        else {
            setColorSkipButton(Color.WHITE);
            setColorDoneText(Color.WHITE);
            setIndicatorColor(Color.WHITE, Color.BLACK);
            setNextArrowColor(Color.WHITE);
        }
    }

    // Opens the menu
    public void openMenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
