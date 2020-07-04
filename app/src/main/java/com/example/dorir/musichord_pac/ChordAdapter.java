package com.example.dorir.musichord_pac;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChordAdapter extends ArrayAdapter<String> {

    // The array of images
    private Bitmap[] instruments;

    // The array of names
    private String[] names;

    // The context
    private Context context;

    public ChordAdapter(@NonNull Context context, String[] names, Bitmap[] instruments) {
        super(context, R.layout.chord_row, names);
        this.instruments = instruments;
        this.names = names;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.chord_row, null);
        TextView t1 = (TextView)row.findViewById(R.id.name);
        ImageView i1 = (ImageView)row.findViewById(R.id.picChord);

        t1.setText(names[position]);
        i1.setImageBitmap(instruments[position]);

        return row;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
