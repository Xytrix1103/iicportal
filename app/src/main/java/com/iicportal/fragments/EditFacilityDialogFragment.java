package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;

public class EditFacilityDialogFragment extends BottomSheetDialogFragment {
    String facility, newFacility;
    FirebaseDatabase database;
    DatabaseReference menuRef;
    String key;

    public EditFacilityDialogFragment() {}

    public EditFacilityDialogFragment(String key) {
        this.facility = facility;
        database = MainActivity.database;
        this.menuRef = database.getReference("facilities/facility");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_facility, container, false);

        return view;
    }

}
