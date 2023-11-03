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

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;

public class HorizontalViewFragment extends Fragment implements AdminDashboardFragment.OpenDrawerInterface {
    private FirebaseAuth mAuth;
    FirebaseUser user;
    SharedPreferences sharedPreferences;
    FrameLayout container;
    NavigationView navigationView;
    Fragment AdminDashboardFragment;
    Fragment ECanteenFragment;
    Fragment FacilityFragment;
    Fragment UserListFragment;
    Fragment OrderListFragment;
    Fragment ChatListFragment;
    Fragment MessageListFragment;
    DrawerLayout drawerLayout;

    public HorizontalViewFragment() {
        super(R.layout.horizontal_view_fragment);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("HorizontalViewFragment", "onCreateView: ");
        return inflater.inflate(R.layout.horizontal_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        navigationView = view.findViewById(R.id.nav_view);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        container = view.findViewById(R.id.horizontal_fragment_container);
        AdminDashboardFragment = new AdminDashboardFragment(this);
        ECanteenFragment = new ECanteenFragment(this);
        FacilityFragment = new FacilityFragment(this);
        UserListFragment = new UserListFragment(this);
        OrderListFragment = new OrderListFragment(this);
        ChatListFragment = new ChatListFragment(this);
        MessageListFragment = new MessageListFragment(this);
        String role = sharedPreferences.getString("role", "");
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
    }

    private void setAdminView(ViewGroup container) {
        Menu menu = navigationView.getMenu();
        menu.clear();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();
        menu.add(Menu.NONE, 0, Menu.NONE, "Dashboard").setIcon(R.drawable.round_dashboard_24);
        menu.add(Menu.NONE, 1, Menu.NONE, "E-Canteen").setIcon(R.drawable.outline_coffee_24);
        menu.add(Menu.NONE, 2, Menu.NONE, "Facilities").setIcon(R.drawable.outline_videogame_asset_24);
        menu.add(Menu.NONE, 3, Menu.NONE, "Users").setIcon(R.drawable.baseline_people_outline_24);
        menu.add(Menu.NONE, 4, Menu.NONE, "Orders").setIcon(R.drawable.outline_food_bank_24);
        menu.add(Menu.NONE, 5, Menu.NONE, "Chats").setIcon(R.drawable.baseline_support_agent_24);
        menu.add(Menu.NONE, 6, Menu.NONE, "Contact").setIcon(R.drawable.baseline_email_24);
        menu.add(Menu.NONE, 7, Menu.NONE, "Logout").setIcon(R.drawable.baseline_logout_24);
        menu.getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.isChecked()) {
                return false;
            }

            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }

            container.removeAllViews();

            switch (item.getItemId()) {
                case 0:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();
                    break;
                case 1:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, ECanteenFragment).commit();
                    break;
                case 2:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, FacilityFragment).commit();
                    break;
                case 3:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, UserListFragment).commit();
                    break;
                case 4:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, OrderListFragment).commit();
                    break;
                case 5:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, ChatListFragment).commit();
                    break;
                case 6:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, MessageListFragment).commit();
                    break;
                case 7:
                    mAuth.signOut();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            item.setChecked(true);
            return true;
        });
    }

    private void setVendorView(ViewGroup container) {
        Menu menu = navigationView.getMenu();
        menu.clear();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();

        menu.add(Menu.NONE, 0, Menu.NONE, "Dashboard").setIcon(R.drawable.round_dashboard_24);
        menu.add(Menu.NONE, 1, Menu.NONE, "E-Canteen").setIcon(R.drawable.outline_coffee_24);
        menu.add(Menu.NONE, 2, Menu.NONE, "Orders").setIcon(R.drawable.outline_food_bank_24);
        menu.add(Menu.NONE, 3, Menu.NONE, "Logout").setIcon(R.drawable.baseline_logout_24);
        menu.getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.isChecked()) {
                return false;
            }

            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }

            container.removeAllViews();

            switch (item.getItemId()) {
                case 0:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, AdminDashboardFragment).commit();
                    break;
                case 1:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, ECanteenFragment).commit();
                    break;
                case 2:
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.horizontal_fragment_container, OrderListFragment).commit();
                    break;
                case 3:
                    mAuth.signOut();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            item.setChecked(true);
            return true;
        });
    }

    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
