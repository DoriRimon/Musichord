package com.example.dorir.musichord_pac;

import java.util.LinkedHashMap;

public class Globe {
    // ----- Global Variables -----

    // The dictionary - from chord types names to their values in the A major scale
    public static LinkedHashMap<String, double[]> scaling = new LinkedHashMap<>();

    // The standard guitar tuning by the number format I created
    public static double[] guitarTuning = new double[] {4.5, 1, 3.5, 6, 2, 4.5};

    // The standard ukulele tuning by the number format I created
    public static double[] ukuleleTuning = new double[] {6, 2.5, 4.5, 1};

    // Contains the A major scale values (in a number format I created)
    public static final double[] originalScale = new double[] { 1.0, 2.0, 3.0, 3.5, 4.5, 5.5, 6.5 };

    // All music notes names
    public static String[] roots = new String[] {"A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab"};

    // Instruments names
    public static String[] instruments = new String[] {"guitar", "ukulele", "piano"};

    // Values of the notes written above (in the format I mentioned earlier)
    public static double[] rootsVal = new double[] {1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5};

    // ----- Global methods for the HashMap related work -----

    // Fills the dictionary
    public static void mapFill(){
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

    // ----- Global methods for GUI related work -----
}
