package com.example.vezba2klk;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private UsersDbHelper dbHelper;
    private ArrayAdapter<User> adapter;
    private final List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn() || !UsersDbHelper.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Pristup CRUD ekranu ima samo administrator.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new UsersDbHelper(this);
        ListView listView = findViewById(R.id.listUsers);
        Button addButton = findViewById(R.id.buttonAddUser);
        Button backButton = findViewById(R.id.buttonBackUsers);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, users) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                android.widget.TextView text1 = view.findViewById(android.R.id.text1);
                android.widget.TextView text2 = view.findViewById(android.R.id.text2);
                User user = getItem(position);
                if (user != null) {
                    text1.setText(user.getName() + " (" + user.getRole() + ")");
                    text2.setText(user.getEmail());
                }
                return view;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> showUserDialog(users.get(position)));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(users.get(position));
            return true;
        });

        addButton.setOnClickListener(v -> showUserDialog(null));
        backButton.setOnClickListener(v -> finish());

        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        users.addAll(dbHelper.getAllUsers());
        adapter.notifyDataSetChanged();
    }

    private void showUserDialog(User user) {
        View formView = LayoutInflater.from(this).inflate(R.layout.dialog_user_form, null, false);
        EditText nameInput = formView.findViewById(R.id.inputUserName);
        EditText emailInput = formView.findViewById(R.id.inputUserEmail);
        Spinner roleSpinner = formView.findViewById(R.id.spinnerUserRole);

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{UsersDbHelper.ROLE_DRIVER, UsersDbHelper.ROLE_PASSENGER, UsersDbHelper.ROLE_ADMIN});
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        if (user != null) {
            nameInput.setText(user.getName());
            emailInput.setText(user.getEmail());
            int roleIndex = roleAdapter.getPosition(user.getRole());
            if (roleIndex >= 0) {
                roleSpinner.setSelection(roleIndex);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(user == null ? "Dodaj korisnika" : "Izmeni korisnika")
                .setView(formView)
                .setPositiveButton("Sačuvaj", null)
                .setNegativeButton("Otkaži", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Unesite ime i email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null) {
                long result = dbHelper.insertUser(new User(name, email, role));
                Toast.makeText(this, result > 0 ? "Korisnik dodat" : "Greška pri unosu", Toast.LENGTH_SHORT).show();
            } else {
                user.setName(name);
                user.setEmail(email);
                user.setRole(role);
                int result = dbHelper.updateUser(user);
                Toast.makeText(this, result > 0 ? "Korisnik izmenjen" : "Greška pri izmeni", Toast.LENGTH_SHORT).show();
            }
            loadUsers();
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void confirmDelete(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Brisanje korisnika")
                .setMessage("Da li želite da obrišete " + user.getName() + "?")
                .setPositiveButton("Obriši", (dialog, which) -> {
                    int result = dbHelper.deleteUser(user.getId());
                    Toast.makeText(this, result > 0 ? "Korisnik obrisan" : "Greška pri brisanju", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .setNegativeButton("Otkaži", null)
                .show();
    }
}

