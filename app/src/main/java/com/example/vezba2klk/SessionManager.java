package com.example.vezba2klk;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "session_prefs";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_NAME = "logged_name";
    private static final String KEY_ROLE = "logged_role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void login(String name, String role) {
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_NAME, name)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_ROLE, UsersDbHelper.ROLE_PASSENGER);
    }
}

