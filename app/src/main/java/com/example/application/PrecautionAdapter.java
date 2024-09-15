package com.example.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.application.R;

import java.util.List;

public class PrecautionAdapter extends ArrayAdapter<Precaution> {

    public PrecautionAdapter(Context context, List<Precaution> precautions) {
        super(context, 0, precautions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Precaution precaution = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.precaution_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.precaution_title);
        titleTextView.setText(precaution.getTitle());

        return convertView;
    }
}
