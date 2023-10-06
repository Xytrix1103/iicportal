package com.iicportal.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;

public class VerticalViewFragment extends Fragment {
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    FrameLayout container;
    Fragment studentHomeFragment;
    Fragment staffHomeFragment;
    Fragment ecanteenMenuFragment;
    Fragment facilitiesMenuFragment;
    Fragment profileFragment;
    BottomNavigationView bottomNavigationView;

    public VerticalViewFragment() {
        super(R.layout.vertical_user_view);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vertical_user_view, container, false);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        container = view.findViewById(R.id.vertical_fragment_container);
        String role = sharedPreferences.getString("role", "Student");
        Menu menu = bottomNavigationView.getMenu();
        studentHomeFragment = new StudentHomeFragment();
        staffHomeFragment = new StaffHomeFragment();
        ecanteenMenuFragment = new ECanteenMenuFragment();
        facilitiesMenuFragment = new FacilityMenuFragment();
        profileFragment = new ProfileFragment();

        ViewGroup finalContainer = container;

        if (role.equals("Student")) {
            setStudentView(finalContainer);
        } else if (role.equals("Staff")) {
            setStaffView(finalContainer);
        }

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }

        return view;
    }

    private void setStaffView(ViewGroup container) {
        Menu menu = bottomNavigationView.getMenu();
        menu.clear();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, staffHomeFragment).commit();
        menu.add(Menu.NONE, 0, 0, "Home").setIcon(R.drawable.outline_home_24);
        menu.add(Menu.NONE, 1, 1, "E-Canteen").setIcon(R.drawable.baseline_restaurant_24);
        menu.add(Menu.NONE, 2, 2, "Profile").setIcon(R.drawable.baseline_person_24);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            container.removeAllViews();

            switch (item.getItemId()) {
                case 0:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, staffHomeFragment).commit();
                    return true;
                case 1:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, ecanteenMenuFragment).commit();
                    return true;
                case 2:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, profileFragment).commit();
                    return true;
            }
            return false;
        });
    }

    public void setStudentView(ViewGroup container) {
        Menu menu = bottomNavigationView.getMenu();
        menu.clear();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, studentHomeFragment).commit();
        menu.add(Menu.NONE, 0, 0, "Home").setIcon(R.drawable.outline_home_24);
        menu.add(Menu.NONE, 1, 1, "E-Canteen").setIcon(R.drawable.baseline_restaurant_24);
        menu.add(Menu.NONE, 2, 2, "Facilities").setIcon(R.drawable.outline_videogame_asset_24);
        menu.add(Menu.NONE, 3, 3, "Profile").setIcon(R.drawable.baseline_person_24);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            container.removeAllViews();

            switch (item.getItemId()) {
                case 0:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, studentHomeFragment).commit();
                    return true;
                case 1:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, ecanteenMenuFragment).commit();
                    return true;
                case 2:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, facilitiesMenuFragment).commit();
                    return true;
                case 3:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, profileFragment).commit();
                    return true;
            }
            return false;
        });
    }

    public void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }
    }
}
