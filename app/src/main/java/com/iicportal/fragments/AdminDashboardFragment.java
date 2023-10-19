package com.iicportal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;

public class AdminDashboardFragment extends Fragment {
    OpenDrawerInterface openDrawerInterface;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public AdminDashboardFragment() {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = null;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public AdminDashboardFragment(OpenDrawerInterface openDrawerInterface) {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = openDrawerInterface;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AdminDashboardFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
        menuBtn.setOnClickListener(v -> openDrawerInterface.openDrawer());
        return view;
    }

    public interface OpenDrawerInterface {
        void openDrawer();
    }
}
