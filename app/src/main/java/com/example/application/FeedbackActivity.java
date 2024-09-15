package com.example.application;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = "FeedbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFeedbackDialog();
    }

    private void showFeedbackDialog() {
        Log.d(TAG, "Showing feedback dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
        builder.setTitle("Feedback");

        final EditText feedbackInput = new EditText(FeedbackActivity.this);
        feedbackInput.setHint("Enter your feedback or suggestions");
        builder.setView(feedbackInput);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String feedback = feedbackInput.getText().toString();
                Log.d(TAG, "Feedback submitted: " + feedback);
                if (!feedback.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FeedbackActivity.this, "Feedback cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Feedback dialog cancelled");
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "Feedback dialog should be shown now");
    }
}
