package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.iicportal.R;

public class OrderListFragment extends Fragment {
    StatusOrderListFragment fragment = new StatusOrderListFragment();

    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    public OrderListFragment() {
        super(R.layout.order_list_fragment);
        openDrawerInterface = null;
    }

    public OrderListFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.order_list_fragment);
        this.openDrawerInterface = openDrawerInterface;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_list_fragment, container, false);
    }

    public void onViewCreated(android.view.View view, android.os.Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout frameLayout = view.findViewById(R.id.orderListFragmentContainer);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);

        getChildFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).commit();

        tabLayout.addTab(tabLayout.newTab().setText("Preparing"));
        tabLayout.addTab(tabLayout.newTab().setText("Ready"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                fragment.setStatus(tab.getPosition());
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        tabLayout.getTabAt(0).select();

        menuBtn.setOnClickListener(v -> {
            if (openDrawerInterface != null) {
                openDrawerInterface.openDrawer();
            }
        });
    }
}

