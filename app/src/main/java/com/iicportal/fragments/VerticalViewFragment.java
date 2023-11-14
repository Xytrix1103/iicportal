package com.iicportal.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;

public class VerticalViewFragment extends Fragment {
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    FrameLayout container;
    Fragment verticalHomeFragment;
    Fragment staffHomeFragment;
    Fragment ecanteenFragment;
    Fragment facilitiesMenuFragment;
    Fragment profileFragment;
    Fragment chatListFragment;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;

    public VerticalViewFragment() {
        super(R.layout.vertical_view_fragment);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vertical_view_fragment, container, false);
        mAuth = MainActivity.mAuth;
        sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        container = view.findViewById(R.id.vertical_fragment_container);
        String role = sharedPreferences.getString("role", "Student");
        verticalHomeFragment = new VerticalHomeFragment();
        ecanteenFragment = new ECanteenFragment();
        facilitiesMenuFragment = new FacilityMenuFragment();
        profileFragment = new ProfileFragment();
        chatListFragment = new ChatListFragment();
        fab = view.findViewById(R.id.fab);

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, verticalHomeFragment).commit();

        fab.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, verticalHomeFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.fab_item);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.ecanteen) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, ecanteenFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.facilities) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, facilitiesMenuFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.chat) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, chatListFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.profile) {
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, profileFragment).commit();
                return true;
            }
            return false;
        });

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }

        return view;
    }

    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }
    }

    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }
    }
}
