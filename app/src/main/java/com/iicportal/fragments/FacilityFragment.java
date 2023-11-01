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

public class FacilityFragment extends Fragment{
    boolean isEdit = false;
    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FacilityMenuFragment facilityMenuFragment;
    EditFacilityMenuFragment editFacilityMenuFragment;

    public FacilityFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.facility_fragment);
        this.openDrawerInterface = openDrawerInterface;
    }

    public FacilityFragment() {
        super(R.layout.facility_fragment);
        openDrawerInterface = null;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facility_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            Log.d("FacilityFragment", "onViewCreated: " + savedInstanceState.getBoolean("isEdit"));
            isEdit = savedInstanceState.getBoolean("isEdit");
        } else {
            Log.d("FacilityFragment", "onViewCreated: " + isEdit);
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
            String role = sharedPreferences.getString("role", "Student");

            if (!role.equals("Admin") && !role.equals("Vendor")) {
                isEdit = false;
            } else if (role.equals("Vendor")) {
                isEdit = true;
            }
        }

        facilityMenuFragment = new FacilityMenuFragment(openDrawerInterface, getChildFragmentManager());
        editFacilityMenuFragment = new EditFacilityMenuFragment(openDrawerInterface, getChildFragmentManager());

        Log.d("FacilityFragment", "onCreateView: " + isEdit);

        getChildFragmentManager().beginTransaction().replace(R.id.facility_fragment_container, isEdit ? editFacilityMenuFragment : facilityMenuFragment).commit();
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        getChildFragmentManager().beginTransaction().replace(R.id.facility_fragment_container, isEdit ? editFacilityMenuFragment : facilityMenuFragment).commit();
    }

    public boolean isEdit() {return isEdit;}

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");
        if (!role.equals("Admin") && !role.equals("Vendor")) {
            isEdit = false;
        }
        getChildFragmentManager().beginTransaction().replace(R.id.facility_fragment_container, isEdit ? editFacilityMenuFragment : facilityMenuFragment).commit();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEdit", isEdit);
    }
}
