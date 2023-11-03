package com.iicportal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.fragments.HorizontalViewFragment;
import com.iicportal.fragments.VerticalViewFragment;

public class MainActivity extends AppCompatActivity {
    public static FirebaseApp app;
    public static FirebaseApp adminApp;
    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static FirebaseDatabase database;
    DatabaseReference usersRef;
    SharedPreferences sharedPreferences;
    Fragment verticalViewFragment;
    Fragment horizontalViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate: ");
        setContentView(R.layout.activity_main);
        app = FirebaseApp.initializeApp(this);
        if (FirebaseApp.getApps(this).size() == 1) {
            adminApp = FirebaseApp.initializeApp(this, FirebaseApp.getInstance().getOptions(), "admin");
        } else {
            adminApp = FirebaseApp.getInstance("admin");
        }
        mAuth = FirebaseAuth.getInstance(app);
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance(app);
        database.getReference().keepSynced(true);
        usersRef = database.getReference("users");
        sharedPreferences = this.getSharedPreferences("com.iicportal", MODE_PRIVATE);
        verticalViewFragment = new VerticalViewFragment();
        horizontalViewFragment = new HorizontalViewFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart: ");
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            user = mAuth.getCurrentUser();
            usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue().toString();
                        sharedPreferences.edit().putString("role", role).apply();
                        if (role.equals("Admin") || role.equals("Vendor")) {
                            Log.d("ViewFragment", "HorizontalViewFragment");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, horizontalViewFragment).commit();
                        } else if (role.equals("Student") || role.equals("Staff")) {
                            Log.d("ViewFragment", "VerticalViewFragment");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verticalViewFragment).commit();
                        }
                    } else {
                        Log.d("not found", "onDataChange: ");
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume: ");

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            user = mAuth.getCurrentUser();
            Log.d("MainActivity", "onStart: " + user.getUid());
            usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String role = snapshot.child("role").getValue().toString();
                        sharedPreferences.edit().putString("role", role).apply();
                        if (role.equals("Admin") || role.equals("Vendor")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, horizontalViewFragment).commit();
                        } else if (role.equals("Student") || role.equals("Staff")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verticalViewFragment).commit();
                        }
                    } else {
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }

    //override on save instance state to prevent app from crashing when screen is rotated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
