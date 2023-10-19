package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iicportal.R;
import com.iicportal.adaptor.OrderListStateAdapter;

public class OrderListFragment extends Fragment {
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

        ViewPager2 viewPager = view.findViewById(R.id.viewPager2);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        OrderListStateAdapter orderListStateAdapter = new OrderListStateAdapter(this, 3);
        viewPager.setAdapter(orderListStateAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Preparing");
                    break;
                case 1:
                    tab.setText("Ready");
                    break;
                case 2:
                    tab.setText("Completed");
                    break;
                default:
                    break;
            }
        }).attach();

        menuBtn.setOnClickListener(v -> {
            if (openDrawerInterface != null) {
                openDrawerInterface.openDrawer();
            }
        });
    }
}

