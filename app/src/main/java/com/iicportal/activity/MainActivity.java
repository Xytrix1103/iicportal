package com.iicportal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    Button logoutButton;
    Button menuButton;
    BottomNavigationView bottomNavigationView;

    public static NavigationBarView.OnItemSelectedListener getOnItemSelectedListener(Context context) {
        return item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                context.startActivity(new Intent(context, MainActivity.class));
                return true;
            } else if (id == R.id.ecanteen) {
                context.startActivity(new Intent(context, ECanteenMenuActivity.class));
                return true;
            } else if (id == R.id.facilities) {
                context.startActivity(new Intent(context, FacilityMenuActivity.class));
                return true;
            } else if (id == R.id.profile) {
                Log.d("MainActivity", "Profile");
                return true;
            }

            return false;
        };
    }

    public static NavigationBarView.OnItemReselectedListener getOnItemReselectedListener(Context context) {
        return item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                Log.d("MainActivity", "Home");
            } else if (id == R.id.ecanteen) {
                Log.d("MainActivity", "ECanteen");
            } else if (id == R.id.facilities) {
                Log.d("MainActivity", "Facilities");
            } else if (id == R.id.profile) {
                Log.d("MainActivity", "Profile");
            }
        };
    }

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
        bottomNavigationView.setOnItemSelectedListener(getOnItemSelectedListener(this));
        bottomNavigationView.setOnItemReselectedListener(getOnItemReselectedListener(this));
    }
}
