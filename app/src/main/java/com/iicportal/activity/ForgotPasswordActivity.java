package com.iicportal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    ActionCodeSettings actionCodeSettings;

    DatabaseReference usersRef;

    RelativeLayout backButton;
    Button submitButton;

    TextInputEditText emailEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        backButton = findViewById(R.id.backBtn);
        submitButton = findViewById(R.id.submitBtn);
        emailEdit = findViewById(R.id.email);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        submitButton.setOnClickListener(v -> {
            String email = emailEdit.getText().toString().trim();

            if (email.isEmpty()) {
                emailEdit.setError("Email is required");
                emailEdit.requestFocus();
                return;
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Success");
                    builder.setMessage("Password reset link has been sent to your email");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("Something went wrong. Please try again later");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                    builder.show();
                }
            });
        });
    }
}
