package com.iicportal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText usernameEdit, passwordEdit;

    Button loginButton;

    TextView forgotPasswordButton, registerButton;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        usersRef.keepSynced(true);

        sharedPreferences = this.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);

        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginBtn);
        forgotPasswordButton = findViewById(R.id.forgotPasswordBtn);
        registerButton = findViewById(R.id.registerBtn);

        loginButton.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (username.isEmpty()) {
                usernameEdit.setError("Username is required");
                usernameEdit.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordEdit.setError("Password is required");
                passwordEdit.requestFocus();
                return;
            }

            Log.d("LoginActivity", "Username: " + username + ", Password: " + password);

            mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    Log.d("LoginActivity", "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        Log.d("LoginActivity", "signInWithEmail:success");
                        user = mAuth.getCurrentUser();

                        usersRef.child(user.getUid()).child("role").get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                String role = task1.getResult().getValue(String.class);
                                Log.d("LoginActivity", "Role: " + role);
                                sharedPreferences.edit().putString("role", role).apply();
                            } else {
                                Log.d("LoginActivity", "Error getting role: " + task1.getException());
                            }
                        });

                        reload();
                    } else {
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
