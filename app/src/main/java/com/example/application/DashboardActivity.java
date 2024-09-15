package com.example.application;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView welcomeUserTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Dashboard");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        welcomeUserTextView = findViewById(R.id.welcome_user);

        databaseHelper = new DatabaseHelper(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d(TAG, "Menu item selected: " + id);

                if (id == R.id.nav_dashboard) {
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                } else if (id == R.id.nav_your_profile) {
                    startActivity(new Intent(getApplicationContext(), DoctorProfileActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                } else if (id == R.id.nav_appointments) {
                    startActivity(new Intent(getApplicationContext(), AppointmentsActivity.class));
                } else if (id == R.id.nav_precautions) {
                    startActivity(new Intent(getApplicationContext(), PrecautionsActivity.class));
                } else if (id == R.id.nav_classify) {
                    startActivity(new Intent(getApplicationContext(), Classifier_Activity.class));
                } else if (id == R.id.nav_help) {
                    startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                } else if (id == R.id.nav_logout) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        LinearLayout homeButton = findViewById(R.id.button_home);
        LinearLayout appointmentsButton = findViewById(R.id.button_appointments);
        LinearLayout classifyButton = findViewById(R.id.button_classify);
        LinearLayout helpButton = findViewById(R.id.button_help);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
            }
        });

        appointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, AppointmentsActivity.class));
            }
        });

        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Classifier_Activity.class));
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, HelpActivity.class));
            }
        });

        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        String email = databaseHelper.getLoggedInUser();
        if (email != null) {
            Cursor cursor = databaseHelper.getUserByEmail(email);
            if (cursor != null && cursor.moveToFirst()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("doctor_first_name"));
                welcomeUserTextView.setText("Welcome " + firstName + "!");
                cursor.close();
            }
        } else {
            welcomeUserTextView.setText("Welcome User!");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
