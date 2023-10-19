package com.iicportal.adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.iicportal.fragments.StatusOrderListFragment;

public class OrderListStateAdapter extends FragmentStateAdapter {
    int tabCount;

    public OrderListStateAdapter(Fragment fragment, int tabCount) {
        super(fragment);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new StatusOrderListFragment(position);
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}
