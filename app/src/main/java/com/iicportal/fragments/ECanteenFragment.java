package com.iicportal.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.iicportal.R;

public class ECanteenFragment extends Fragment {
    boolean isEdit = false;
    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    ECanteenMenuFragment eCanteenMenuFragment;
    EditECanteenMenuFragment editECanteenMenuFragment;

    public ECanteenFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.ecanteen_fragment);
        this.openDrawerInterface = openDrawerInterface;
    }

    public ECanteenFragment() {
        super(R.layout.ecanteen_fragment);
        openDrawerInterface = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecanteen_fragment, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            Log.d("ECanteenFragment", "onViewCreated: " + savedInstanceState.getBoolean("isEdit"));
            isEdit = savedInstanceState.getBoolean("isEdit");
        } else {
            Log.d("ECanteenFragment", "onViewCreated: " + isEdit);
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
            String role = sharedPreferences.getString("role", "Student");

            if (!role.equals("Admin") && !role.equals("Vendor")) {
                isEdit = false;
            } else if (role.equals("Vendor")) {
                isEdit = true;
            }
        }

        eCanteenMenuFragment = new ECanteenMenuFragment(openDrawerInterface, getChildFragmentManager());
        editECanteenMenuFragment = new EditECanteenMenuFragment(openDrawerInterface, getChildFragmentManager());

        Log.d("ECanteenFragment", "onCreateView: " + isEdit);

        getChildFragmentManager().beginTransaction().replace(R.id.ecanteen_fragment_container, isEdit ? editECanteenMenuFragment : eCanteenMenuFragment).commit();
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        getChildFragmentManager().beginTransaction().replace(R.id.ecanteen_fragment_container, isEdit ? editECanteenMenuFragment : eCanteenMenuFragment).commit();
    }

    public boolean isEdit() {
        return isEdit;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");
        if (!role.equals("Admin") && !role.equals("Vendor")) {
            isEdit = false;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.ecanteen_fragment_container, isEdit ? editECanteenMenuFragment : eCanteenMenuFragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEdit", isEdit);
    }
}
