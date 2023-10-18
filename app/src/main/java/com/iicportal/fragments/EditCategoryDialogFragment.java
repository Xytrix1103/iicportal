package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;

public class EditCategoryDialogFragment extends BottomSheetDialogFragment {
    //can be used to edit the Category text, delete category (and all its items)
    String category, newCategory;
    public EditCategoryDialogFragment() {
    }

    public EditCategoryDialogFragment(String category) {
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_category_dialog, container, false);

        TextInputEditText categoryEditText = view.findViewById(R.id.category);
        categoryEditText.setText(category);

        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
        TextView saveBtn = view.findViewById(R.id.saveBtn);
        ImageView deleteBtn = view.findViewById(R.id.deleteBtn);

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
            newCategory = categoryEditText.getText().toString();
            if (newCategory.isEmpty()) {
                categoryEditText.setError("Category cannot be empty");
                categoryEditText.requestFocus();
            } else {
                //remove old category and add new category
                FirebaseDatabase.getInstance().getReference("ecanteen/categories/" + category).removeValue();
                FirebaseDatabase.getInstance().getReference("ecanteen/categories/" + newCategory).child("category").setValue(newCategory);
                FirebaseDatabase.getInstance().getReference("ecanteen/fooditems").orderByChild("category").equalTo(category).get().addOnSuccessListener(dataSnapshot -> {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.child("category").getRef().setValue(newCategory);
                    }
                });
            }
            dismiss();
        });

        deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete this category?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                FirebaseDatabase.getInstance().getReference("ecanteen/categories/" + category).removeValue();
                FirebaseDatabase.getInstance().getReference("ecanteen/fooditems").orderByChild("category").equalTo(category).getRef().removeValue();
                dialog.dismiss();
                dismiss();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        return view;
    }
}
