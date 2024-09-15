package com.example.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<Profile> {

    public ProfileAdapter(Context context, List<Profile> profiles) {
        super(context, 0, profiles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Profile profile = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.profile_name);
        nameTextView.setText(profile.getName());

        return convertView;
    }
}
