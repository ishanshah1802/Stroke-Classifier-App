package com.example.application;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    Cursor cursor = dbHelper.getUserByEmail(email);
                    if (cursor != null && cursor.moveToFirst()) {
                        String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("doctor_password"));
                        if (password.equals(storedPassword)) {
                            dbHelper.setLoggedInUser(email);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Login Success.", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "No account with this email.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        boolean registrationSuccess = getIntent().getBooleanExtra("registration_success", false);
        if (registrationSuccess) {
            Toast.makeText(this, "You have successfully registered. Now please log in.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, UserRegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
