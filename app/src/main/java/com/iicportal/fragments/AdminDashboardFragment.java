package com.iicportal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.iicportal.R;

public class AdminDashboardFragment extends Fragment {
    OpenDrawerInterface openDrawerInterface;

    public AdminDashboardFragment(OpenDrawerInterface openDrawerInterface) {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = openDrawerInterface;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AdminDashboardFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        menuBtn.setOnClickListener(v -> openDrawerInterface.openDrawer());
        return view;
    }

    public interface OpenDrawerInterface {
        void openDrawer();
    }
}
