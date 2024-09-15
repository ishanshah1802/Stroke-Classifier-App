package com.example.application;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class DoctorProfileActivity extends AppCompatActivity {

    private static final String TAG = "DoctorProfileActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText genderEditText;
    private EditText birthdateEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button saveButton;
    private ImageView editButton;
    private ImageView deleteButton;
    private TextView profileHeading;

    private DatabaseHelper dbHelper;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Your Profile");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

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
                } else if (id == R.id.nav_logout) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        loadUserProfile();
    }

    private void initializeViews() {
        firstNameEditText = findViewById(R.id.edit_first_name);
        lastNameEditText = findViewById(R.id.edit_last_name);
        genderEditText = findViewById(R.id.edit_gender);
        birthdateEditText = findViewById(R.id.edit_birthdate);
        emailEditText = findViewById(R.id.edit_email);
        passwordEditText = findViewById(R.id.edit_password);
        saveButton = findViewById(R.id.button_save);
        editButton = findViewById(R.id.button_edit);
        deleteButton = findViewById(R.id.button_delete);
        profileHeading = findViewById(R.id.profile_heading);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditing(true);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement delete profile functionality
                // e.g., dbHelper.deleteProfile(userId);
            }
        });
    }

    private void saveProfile() {
        String email = dbHelper.getLoggedInUser();
        if (email != null) {
            ContentValues values = new ContentValues();
            values.put("doctor_first_name", profileHeading.getText().toString());
            values.put("doctor_first_name", firstNameEditText.getText().toString());
            values.put("doctor_last_name", lastNameEditText.getText().toString());
            values.put("doctor_gender", genderEditText.getText().toString());
            values.put("doctor_birthdate", birthdateEditText.getText().toString());
            values.put("doctor_email", emailEditText.getText().toString());
            values.put("doctor_password", passwordEditText.getText().toString());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.update(DatabaseHelper.TABLE_DOCTOR_DETAILS, values, "doctor_email = ?", new String[]{email});
            toggleEditing(false);
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        String email = dbHelper.getLoggedInUser();
        if (email != null) {
            Cursor cursor = dbHelper.getUserByEmail(email);
            if (cursor != null && cursor.moveToFirst()) {
                profileHeading.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_first_name")) + "'s Profile");
                firstNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_first_name")));
                lastNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_last_name")));
                genderEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_gender")));
                birthdateEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_birthdate")));
                emailEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_email")));
                passwordEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("doctor_password")));
                cursor.close();
            }
        }
    }

    private void toggleEditing(boolean isEditing) {
        firstNameEditText.setEnabled(isEditing);
        lastNameEditText.setEnabled(isEditing);
        genderEditText.setEnabled(isEditing);
        birthdateEditText.setEnabled(isEditing);
        emailEditText.setEnabled(isEditing);
        passwordEditText.setEnabled(isEditing);
        saveButton.setEnabled(isEditing);
        this.isEditing = isEditing;
        if (isEditing) {
            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
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
