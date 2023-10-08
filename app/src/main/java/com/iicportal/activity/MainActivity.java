package com.iicportal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.fragments.HorizontalViewFragment;
import com.iicportal.fragments.VerticalViewFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    FrameLayout container;
    Fragment verticalViewFragment;
    Fragment horizontalViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences("com.iicportal", MODE_PRIVATE);
        verticalViewFragment = new VerticalViewFragment();
        horizontalViewFragment = new HorizontalViewFragment();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sharedPreferences.edit().putString("role", snapshot.getValue().toString()).apply();
                if (snapshot.getValue().toString().equals("Student") || snapshot.getValue().toString().equals("Staff")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verticalViewFragment).commit();
                } else if (snapshot.getValue().toString().equals("Admin") || snapshot.getValue().toString().equals("Vendor")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, horizontalViewFragment).commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
