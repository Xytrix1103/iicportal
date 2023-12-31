package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.activity.MainActivity;

public class EditUserDialogFragment extends BottomSheetDialogFragment {
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String key;

    public EditUserDialogFragment(String key) {
        database = MainActivity.database;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.usersRef = database.getReference("users/");
        this.key = key;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(com.iicportal.R.layout.activity_edit_user, container, false);

        return view;
    }
}
