package com.example.application;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrecautionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precaution_detail);

        String title = getIntent().getStringExtra("title");
        String detail = getIntent().getStringExtra("detail");

        TextView titleTextView = findViewById(R.id.precaution_title);
        TextView detailTextView = findViewById(R.id.precaution_detail);

        titleTextView.setText(title);
        detailTextView.setText(detail);
    }
}
