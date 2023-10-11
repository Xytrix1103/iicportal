package com.iicportal.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;

public class HorizontalViewFragment extends Fragment implements AdminDashboardFragment.OpenDrawerInterface {
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    FrameLayout container;
    NavigationView navigationView;
    Fragment AdminDashboardFragment;
    Fragment ECanteenMenuFragment;
    Fragment FacilityMenuFragment;
    Fragment UserListFragment;
    DrawerLayout drawerLayout;

    public HorizontalViewFragment() {
        super(R.layout.horizontal_view_fragment);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("HorizontalViewFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.horizontal_view_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        navigationView = view.findViewById(R.id.nav_view);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        container = view.findViewById(R.id.horizontal_fragment_container);
        AdminDashboardFragment = new AdminDashboardFragment(this);
        ECanteenMenuFragment = new ECanteenMenuFragment();
        FacilityMenuFragment = new FacilityMenuFragment();
        UserListFragment = new UserListFragment(this);
        String role = sharedPreferences.getString("role", "Admin");
        ViewGroup finalContainer = container;

        if (role.equals("Admin")) {
            setAdminView(finalContainer);
        } else if (role.equals("Vendor")) {
            setVendorView(finalContainer);
        }

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }

        return view;
    }

    private void setAdminView(ViewGroup container) {
        Menu menu = navigationView.getMenu();
        menu.clear();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();

        menu.add(Menu.NONE, 1, Menu.NONE, "Dashboard").setIcon(R.drawable.round_dashboard_24);
        menu.add(Menu.NONE, 2, Menu.NONE, "E-Canteen").setIcon(R.drawable.baseline_restaurant_24);
        menu.add(Menu.NONE, 3, Menu.NONE, "Facilities").setIcon(R.drawable.outline_videogame_asset_24);
        menu.add(Menu.NONE, 4, Menu.NONE, "Users").setIcon(R.drawable.baseline_people_outline_24);

        navigationView.setNavigationItemSelectedListener(item -> {
            container.removeAllViews();

            switch (item.getItemId()) {
                case 1:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();
                    break;
                case 2:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, ECanteenMenuFragment).commit();
                    break;
                case 3:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, FacilityMenuFragment).commit();
                    break;
                case 4:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, UserListFragment).commit();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setVendorView(ViewGroup container) {
        Menu menu = navigationView.getMenu();
        menu.clear();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();

        menu.add(Menu.NONE, 1, Menu.NONE, "Dashboard").setIcon(R.drawable.round_dashboard_24);
        menu.add(Menu.NONE, 2, Menu.NONE, "E-Canteen").setIcon(R.drawable.baseline_restaurant_24);

        navigationView.setNavigationItemSelectedListener(item -> {
            container.removeAllViews();

            switch (item.getItemId()) {
                case 1:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();
                    break;
                case 2:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, ECanteenMenuFragment).commit();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
