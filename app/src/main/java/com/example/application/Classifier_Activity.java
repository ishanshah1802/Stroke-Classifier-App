package com.example.application;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.res.AssetFileDescriptor;

public class Classifier_Activity extends AppCompatActivity {

    private static final String TAG = "Classifier_Activity";
    private static final int REQUEST_CODE_IMAGE = 1;
    private static final int IMG_SIZE = 224;

    private ImageView ivMedicalImage;
    private TextView tvResult, tvSelectedImageName;
    private Button btnUpload, btnPredict;
    private Button button;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;

    private Interpreter tflite;
    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier);

        dbHelper = new DatabaseHelper(this);

        String user = dbHelper.getLoggedInUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        button = findViewById(R.id.logout);
        ivMedicalImage = findViewById(R.id.ivMedicalImage);
        tvResult = findViewById(R.id.tvResult);
        btnUpload = findViewById(R.id.btnUpload);
        btnPredict = findViewById(R.id.btnPredict);
        tvSelectedImageName = findViewById(R.id.tvSelectedImageName);
        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Classify the Stroke");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

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

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize TFLite interpreter
        try {
            tflite = new Interpreter(loadModelFile());
            Log.d(TAG, "Model loaded successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
            tvResult.setText("Error loading model: " + e.getMessage());
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    if (tflite == null) {
                        tvResult.setText("Model not loaded");
                        return;
                    }
                    new PredictTask().execute(selectedImage);
                } else {
                    tvResult.setText("Please select an image first.");
                    tvResult.setVisibility(View.VISIBLE);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.clearLoggedInUser();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri selectedImageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                selectedImage = BitmapFactory.decodeStream(inputStream);
                ivMedicalImage.setImageBitmap(selectedImage);
                tvSelectedImageName.setText(getFileName(selectedImageUri));
                tvSelectedImageName.setVisibility(View.VISIBLE);
                ivMedicalImage.setVisibility(View.VISIBLE);
                btnPredict.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private String predictImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMG_SIZE, IMG_SIZE, true);
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * IMG_SIZE * IMG_SIZE * 3).order(ByteOrder.nativeOrder());

        for (int y = 0; y < IMG_SIZE; y++) {
            for (int x = 0; x < IMG_SIZE; x++) {
                int pixel = resizedBitmap.getPixel(x, y);
                inputBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f); // Red
                inputBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);  // Green
                inputBuffer.putFloat((pixel & 0xFF) / 255.0f);         // Blue
            }
        }

        float[][] output = new float[1][2];
        tflite.run(inputBuffer, output);

        return output[0][0] > output[0][1] ? "CE" : "LAA";
    }

    @Nullable
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private class PredictTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            tvResult.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return predictImage(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(String prediction) {
            progressBar.setVisibility(View.GONE);
            tvResult.setText("Prediction: " + prediction);
            tvResult.setVisibility(View.VISIBLE);
        }
    }
}
