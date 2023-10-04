package com.iicportal.activity;

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

public class StudentHomeFragment extends Fragment {
    private Context context;
    private FirebaseAuth mAuth;
    Button logoutButton;
    Button menuButton;

    public StudentHomeFragment() {
        super(R.layout.student_home_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_home_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = view.findViewById(R.id.logoutBtn);

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
