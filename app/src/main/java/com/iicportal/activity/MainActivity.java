package com.iicportal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    Button logoutButton;
    Button menuButton;
    BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logoutBtn);
        menuButton = findViewById(R.id.canteenBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

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
            finish();
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.ecanteen) {
                startActivity(new Intent(this, ECanteenMenuActivity.class));
                finish();
                return true;
            } else if (id == R.id.facilities) {
                Log.d("MainActivity", "Facilities");
                return true;
            } else if (id == R.id.profile) {
                Log.d("MainActivity", "Profile");
                return true;
            }

            return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            Log.d("MainActivity", "Reselected");
        });
    }
}
