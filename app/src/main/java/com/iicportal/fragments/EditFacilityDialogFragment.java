package com.iicportal.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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

//        TextInputEditText facilityEditText = view.findViewById(R.id.facility);
//        facilityEditText.setText(facility);
//
//        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
//        TextView saveBtn = view.findViewById(R.id.saveBtn);
//        ImageView deleteBtn = view.findViewById(R.id.deleteBtn);
//
//        cancelBtn.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("Cancel");
//            builder.setMessage("Are you sure you want to cancel?");
//            builder.setPositiveButton("Yes", (dialog, which) -> {
//                dialog.dismiss();
//                dismiss();
//            });
//            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        });
//
//        saveBtn.setOnClickListener(v -> {
//            newFacility = facilityEditText.getText().toString();
//            if (newFacility.isEmpty()) {
//                facilityEditText.setError("Facility cannot be empty");
//                facilityEditText.requestFocus();
//            } else {
//                database.getReference("facilities/" + facility).removeValue();
//                database.getReference("facilities/" + newFacility).child("facility").setValue(newFacility);
//            }
//            dismiss();
//        });
//
//        deleteBtn.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("Delete");
//            builder.setMessage("Are you sure you want to delete this facility?");
//            builder.setPositiveButton("Yes", (dialog, which) -> {
//                database.getReference("facilities/" + facility).removeValue();
//                database.getReference("facilities/" + newFacility).child("facility").setValue(newFacility);
//                dialog.dismiss();
//                dismiss();
//            });
//            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        });

        return view;
    }

    private void updateImage(Uri uri, ImageView image) {
        Glide.with(requireContext()).load(uri).into(image);
    }

}
