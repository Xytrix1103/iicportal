package com.iicportal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    Button logoutButton;
    Button menuButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutBtn);
        menuButton = findViewById(R.id.canteenBtn);

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        menuButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ECanteenMenuActivity.class));
        });
    }
}
