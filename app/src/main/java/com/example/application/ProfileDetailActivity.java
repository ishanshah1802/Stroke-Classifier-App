package com.example.application;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseHelper databaseHelper;
    private int profileId;
    private static final String TAG = "ProfileDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        databaseHelper = new DatabaseHelper(this);

        String name = getIntent().getStringExtra("name");
        int age = getIntent().getIntExtra("age", 0);
        String problems = getIntent().getStringExtra("problems");
        String address = getIntent().getStringExtra("address");
        String mobile = getIntent().getStringExtra("mobile");
        profileId = getIntent().getIntExtra("id", -1);

        Log.d(TAG, "Profile ID received: " + profileId);

        TextView nameTextView = findViewById(R.id.profile_name);
        TextView ageTextView = findViewById(R.id.profile_age);
        TextView problemsTextView = findViewById(R.id.profile_problems);
        TextView addressTextView = findViewById(R.id.profile_address);
        TextView mobileTextView = findViewById(R.id.profile_mobile);
        Button deleteButton = findViewById(R.id.button_delete);

        setTitle(name + "'s Detail");
        nameTextView.setText(name);
        ageTextView.setText(String.valueOf(age));
        problemsTextView.setText(problems);
        addressTextView.setText(address);
        mobileTextView.setText(mobile);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailActivity.this);
        builder.setTitle("Delete Profile");
        builder.setMessage("Are you sure you want to delete this profile?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Attempting to delete profile with ID: " + profileId);
                boolean isDeleted = databaseHelper.deleteProfile(profileId);
                Log.d(TAG, "Deletion result: " + isDeleted);
                if (isDeleted) {
                    Toast.makeText(ProfileDetailActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileDetailActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ProfileDetailActivity.this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
