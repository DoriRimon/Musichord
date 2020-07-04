package com.example.dorir.musichord_pac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
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

public class GuitarChordNamer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    // The dictionary - from chord types names to their values in the A major scale
    private LinkedHashMap<String, double[]> scaling = new LinkedHashMap<>();

    // The standard guitar tuning by the number format I created
    private double[] guitarTuning = new double[] {4.5, 1, 3.5, 6, 2, 4.5};

    // Contains the A major scale values (in a number format I created)
    private final double[] originalScale = new double[] { 1.0, 2.0, 3.0, 3.5, 4.5, 5.5, 6.5 };

    // All music notes names
    private String[] roots = new String[] {"A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab"};

    // Values of the notes written above (in the format I mentioned earlier)
    private double[] rootsVal = new double[] {1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5};

    // The values of the guitar section that is relevant at the moment
    private double[][] diagram = new double[6][6];

    // The values of the notes selected
    private double[] choices = new double[] {0, 0, 0, 0, 0, 0};

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

    // The text view for the little x that shows the open fret
    private TextView x;

    // The dialog that shows the options
    private static Dialog dialog;

    // Boolean to check if a valid chord was entered
    private static boolean flag;

    // The database
    DatabaseHelper mdb;

    // The Buttons Navigator at the bottom of the screen
    private BottomNavigationView bottomNavigationView;

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
        mapFill();

        // ----- Adding on click listeners -----
        rg0 = findViewById(R.id.rg0_2);
        rg1 = findViewById(R.id.rg1_2);
        rg2 = findViewById(R.id.rg2_2);
        rg3 = findViewById(R.id.rg3_2);
        rg4 = findViewById(R.id.rg4_2);
        rg5 = findViewById(R.id.rg5_2);
        groups = new RadioGroup[]{rg0, rg1, rg2, rg3, rg4, rg5};

        linearLayout = findViewById(R.id.r_buttons);
        x = findViewById(R.id.x2);

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

    BottomNavigationView.OnNavigationItemSelectedListener select = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.guitar_selector:
                    return true;
                case R.id.ukulele_selector:
                    openUke();
                    return true;
                case R.id.piano_selector:
                    return true;
            }
            return false;
        }
    };

    // On checked change listener
    CompoundButton.OnCheckedChangeListener checked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton cb, boolean b) {
            switch (cb.getId()){
                case R.id.music_dialog:
                    if (b) {
                        Intent svc = new Intent(GuitarChordNamer.this, BackgroundSoundService.class);
                        startService(svc);
                    }
                    else{
                        Intent svc = new Intent(GuitarChordNamer.this, BackgroundSoundService.class);
                        stopService(svc);
                    }
                    break;
            }
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
                namePrint();
                break;
            case R.id.last_searched_dialog:
                dialog.dismiss();
                openLastSearched();
                break;
            case R.id.options:
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
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        switch (parent.getId()){
            case R.id.instrument2:
                switch (pos){
                    case 0: // guitar
                        break;
                    case 1: // ukulele
                        openUke();
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
    public void addData(String name, byte[] image){
        mdb.addData(name, image, "guitar");
    }

    // Controls the back button (navigates to the menu page)
    @Override
    public void finish() {
        super.finish();
        openMenu();
    }

    // Fills the diagram with the notes values
    public void diagramFill(int fret){
        for (int j = 0; j < 6; j++)
            diagram[0][j] = guitarTuning[j] + fret * 0.5;
        for (int i = 1; i < 6; i++) {
            for (int j = 0; j < 6; j++)
                diagram[i][j] = diagram[i - 1][j] + 0.5;
        }
    }

    // Fills choices with the values from the radio buttons
    public void choicesFill(){
        for(int i = 0; i < groups.length; i++){
            RadioGroup rg = groups[i];
            int selectedId = rg.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            for(int j = 0; j < rg.getChildCount(); j++){
                RadioButton r = (RadioButton)rg.getChildAt(j);
                boolean flag = r.isChecked();
                if(flag) {
                    if(j == 0)
                        choices[i] = 0;
                    else
                        choices[i] = diagram[j-1][i];
                }
            }
        }
    }

    // Checks all permutes of an array
    // *This function is used to see all the permutes of a chord*
    public void permutes(double[] arr, int k, double root, double baseNote){
        //Toast.makeText(this, "Entered Permutes", Toast.LENGTH_SHORT).show();
        for(int i = k; i < arr.length; i++){
            swap(arr, i, k);
            permutes(arr, k+1, root, baseNote);
            swap(arr, k, i);
        }
        if(k == arr.length - 1) {
            //Toast.makeText(this, "Called mapSearch on: " + Arrays.toString(arr), Toast.LENGTH_SHORT).show();
            String type = mapSearch(arr);
            if(!type.equals("none")) {
                flag = true;
                revealName(type, root, baseNote);
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
    public double[] actualChord(){
        int f = Integer.parseInt(fret.getText().toString());
        diagramFill(f);
        choicesFill();
        //Toast.makeText(this, "Choices: " + Arrays.toString(choices), Toast.LENGTH_SHORT).show();
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
    public void namePrint() {
        double[] chord = actualChord();
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

        for (int i = 0; i < chord.length; i++){
            if (!flag) {
                double[] slice = Arrays.copyOfRange(chord, 1, chord.length);
                permutes(osc(slice, root), 0, root, baseNote);
            }
            if (!flag) {
                root = chord[i];
                permutes(osc(chord, root), 0, root, baseNote);
            }
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
                if (arr[j] == arr[i])
                    flag = false;
            }
            if (flag)
                res[i] = arr[i];
        }
        res = Arrays.stream(res).filter(x -> x != 0).toArray();
        return res;
    }

    // The actual print of the name to the screen
    public void revealName(String type, double root, double baseNote){
        //Toast.makeText(this, "Entered revealName", Toast.LENGTH_SHORT).show();
        String s = "";
        int p = 0;
        for(int i = 0; i < rootsVal.length; i++){
            if(rootsVal[i] == root)
                p = i;
        }
        s += roots[p];
        s += type;
        if (baseNote != root){
            p = 0;
            for (int i = 0; i < rootsVal.length; i++){
                if (rootsVal[i] == baseNote)
                    p = i;
            }
            s += "/" + roots[p];
        }
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
    public String mapSearch(double[] chord){
        //double[] newChord = osc(chord, root);
        //Toast.makeText(this, Arrays.toString(newChord), Toast.LENGTH_SHORT).show();
        for(Map.Entry<String, double[]> entry : scaling.entrySet()) {
            if(Arrays.equals(chord, entry.getValue()))
                return entry.getKey();
        }
        return "none";
    }

    // Opens the lsat searched activity
    public void openLastSearched(){
        Intent intent = new Intent(this, GuitarListData.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //CustomIntent.customType(this, "left-to-right");
    }

    // Opens the ukulele activity
    public void openUke(){
        Intent intent = new Intent(this, UkuleleChordNamer.class);

        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(go, "go");
        pairs[1] = new Pair<View, String>(fret, "fret");
        pairs[2] = new Pair<View, String>(options, "options");
        pairs[3] = new Pair<View, String>(bottomNavigationView, "bottom_bar");
        //pairs[4] = new Pair<View, String>(linearLayout, "buttons");
        pairs[4] = new Pair<View, String>(x, "x");

        ActivityOptions ao = ActivityOptions.makeSceneTransitionAnimation(this, pairs);

        startActivity(intent, ao.toBundle());
    }

    // Opens the menu
    public void openMenu(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //CustomIntent.customType(this, "left-to-right");
    }

    // Opens the under construction activity
    public void openUnderConstruction(){
        Intent intent = new Intent(this, UnderConstruction.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //CustomIntent.customType(this, "left-to-right");
    }

    // Fills the dictionary
    public void mapFill(){
        scaling.put("", new double[]{originalScale[0], originalScale[2], originalScale[4]});
        scaling.put("major", new double[]{originalScale[0], originalScale[2], originalScale[4]});
        scaling.put("5", new double[]{originalScale[0], originalScale[4]});
        scaling.put("sus4", new double[]{originalScale[0], originalScale[3], originalScale[4]});
        scaling.put("sus2", new double[]{originalScale[0], originalScale[1], originalScale[4]});
        scaling.put("add9", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[1]}); //9
        scaling.put("6", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[5]});
        scaling.put("6/9", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6], originalScale[1]}); //9
        scaling.put("maj7", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6]});
        scaling.put("maj9", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6], originalScale[1]}); //9
        scaling.put("maj7#11", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6], originalScale[3] + 0.5}); //11
        scaling.put("maj13", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6], originalScale[1], originalScale[5]}); //9, 13
        scaling.put("m", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4]});
        scaling.put("minor", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4]});
        scaling.put("m(add9)", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[1]}); //9
        scaling.put("m6", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[5]});
        scaling.put("mb6", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[5] - 0.5});
        scaling.put("m6/9", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[5], originalScale[1]}); //9
        scaling.put("m7", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[6] - 0.5});
        scaling.put("m7b5", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4] - 0.5, originalScale[6] - 0.5});
        scaling.put("m(maj7)", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[6]});
        scaling.put("m9", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("m9b5", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4] - 0.5, originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("m9(maj7)", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[6], originalScale[1]}); //9
        scaling.put("m11", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[6] - 0.5, originalScale[1], originalScale[3]}); //9, 11
        // not guitar oriented: scaling.put("m13", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4], originalScale[6] - 0.5, originalScale[1], originalScale[3], originalScale[5]}); //9, 11, 13
        scaling.put("7", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6] - 0.5});
        scaling.put("7sus4", new double[]{originalScale[0], originalScale[3], originalScale[4], originalScale[6] - 0.5});
        scaling.put("7b5", new double[]{originalScale[0], originalScale[2], originalScale[4] - 0.5, originalScale[6] - 0.5});
        scaling.put("9", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("9sus4", new double[]{originalScale[0], originalScale[3], originalScale[4], originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("9b5", new double[]{originalScale[0], originalScale[2], originalScale[4] - 0.5, originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("7b9", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6] - 0.5, originalScale[1] - 0.5}); //9
        scaling.put("7#9", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6] - 0.5, originalScale[1] + 0.5}); //9
        scaling.put("7b5(#9)", new double[]{originalScale[0], originalScale[2], originalScale[4] - 0.5, originalScale[6] - 0.5, originalScale[1] + 0.5}); //9
        scaling.put("11", new double[]{originalScale[0], originalScale[4], originalScale[6] - 0.5, originalScale[1], originalScale[3]}); //9, 11
        scaling.put("7#11", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6] - 0.5, originalScale[3] + 0.5});
        scaling.put("13", new double[]{originalScale[0], originalScale[2], originalScale[4], originalScale[6] - 0.5, originalScale[1], originalScale[5]}); //9, 13
        scaling.put("13sus4", new double[]{originalScale[0], originalScale[3], originalScale[4], originalScale[6] - 0.5, originalScale[1], originalScale[5]}); //9, 13
        scaling.put("aug", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5});
        scaling.put("+", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5});
        scaling.put("aug7", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5});
        scaling.put("+7", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5});
        scaling.put("aug9", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("+9", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5, originalScale[1]}); //9
        scaling.put("aug7b9", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5, originalScale[1] - 0.5}); //9
        scaling.put("+7b9", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5, originalScale[1] - 0.5}); //9
        scaling.put("aug7#9", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5, originalScale[1] + 0.5}); //9
        scaling.put("+7#9", new double[]{originalScale[0], originalScale[2], originalScale[4] + 0.5, originalScale[6] - 0.5, originalScale[1] + 0.5}); //9
        scaling.put("dim", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4] - 0.5});
        scaling.put("dim7", new double[]{originalScale[0], originalScale[2] - 0.5, originalScale[4] - 0.5, originalScale[6] - 1});
    }
}
