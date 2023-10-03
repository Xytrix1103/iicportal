package com.iicportal.activity;

import android.app.ActivityOptions;
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
            String[] items = new String[] {
                    "Home",
                    "E-Canteen",
                    "Facilities",
                    "Profile"
            };
            String destination = item.getTitle().toString();
            String current = "";

            switch (context.getClass().getSimpleName()) {
                case "MainActivity":
                    current = "Home";
                    break;
                case "ECanteenMenuActivity":
                    current = "E-Canteen";
                    break;
                case "FacilitiesActivity":
                    current = "Facilities";
                    break;
                case "ProfileActivity":
                    current = "Profile";
                    break;
            }

            Log.d("MainActivity", "Current: " + current);
            Log.d("MainActivity", "Destination: " + destination);

            int currentInd = 0;
            int destinationInd = 0;

            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(current)) {
                    currentInd = i;
                }
                if (items[i].equals(destination)) {
                    destinationInd = i;
                }
            }

            Log.d("MainActivity", "Current index: " + currentInd);
            Log.d("MainActivity", "Destination index: " + destinationInd);

            ActivityOptions options = currentInd < destinationInd ?
                    ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left)
                    : ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_left, R.anim.slide_out_right);

            if (id == R.id.home) {
                if (!current.equals("Home")) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent, options.toBundle());
                }
            } else if (id == R.id.ecanteen) {
                if (!current.equals("E-Canteen")) {
                    Intent intent = new Intent(context, ECanteenMenuActivity.class);
                    context.startActivity(intent, options.toBundle());
                }
            } else if (id == R.id.facilities) {
                if (!current.equals("Facilities")) {
                    Intent intent = new Intent(context, FacilityMenuActivity.class);
                    context.startActivity(intent, options.toBundle());
                }
            } else if (id == R.id.profile) {
                if (!current.equals("Profile")) {
                    Log.d("MainActivity", "Profile");
                }
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

    public void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
