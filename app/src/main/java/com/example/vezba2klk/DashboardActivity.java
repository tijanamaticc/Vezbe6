package com.example.vezba2klk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        TextView welcomeText = findViewById(R.id.textWelcome);
        TextView roleText = findViewById(R.id.textRole);
        Button usersButton = findViewById(R.id.buttonUsers);
        Button syncButton = findViewById(R.id.buttonSync);
        Button contactsButton = findViewById(R.id.buttonContacts);
        Button logoutButton = findViewById(R.id.buttonLogout);

        welcomeText.setText("Dobrodošli, " + sessionManager.getUserName());
        roleText.setText("Uloga: " + sessionManager.getUserRole());

        boolean isAdmin = UsersDbHelper.ROLE_ADMIN.equals(sessionManager.getUserRole());
        usersButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        findViewById(R.id.adminInfo).setVisibility(isAdmin ? View.GONE : View.VISIBLE);

        usersButton.setOnClickListener(v -> startActivity(new Intent(this, UsersActivity.class)));
        syncButton.setOnClickListener(v -> startActivity(new Intent(this, SyncSettingsActivity.class)));
        contactsButton.setOnClickListener(v -> startActivity(new Intent(this, ContactsActivity.class)));
        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

