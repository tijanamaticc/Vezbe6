package com.example.vezba2klk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openDashboard();
            return;
        }

        EditText nameInput = findViewById(R.id.inputName);
        Spinner roleSpinner = findViewById(R.id.spinnerRole);
        Button loginButton = findViewById(R.id.buttonLogin);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{UsersDbHelper.ROLE_DRIVER, UsersDbHelper.ROLE_PASSENGER, UsersDbHelper.ROLE_ADMIN});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        loginButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Unesite ime korisnika", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.login(name, role);
            openDashboard();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openDashboard();
        }
    }

    private void openDashboard() {
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}