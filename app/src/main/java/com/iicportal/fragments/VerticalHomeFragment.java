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
import com.google.firebase.auth.FirebaseUser;
import com.iicportal.R;
import com.iicportal.activity.ContactActivity;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;

public class VerticalHomeFragment extends Fragment {
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Button logoutButton;
    Button livechatButton;
    Button contactButton;

    public VerticalHomeFragment() {
        super(R.layout.vertical_home_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vertical_home_fragment, container, false);

        mAuth = MainActivity.mAuth;
        currentUser = mAuth.getCurrentUser();

        contactButton = view.findViewById(R.id.contactBtn);
        livechatButton = view.findViewById(R.id.livechatBtn);
        logoutButton = view.findViewById(R.id.logoutBtn);

        contactButton.setOnClickListener(v -> {
            startActivity(new Intent(context, ContactActivity.class));
        });

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
