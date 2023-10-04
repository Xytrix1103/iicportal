package com.iicportal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    FrameLayout container;
    Fragment ecanteenMenuFragment;
    Fragment studentHomeFragment;
    Fragment facilitiesMenuFragment;
    Fragment profileFragment;
    BottomNavigationView bottomNavigationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ecanteenMenuFragment = new ECanteenMenuFragment();
        studentHomeFragment = new StudentHomeFragment();
        facilitiesMenuFragment = new FacilityMenuFragment();
        profileFragment = new ProfileFragment();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        container = findViewById(R.id.fragment_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, studentHomeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            container.removeAllViews();
            if (id == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, studentHomeFragment).commit();
                return true;
            } else if (id == R.id.ecanteen) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ecanteenMenuFragment).commit();
                return true;
            } else if (id == R.id.facilities) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, facilitiesMenuFragment).commit();
                return true;
            } else if (id == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                return true;
            }
            return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
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
        });
    }

    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
