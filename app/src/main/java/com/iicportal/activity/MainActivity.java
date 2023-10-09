package com.iicportal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.fragments.HorizontalViewFragment;
import com.iicportal.fragments.VerticalViewFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference usersRef;
    SharedPreferences sharedPreferences;

    FrameLayout container;
    Fragment verticalViewFragment;
    Fragment horizontalViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        sharedPreferences = this.getSharedPreferences("com.iicportal", MODE_PRIVATE);
        verticalViewFragment = new VerticalViewFragment();
        horizontalViewFragment = new HorizontalViewFragment();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (user != null) {
                    //get role of user
                    String role = snapshot.child(user.getUid()).child("role").getValue().toString();
                    //store role in shared preferences
                    sharedPreferences.edit().putString("role", role).apply();
                    //if role is admin
                    if (role.equals("Admin") || role.equals("Vendor")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, horizontalViewFragment).commit();
                    } else if (role.equals("Student") || role.equals("Staff")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verticalViewFragment).commit();
                    }
                } else {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
