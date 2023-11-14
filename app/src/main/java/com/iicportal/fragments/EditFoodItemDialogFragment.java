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
import com.iicportal.models.FoodItem;

public class EditFoodItemDialogFragment extends BottomSheetDialogFragment {
    FoodItem foodItem, newFoodItem;
    FirebaseDatabase database;
    DatabaseReference menuRef;
    String key;

    public EditFoodItemDialogFragment() {

    }

    public EditFoodItemDialogFragment(String key) {
        this.key = key;
        database = MainActivity.database;
        this.menuRef = database.getReference("ecanteen/fooditems/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_food_item, container, false);



        return view;
    }

}