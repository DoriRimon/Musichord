package com.example.dorir.musichord_pac;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChordNamer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    // The values of the section that is relevant at the moment
    private double[][] guitarDiagram = new double[6][6];
    private double[][] ukuleleDiagram = new double[6][4];

    // The values of the notes selected
    private double[] guitarChoices = new double[] {0, 0, 0, 0, 0, 0};
    private double[] ukuleleChoices = new double[] {0, 0, 0, 0};

    // An EditText that lets the user to choose the fret he's on
    private EditText fret;

    // A button to start the search
    private Button go;

    // A button to open an options dialog
    private Button options;

    // Switch to Start the music
    private Switch music;

    // Open the list of the last chords searched
    private Button ls; // (last searched)

    // Settings button
    private Button settings;

    // All the radio buttons of the guitar neck
    private RadioGroup rg0;
    private RadioGroup rg1;
    private RadioGroup rg2;
    private RadioGroup rg3;
    private RadioGroup rg4;
    private RadioGroup rg5;
    private RadioGroup[] groups;
    private LinearLayout linearLayout;

    // The dialog that shows the options
    private static Dialog dialog;

    // Boolean to check if a valid chord was entered
    private static boolean flag;

    // The database
    DatabaseHelper mdb;

    // The Buttons Navigator at the bottom of the screen
    private BottomNavigationView bottomNavigationView;

    // Current chord
    private String current = Globe.instruments[0]; // Guitar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guitar_chord_namer);

        // Exclude status and navigation bars as shared elements
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

        // Fills the dictionary
        Globe.mapFill();

        // ----- Adding on click listeners -----
        rg0 = findViewById(R.id.rg0_2);
        rg1 = findViewById(R.id.rg1_2);
        rg2 = findViewById(R.id.rg2_2);
        rg3 = findViewById(R.id.rg3_2);
        rg4 = findViewById(R.id.rg4_2);
        rg5 = findViewById(R.id.rg5_2);
        groups = new RadioGroup[]{rg0, rg1, rg2, rg3, rg4, rg5};

        linearLayout = findViewById(R.id.r_buttons);

        go = findViewById(R.id.go2);
        go.setOnClickListener(this);

        fret = findViewById(R.id.fret2);

        options = findViewById(R.id.options);
        options.setOnClickListener(this);

        // The database
        mdb = new DatabaseHelper(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.guitar_selector);
        bottomNavigationView.setOnNavigationItemSelectedListener(select);

        // Creating the dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.options_dialog);

        // ----- Dialog on click listeners -----

        ls = dialog.findViewById(R.id.last_searched_dialog);
        ls.setOnClickListener(this);

        settings = dialog.findViewById(R.id.settings_dialog);
        settings.setOnClickListener(this);

        music = dialog.findViewById(R.id.music_dialog);
        music.setOnCheckedChangeListener(checked);

        // Check if music is playing (if it is turn the switch on)
        if (isMyServiceRunning(BackgroundSoundService.class))
            music.setChecked(true);
    }

    BottomNavigationView.OnNavigationItemSelectedListener select = menuItem -> {
        switch (menuItem.getItemId()){
            case R.id.guitar_selector:
                setGuitarScreen();
                return true;
            case R.id.ukulele_selector:
                setUkuleleScreen();
                return true;
            case R.id.piano_selector:
                return true;
        }
        return false;
    };

    // On checked change listener
    CompoundButton.OnCheckedChangeListener checked = (cb, b) -> {
        switch (cb.getId()){
            case R.id.music_dialog:
                Intent svc = new Intent(ChordNamer.this, BackgroundSoundService.class);
                if (b) {
                    startService(svc);
                }
                else{
                    stopService(svc);
                }
                break;
        }
    };

    // Checks if the service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // The onClick method
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go2:
                guitarNamePrint();
                break;
            case R.id.last_searched_dialog:
                dialog.dismiss();
                openLastSearched();
                break;
            case R.id.options:
            case R.id.options2:
                // ----- Move the dialog to be under the options button -----
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                wlp.gravity = Gravity.TOP | Gravity.LEFT;
                wlp.x = (int)options.getX();   //x position
                wlp.y = (int)options.getY() + options.getHeight();   //y position
                dialog.show();
                break;
            case R.id.settings_dialog:
                openUnderConstruction();
                break;
            case R.id.go3:
                ukuleleNamePrint();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        switch (parent.getId()){
            case R.id.instrument2:
                switch (pos){
                    case 0: // guitar
                        setGuitarScreen();
                        break;
                    case 1: // ukulele
                        setUkuleleScreen();
                        break;
                    case 2: // piano
                        break;
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // Add data to the database
    public void addData(String name, byte[] image) {
        mdb.addData(name, image, current);
    }

    // Controls the back button (navigates to the menu page)
    @Override
    public void finish() {
        super.finish();
        openMenu();
    }

    // Fills the diagram with the notes values
    public void guitarDiagramFill(int fret){
        for (int j = 0; j < 6; j++)
            guitarDiagram[0][j] = Globe.guitarTuning[j] + fret * 0.5;
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 6; j++)
                guitarDiagram[i][j] = guitarDiagram[i - 1][j] + 0.5;
        }
    }

    public void ukuleleDiagramFill(int fret){
        for (int j = 0; j < 4; j++)
            ukuleleDiagram[0][j] = Globe.ukuleleTuning[j] + fret * 0.5;
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 4; j++)
                ukuleleDiagram[i][j] = ukuleleDiagram[i - 1][j] + 0.5;
        }
    }

    // Fills choices with the values from the radio buttons
    public void guitarChoicesFill(){
        for(int i = 0; i < groups.length; i++){
            RadioGroup rg = groups[i];
            int selectedId = rg.getCheckedRadioButtonId();
            for(int j = 0; j < rg.getChildCount(); j++){
                RadioButton r = (RadioButton)rg.getChildAt(j);
                boolean flag = r.isChecked();
                if(flag) {
                    if(j == 0)
                        guitarChoices[i] = 0;
                    else
                        guitarChoices[i] = guitarDiagram[j-1][i];
                }
            }
        }
    }

    public void ukuleleChoicesFill(){
        for(int i = 0; i < groups.length; i++){
            RadioGroup rg = groups[i];
            int selectedId = rg.getCheckedRadioButtonId();
            for(int j = 0; j < rg.getChildCount(); j++){
                RadioButton r = (RadioButton)rg.getChildAt(j);
                boolean flag = r.isChecked();
                if(flag) {
                    if(j == 0)
                        ukuleleChoices[i] = 0;
                    else
                        ukuleleChoices[i] = ukuleleDiagram[j-1][i];
                }
            }
        }
    }

    // Checks all permutes of an array
    // *This function is used to see all the permutes of a chord*
    public void guitarPermutes(double[] arr, int k, double root, double baseNote){
        for(int i = k; i < arr.length; i++){
            swap(arr, i, k);
            guitarPermutes(arr, k+1, root, baseNote);
            swap(arr, k, i);
        }
        if(k == arr.length - 1) {
            String type = mapSearch(arr);
            if(!type.equals("none")) {
                flag = true;
                guitarRevealName(type, root, baseNote);
            }
        }
    }

    public void ukulelePermutes(double[] arr, int k, double root){
        for(int i = k; i < arr.length; i++){
            swap(arr, i, k);
            ukulelePermutes(arr, k+1, root);
            swap(arr, k, i);
        }
        if(k == arr.length - 1) {
            String type = mapSearch(arr);
            if(!type.equals("none")) {
                ukuleleRevealName(type, root);
                flag = true;
            }
        }
    }

    // Makes sure the chord array contains valid values
    public void resetArr(double[] arr){
        for (int k = 0; k < arr.length; k++){
            while(arr[k] >= 7)
                arr[k] -= 6;
            while(arr[k] <= 0)
                arr[k] += 6;
        }
    }

    // Swaps between two values in an array
    public void swap(double[] chord, int i, int k){
        double a = chord[i];
        chord[i] = chord[k];
        chord[k] = a;
    }

    // Creates a clean chord form the input
    // --> without repeats and at the correct size
    public double[] actualChord(String instrument){
        int f = Integer.parseInt(fret.getText().toString());
        double[] choices = new double[]{};
        if (instrument.equals(Globe.instruments[0])) { //Guitar
            guitarDiagramFill(f);
            guitarChoicesFill();
            choices = guitarChoices;
        }
        if (instrument.equals(Globe.instruments[1])) { // Ukulele
            ukuleleDiagramFill(f);
            ukuleleChoicesFill();
            choices = ukuleleChoices;

        }

        int count = 0;
        for (int i = 0; i < choices.length; i++) {
            if (choices[i] != 0)
                count++;
        }
        double[] chord = new double[count];
        int j = 0;
        for (int i = 0; i < chord.length; i++) {
            while (j < choices.length && chord[i] == 0) {
                if (choices[j] != 0)
                    chord[i] = choices[j];
                j++;
            }
        }
        resetArr(chord);
        return chord;
    }

    // Checks if the chord is valid
    public void guitarNamePrint() {
        double[] chord = actualChord(Globe.instruments[0]); // Guitar
        flag = false;
        double root;
        double baseNote;
        if (chord.length != 0) {
            root = chord[0];
            baseNote = chord[0];
        }
        else {
            vibrate();
            return;
        }

        for (double v : chord) {
            if (!flag) {
                double[] slice = Arrays.copyOfRange(chord, 1, chord.length);
                guitarPermutes(osc(slice, root), 0, root, baseNote);
            }
            if (!flag) {
                root = v;
                guitarPermutes(osc(chord, root), 0, root, baseNote);
            }
        }

        if (!flag)
            vibrate();
    }

    public void ukuleleNamePrint() {
        double[] chord = actualChord(Globe.instruments[1]); // Ukulele
        flag = false;
        double root;
        if (chord.length != 0) {
            for (double v : chord) {
                root = v;
                ukulelePermutes(osc(chord, root), 0, root);
            }
        }
        else{
            vibrate();
            return;
        }
        if (!flag)
            vibrate();
    }

    // Vibrates the phone
    public void vibrate(){
        Intent i = new Intent(this, Broadcast.class);
        i.setAction("vibrate");
        this.sendBroadcast(i);
    }

    // Returns a new arrays in the original scale
    // --> 'original scale chord'
    public double[] osc(double[] arr, double root){
        double dist = root - 1;
        double[] res = new double[arr.length];
        for(int k = 0; k < arr.length; k++) {
            res[k] = arr[k] - dist;
            while(res[k] >= 7)
                res[k] -= 6;
            while(res[k] <= 0)
                res[k] += 6;
        }
        return arrayCleaner(res);
    }

    // Returns a new arrays without repeats
    public double[] arrayCleaner(double[] arr){
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
            res[i] = 0;
        boolean flag;
        for (int i = 0; i < arr.length; i++){
            flag = true;
            for (int j = 0; j < i; j++){
                if (arr[j] == arr[i]) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                res[i] = arr[i];
        }
        res = Arrays.stream(res).filter(x -> x != 0).toArray();
        return res;
    }

    // The actual print of the name to the screen
    public void guitarRevealName(String type, double root, double baseNote){
        String s = "";
        int p = 0;
        for(int i = 0; i < Globe.rootsVal.length; i++){
            if(Globe.rootsVal[i] == root)
                p = i;
        }
        s += Globe.roots[p];
        s += type;
        if (baseNote != root){
            p = 0;
            for (int i = 0; i < Globe.rootsVal.length; i++){
                if (Globe.rootsVal[i] == baseNote)
                    p = i;
            }
            s += "/" + Globe.roots[p];
        }
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        Bitmap b = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        linearLayout.draw(c);
        addData(s, getBytes(b));
    }

    public void ukuleleRevealName(String type, double r){
        if (flag)
            return;
        String s = "";
        int p = 0;
        for(int i = 0; i < Globe.rootsVal.length; i++){
            if(Globe.rootsVal[i] == r)
                p = i;
        }
        s += Globe.roots[p];
        s += type;
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        Bitmap b = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        linearLayout.draw(c);
        addData(s, getBytes(b));
    }

    // Turns image into byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Searches the chord in the dictionary
    public String mapSearch(double[] chord) {
        //double[] newChord = osc(chord, root);
        //Toast.makeText(this, Arrays.toString(newChord), Toast.LENGTH_SHORT).show();
        for (Map.Entry<String, double[]> entry : Globe.scaling.entrySet()) {
            if (Arrays.equals(chord, entry.getValue()))
                return entry.getKey();
        }
        return "none";
    }

    private void setGuitarScreen() {
        setContentView(R.layout.activity_guitar_chord_namer);

        // Exclude status and navigation bars as shared elements
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

        // ----- Adding on click listeners -----
        rg0 = findViewById(R.id.rg0_2);
        rg1 = findViewById(R.id.rg1_2);
        rg2 = findViewById(R.id.rg2_2);
        rg3 = findViewById(R.id.rg3_2);
        rg4 = findViewById(R.id.rg4_2);
        rg5 = findViewById(R.id.rg5_2);
        groups = new RadioGroup[]{rg0, rg1, rg2, rg3, rg4, rg5};

        linearLayout = findViewById(R.id.r_buttons);

        go = findViewById(R.id.go2);
        go.setOnClickListener(this);

        fret = findViewById(R.id.fret2);

        options = findViewById(R.id.options);
        options.setOnClickListener(this);

        // The database
        mdb = new DatabaseHelper(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.guitar_selector);
        bottomNavigationView.setOnNavigationItemSelectedListener(select);

        // Creating the dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.options_dialog);

        // ----- Dialog on click listeners -----

        ls = dialog.findViewById(R.id.last_searched_dialog);
        ls.setOnClickListener(this);

        settings = dialog.findViewById(R.id.settings_dialog);
        settings.setOnClickListener(this);

        music = dialog.findViewById(R.id.music_dialog);
        music.setOnCheckedChangeListener(checked);

        // Check if music is playing (if it is turn the switch on)
        if (isMyServiceRunning(BackgroundSoundService.class))
            music.setChecked(true);

        current = Globe.instruments[0]; // Guitar
    }

    private void setUkuleleScreen() {
        setContentView(R.layout.activity_ukulele_chord_namer);

        // Exclude status and navigation bars as shared elements
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

        // ----- Adding on click listeners -----
        rg0 = findViewById(R.id.rg0_3);
        rg1 = findViewById(R.id.rg1_3);
        rg2 = findViewById(R.id.rg2_3);
        rg3 = findViewById(R.id.rg3_3);
        groups = new RadioGroup[]{rg0, rg1, rg2, rg3};
        linearLayout = findViewById(R.id.r_buttons);

        go = findViewById(R.id.go3);
        go.setOnClickListener(this);
        fret = findViewById(R.id.fret3);
        options = findViewById(R.id.options2);
        options.setOnClickListener(this);

        bottomNavigationView = findViewById(R.id.bottom_ukulele_navigation);
        bottomNavigationView.setSelectedItemId(R.id.ukulele_selector);
        bottomNavigationView.setOnNavigationItemSelectedListener(select);

        mdb = new DatabaseHelper(this);

        // Creating the dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.options_dialog);

        // ----- Dialog on click listeners -----
        ls = dialog.findViewById(R.id.last_searched_dialog);
        ls.setOnClickListener(this);

        settings = dialog.findViewById(R.id.settings_dialog);
        settings.setOnClickListener(this);

        music = dialog.findViewById(R.id.music_dialog);
        music.setOnCheckedChangeListener(checked);

        // Check if music is playing (if it is turn the switch on)
        if (isMyServiceRunning(BackgroundSoundService.class))
            music.setChecked(true);

        current = Globe.instruments[1]; // Ukulele
    }

    // Opens the menu
    public void openMenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Opens the under construction activity
    public void openUnderConstruction(){
        Intent intent = new Intent(this, UnderConstruction.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // Opens the lsat searched activity
    public void openLastSearched(){
        Intent intent = new Intent(this, LastSearched.class);
        intent.putExtra("instrument", current);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
