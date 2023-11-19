package com.iicportal.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

        mAuth = MainActivity.mAuth;
        database = MainActivity.database;
        usersRef = database.getReference("users");

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
                    if (task.isSuccessful() && task.getResult() != null) {
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
                        usersRef.orderByChild("email").equalTo(username).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                if (task1.getResult().exists()) {
                                    Log.d("LoginActivity", "Password is incorrect");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setTitle("Password is incorrect");
                                    builder.setMessage("Please enter the correct password");
                                    builder.setPositiveButton("OK", (dialog, which) -> {
                                        passwordEdit.requestFocus();
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                } else {
                                    Log.d("LoginActivity", "User does not exist");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setTitle("User does not exist");
                                    builder.setMessage("Please enter the correct username");
                                    builder.setPositiveButton("OK", (dialog, which) -> {
                                        usernameEdit.requestFocus();
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            } else {
                                Log.d("LoginActivity", "Error getting user: " + task.getException());
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setTitle("Error getting user");
                                builder.setMessage("Please try again later");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    usernameEdit.requestFocus();
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });

                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
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
        if (mAuth.getCurrentUser() != null) {
            user = mAuth.getCurrentUser();
            usersRef.child(user.getUid()).child("role").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String role = task.getResult().getValue(String.class);
                    Log.d("LoginActivity", "Role: " + role);
                    sharedPreferences.edit().putString("role", role).apply();
                } else {
                    Log.d("LoginActivity", "Error getting role: " + task.getException());
                }
            });
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("LoginActivity", "User is null");
        }
    }
}
