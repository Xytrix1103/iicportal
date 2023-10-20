package com.iicportal.adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.iicportal.fragments.StatusOrderListFragment;

public class OrderListStateAdapter extends FragmentStateAdapter {
    int tabCount;
    Fragment statusOrderListFragment;

    public OrderListStateAdapter(FragmentManager fragment, Lifecycle lifecycle, int tabCount) {
        super(fragment, lifecycle);
        this.tabCount = tabCount;
        this.statusOrderListFragment = null;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new StatusOrderListFragment();
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}
