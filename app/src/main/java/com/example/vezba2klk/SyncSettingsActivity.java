package com.example.vezba2klk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SyncSettingsActivity extends AppCompatActivity {
    private SyncPrefs syncPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_settings);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        syncPrefs = new SyncPrefs(this);
        RadioGroup radioGroup = findViewById(R.id.radioGroupSync);
        TextView currentValue = findViewById(R.id.textCurrentSync);
        Button saveButton = findViewById(R.id.buttonSaveSync);
        Button backButton = findViewById(R.id.buttonBackSync);

        currentValue.setText("Trenutno podešavanje: " + syncPrefs.getReadableLabel());

        long interval = syncPrefs.getInterval();
        if (interval == SyncPrefs.SYNC_1_MIN) {
            radioGroup.check(R.id.radio1Min);
        } else if (interval == SyncPrefs.SYNC_15_MIN) {
            radioGroup.check(R.id.radio15Min);
        } else if (interval == SyncPrefs.SYNC_30_MIN) {
            radioGroup.check(R.id.radio30Min);
        } else {
            radioGroup.check(R.id.radioNever);
        }

        saveButton.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            long selectedInterval = SyncPrefs.SYNC_NEVER;
            if (checkedId == R.id.radio1Min) {
                selectedInterval = SyncPrefs.SYNC_1_MIN;
            } else if (checkedId == R.id.radio15Min) {
                selectedInterval = SyncPrefs.SYNC_15_MIN;
            } else if (checkedId == R.id.radio30Min) {
                selectedInterval = SyncPrefs.SYNC_30_MIN;
            }

            syncPrefs.setInterval(selectedInterval);
            currentValue.setText("Trenutno podešavanje: " + syncPrefs.getReadableLabel());
            startService(new Intent(this, SyncService.class));
        });

        backButton.setOnClickListener(v -> finish());
    }
}

