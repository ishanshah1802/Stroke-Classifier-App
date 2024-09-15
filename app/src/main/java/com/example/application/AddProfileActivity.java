package com.example.application;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AddProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText nameEditText;
    private EditText ageEditText;
    private EditText problemsEditText;
    private EditText addressEditText;
    private EditText mobileEditText;
    private Button saveButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        databaseHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Patient Detail");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nameEditText = findViewById(R.id.edit_text_name);
        ageEditText = findViewById(R.id.edit_text_age);
        problemsEditText = findViewById(R.id.edit_text_problems);
        addressEditText = findViewById(R.id.edit_text_address);
        mobileEditText = findViewById(R.id.edit_text_mobile);
        saveButton = findViewById(R.id.button_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String ageString = ageEditText.getText().toString();
                String problems = problemsEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();

                if (name.isEmpty() || ageString.isEmpty() || problems.isEmpty() || address.isEmpty() || mobile.isEmpty()) {
                    Toast.makeText(AddProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(ageString);
                String email = databaseHelper.getLoggedInUser();
                int doctorId = databaseHelper.getDoctorIdByEmail(email);

                showConfirmationDialog(name, age, problems, address, mobile, doctorId);
            }
        });
    }

    private void showConfirmationDialog(String name, int age, String problems, String address, String mobile, int doctorId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProfileActivity.this);
        builder.setTitle("Confirm Profile Addition");
        builder.setMessage("Are you sure you want to add this profile?\n\n" +
                "Name: " + name + "\n" +
                "Age: " + age + "\n" +
                "Problems: " + problems + "\n" +
                "Address: " + address + "\n" +
                "Mobile: " + mobile);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Profile profile = new Profile(name, age, problems, address, mobile);
                boolean isAdded = databaseHelper.addProfile(profile, doctorId);

                if (isAdded) {
                    Toast.makeText(AddProfileActivity.this, "Profile added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddProfileActivity.this, "Failed to add profile", Toast.LENGTH_SHORT).show();
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
