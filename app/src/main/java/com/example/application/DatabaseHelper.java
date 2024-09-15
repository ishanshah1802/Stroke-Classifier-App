package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stroke_classifier_app.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_DOCTOR_DETAILS = "doctor_details";
    private static final String DOCTOR_ID = "doctor_id";
    private static final String DOCTOR_FIRST_NAME = "doctor_first_name";
    private static final String DOCTOR_LAST_NAME = "doctor_last_name";
    private static final String DOCTOR_GENDER = "doctor_gender";
    private static final String DOCTOR_BIRTHDATE = "doctor_birthdate";
    private static final String DOCTOR_EMAIL = "doctor_email";
    private static final String DOCTOR_PASSWORD = "doctor_password";

    private static final String TABLE_PATIENT_PROFILES = "patient_details";
    private static final String PATIENT_ID = "patient_id";
    private static final String PATIENT_NAME = "patient_name";
    private static final String PATIENT_AGE = "patient_age";
    private static final String PATIENT_PROBLEMS = "patient_problems";
    private static final String PATIENT_ADDRESS = "patient_address";
    private static final String PATIENT_MOBILE = "patient_mobile";

    public static final String TABLE_USER_SESSION = "user_session";
    public static final String SESSION_EMAIL = "email";

    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDoctorTable = "CREATE TABLE " + TABLE_DOCTOR_DETAILS + " (" +
                DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DOCTOR_FIRST_NAME + " TEXT, " +
                DOCTOR_LAST_NAME + " TEXT, " +
                DOCTOR_GENDER + " TEXT, " +
                DOCTOR_BIRTHDATE + " TEXT, " +
                DOCTOR_EMAIL + " TEXT, " +
                DOCTOR_PASSWORD + " TEXT)";
        db.execSQL(createDoctorTable);

        String createProfileTable = "CREATE TABLE " + TABLE_PATIENT_PROFILES + " (" +
                PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PATIENT_NAME + " TEXT, " +
                PATIENT_AGE + " INTEGER, " +
                PATIENT_PROBLEMS + " TEXT, " +
                PATIENT_ADDRESS + " TEXT, " +
                PATIENT_MOBILE + " TEXT, " +
                DOCTOR_ID + " INTEGER, " +
                "FOREIGN KEY (" + DOCTOR_ID + ") REFERENCES " + TABLE_DOCTOR_DETAILS + " (" + DOCTOR_ID + "))";
        db.execSQL(createProfileTable);


        String CREATE_USER_SESSION_TABLE = "CREATE TABLE " + TABLE_USER_SESSION + "("
                + SESSION_EMAIL + " TEXT PRIMARY KEY)";
        db.execSQL(CREATE_USER_SESSION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SESSION);
        onCreate(db);
    }

    public boolean addUser(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(TABLE_DOCTOR_DETAILS, null, values);
        return result != -1;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_DOCTOR_DETAILS + " WHERE " + DOCTOR_EMAIL + "=?";
        return db.rawQuery(query, new String[]{email});
    }

    public void setLoggedInUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_SESSION, null, null);
        ContentValues values = new ContentValues();
        values.put(SESSION_EMAIL, email);
        db.insert(TABLE_USER_SESSION, null, values);
    }

    public void clearLoggedInUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_SESSION, null, null);
    }

    public String getLoggedInUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_SESSION, new String[]{SESSION_EMAIL}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow(SESSION_EMAIL));
            cursor.close();
            return email;
        }
        return null;
    }

    public int getDoctorIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + DOCTOR_ID + " FROM " + TABLE_DOCTOR_DETAILS + " WHERE " + DOCTOR_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            int doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(DOCTOR_ID));
            cursor.close();
            return doctorId;
        }
        return -1;
    }

    public boolean addProfile(Profile profile, int doctorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PATIENT_NAME, profile.getName());
        values.put(PATIENT_AGE, profile.getAge());
        values.put(PATIENT_PROBLEMS, profile.getProblems());
        values.put(PATIENT_ADDRESS, profile.getAddress());
        values.put(PATIENT_MOBILE, profile.getMobile());
        values.put(DOCTOR_ID, doctorId);

        long result = db.insert(TABLE_PATIENT_PROFILES, null, values);
        return result != -1;
    }

    public boolean deleteProfile(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Attempting to delete profile with ID: " + id);
        int result = db.delete(TABLE_PATIENT_PROFILES, PATIENT_ID + "=?", new String[]{String.valueOf(id)});
        Log.d(TAG, "Delete result: " + result);
        db.close();
        return result > 0;
    }

    public ArrayList<Profile> getProfilesByDoctorId(int doctorId) {
        ArrayList<Profile> profiles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PATIENT_PROFILES + " WHERE " + DOCTOR_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId)});

        if (cursor.moveToFirst()) {
            do {
                profiles.add(new Profile(
                        cursor.getInt(cursor.getColumnIndexOrThrow(PATIENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PATIENT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(PATIENT_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PATIENT_PROBLEMS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PATIENT_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PATIENT_MOBILE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return profiles;
    }

//    public void logDoctorDetailsColumns() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_DOCTOR_DETAILS + ")", null);
//        if (cursor.moveToFirst()) {
//            do {
//                String columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                Log.d(TAG, "Column in doctor_details: " + columnName);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//    }
}
