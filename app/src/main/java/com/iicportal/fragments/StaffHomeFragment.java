package com.iicportal.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;

public class StaffHomeFragment extends Fragment {
    private Context context;
    private FirebaseAuth mAuth;
    Button logoutButton;

    public StaffHomeFragment() {
        super(R.layout.staff_home_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_home_fragment, container, false);

        mAuth = MainActivity.mAuth;
        logoutButton = view.findViewById(R.id.logoutBtn);

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
