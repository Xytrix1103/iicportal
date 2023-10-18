package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;

public class AddCategoryDialogFragment extends BottomSheetDialogFragment {
    String category;

    public AddCategoryDialogFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_category_dialog, container, false);

        TextInputEditText categoryEditText = view.findViewById(R.id.category);

        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
        TextView saveBtn = view.findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Cancel");
            builder.setMessage("Are you sure you want to cancel?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                dismiss();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        saveBtn.setOnClickListener(v -> {
            category = categoryEditText.getText().toString();
            if (category.isEmpty()) {
                categoryEditText.setError("Category cannot be empty");
                categoryEditText.requestFocus();
            } else {
                FirebaseDatabase.getInstance().getReference("ecanteen/categories/" + category).child("category").setValue(category);
            }
            dismiss();
        });

        return view;
    }
}
