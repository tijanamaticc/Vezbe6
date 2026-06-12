package com.example.vezba2klk;

import android.content.Context;
import android.content.SharedPreferences;

public class SyncPrefs {
    public static final long SYNC_NEVER = -1L;
    public static final long SYNC_1_MIN = 60_000L;
    public static final long SYNC_15_MIN = 15 * 60_000L;
    public static final long SYNC_30_MIN = 30 * 60_000L;

    private static final String PREF_NAME = "sync_prefs";
    private static final String KEY_INTERVAL = "sync_interval";

    private final SharedPreferences prefs;

    public SyncPrefs(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setInterval(long intervalMillis) {
        prefs.edit().putLong(KEY_INTERVAL, intervalMillis).apply();
    }

    public long getInterval() {
        return prefs.getLong(KEY_INTERVAL, SYNC_NEVER);
    }

    public String getReadableLabel() {
        long interval = getInterval();
        if (interval == SYNC_1_MIN) {
            return "Svaki 1 minut";
        }
        if (interval == SYNC_15_MIN) {
            return "Svaki 15 minuta";
        }
        if (interval == SYNC_30_MIN) {
            return "Svaki 30 minuta";
        }
        return "Nikad";
    }
}

