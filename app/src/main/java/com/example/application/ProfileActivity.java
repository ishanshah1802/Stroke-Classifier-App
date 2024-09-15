package com.example.application;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    FirebaseAuth auth;
    private ListView listView;
    private ProfileAdapter adapter;
    private ArrayList<Profile> profiles;
    private DatabaseHelper databaseHelper;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Patient Profiles");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
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
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        String loggedInEmail = databaseHelper.getLoggedInUser();
        Cursor cursor = databaseHelper.getUserByEmail(loggedInEmail);
        int doctorId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            doctorId = cursor.getInt(cursor.getColumnIndexOrThrow("doctor_id"));
            cursor.close();
        }

        if (doctorId == -1) {
            Toast.makeText(this, "Failed to get logged-in doctor ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ListView and set adapter
        listView = findViewById(R.id.profiles_list_view);
        profiles = databaseHelper.getProfilesByDoctorId(doctorId);
        adapter = new ProfileAdapter(this, profiles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Profile selectedProfile = profiles.get(position);
            Intent intent = new Intent(getApplicationContext(), ProfileDetailActivity.class);
            intent.putExtra("id", selectedProfile.getId());
            intent.putExtra("name", selectedProfile.getName());
            intent.putExtra("age", selectedProfile.getAge());
            intent.putExtra("problems", selectedProfile.getProblems());
            intent.putExtra("address", selectedProfile.getAddress());
            intent.putExtra("mobile", selectedProfile.getMobile());
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Profile selectedProfile = profiles.get(position);
            boolean isDeleted = databaseHelper.deleteProfile(selectedProfile.getId());
            if (isDeleted) {
                profiles.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(ProfileActivity.this, "Profile deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        FloatingActionButton addButton = findViewById(R.id.button_add_profile);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        profiles.clear();
        String loggedInEmail = databaseHelper.getLoggedInUser();
        Cursor cursor = databaseHelper.getUserByEmail(loggedInEmail);
        int doctorId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            doctorId = cursor.getInt(cursor.getColumnIndexOrThrow("doctor_id"));
            cursor.close();
        }

        if (doctorId != -1) {
            profiles.addAll(databaseHelper.getProfilesByDoctorId(doctorId));
            adapter.notifyDataSetChanged();
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
