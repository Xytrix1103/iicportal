package com.iicportal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.fragments.VerticalViewFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    FrameLayout container;
    Fragment verticalViewFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = this.getSharedPreferences("com.iicportal", MODE_PRIVATE);
        verticalViewFragment = new VerticalViewFragment();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verticalViewFragment).commit();
    }

    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/role").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sharedPreferences.edit().putString("role", task.getResult().getValue().toString()).apply();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/role").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sharedPreferences.edit().putString("role", task.getResult().getValue().toString()).apply();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
