package com.example.vezba2klk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
    private static final String TAG = "SyncService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SyncPrefs syncPrefs = new SyncPrefs(this);
        long interval = syncPrefs.getInterval();
        Log.d(TAG, "Sinhronizacija pokrenuta. Interval = " + interval + " ms");
        if (interval == SyncPrefs.SYNC_NEVER) {
            Log.d(TAG, "Sinhronizacija je isključena u SharedPreferences.");
        } else {
            Log.d(TAG, "Podešen period za sinhronizaciju: " + syncPrefs.getReadableLabel());
        }
        stopSelf(startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

