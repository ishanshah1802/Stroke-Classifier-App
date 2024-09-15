package com.example.application;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, birthdateEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Spinner genderSpinner;
    private Button registerButton;
    private DatabaseHelper dbHelper;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        dbHelper = new DatabaseHelper(this);
//        dbHelper.logDoctorDetailsColumns();

        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        genderSpinner = findViewById(R.id.gender_spinner);
        birthdateEditText = findViewById(R.id.birthdate);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progressBar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String birthdate = birthdateEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (firstName.isEmpty() || lastName.isEmpty() || birthdate.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserRegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserRegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put("doctor_first_name", firstName);
                    values.put("doctor_last_name", lastName);
                    values.put("doctor_gender", gender);
                    values.put("doctor_birthdate", birthdate);
                    values.put("doctor_email", email);
                    values.put("doctor_password", password);

                    boolean isInserted = dbHelper.addUser(values);
                    progressBar.setVisibility(View.GONE);

                    if (isInserted) {
                        Toast.makeText(UserRegistrationActivity.this, "Account Created.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Classifier_Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserRegistrationActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(UserRegistrationActivity.this, MainActivity.class);
        intent.putExtra("registration_success", true);
        startActivity(intent);
        finish();
    }
}
