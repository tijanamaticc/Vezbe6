package com.example.vezba2klk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UsersDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "vezba2klk_users.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "korisnici";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "ime";
    public static final String COL_EMAIL = "email";
    public static final String COL_ROLE = "uloga";

    public static final String ROLE_DRIVER = "vozač";
    public static final String ROLE_PASSENGER = "putnik";
    public static final String ROLE_ADMIN = "administrator";

    public UsersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_ROLE + " TEXT NOT NULL)");

        insertSeedUser(db, "Ana Admin", "ana.admin@example.com", ROLE_ADMIN);
        insertSeedUser(db, "Marko Vozac", "marko.vozac@example.com", ROLE_DRIVER);
        insertSeedUser(db, "Jelena Putnik", "jelena.putnik@example.com", ROLE_PASSENGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertSeedUser(SQLiteDatabase db, String name, String email, String role) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_ROLE, role);
        db.insert(TABLE_USERS, null, values);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_ROLE, user.getRole());
        return db.insert(TABLE_USERS, null, values);
    }

    public int updateUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_ROLE, user.getRole());
        return db.update(TABLE_USERS, values, COL_ID + "=?", new String[]{String.valueOf(user.getId())});
    }

    public int deleteUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_USERS, COL_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COL_NAME + " ASC");
        try {
            while (cursor.moveToNext()) {
                users.add(cursorToUser(cursor));
            }
        } finally {
            cursor.close();
        }
        return users;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)));
        return user;
    }
}

