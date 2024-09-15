package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HelpActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Help");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Set up bottom navigation
        LinearLayout homeButton = findViewById(R.id.button_home);
        LinearLayout appointmentsButton = findViewById(R.id.button_appointments);
        LinearLayout classifyButton = findViewById(R.id.button_classify);
        LinearLayout helpButton = findViewById(R.id.button_help);

        homeButton.setOnClickListener(view -> startActivity(new Intent(HelpActivity.this, DashboardActivity.class)));
        appointmentsButton.setOnClickListener(view -> startActivity(new Intent(HelpActivity.this, AppointmentsActivity.class)));
        classifyButton.setOnClickListener(view -> startActivity(new Intent(HelpActivity.this, Classifier_Activity.class)));
        helpButton.setOnClickListener(view -> {
            // Already in HelpActivity, do nothing
        });
    }
}
