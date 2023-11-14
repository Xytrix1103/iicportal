package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.Feedback;

import java.text.SimpleDateFormat;

public class FeedbackDetailsDialogFragment extends BottomSheetDialogFragment {
    private Feedback feedback;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference feedbackRef;

    public FeedbackDetailsDialogFragment() {
    }

    public FeedbackDetailsDialogFragment(Feedback feedback) {
        this.feedback = feedback;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = MainActivity.user;
        this.database = MainActivity.database;
        this.feedbackRef = database.getReference("feedback/");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_details_dialog, container, false);

        TextView feedbackDateText = view.findViewById(R.id.feedbackDialogDate);
        TextView firstNameText = view.findViewById(R.id.firstNameValue);
        TextView lastNameText = view.findViewById(R.id.lastNameValue);
        TextView emailText = view.findViewById(R.id.emailValue);
        TextView phoneText = view.findViewById(R.id.phoneValue);
        TextView messageText = view.findViewById(R.id.messageValue);
        TextView closeButton = view.findViewById(R.id.closeBtn);

        feedbackDateText.setText(new SimpleDateFormat("dd MMM yyyy").format(feedback.getTimestamp()));
        firstNameText.setText(feedback.getFirstName());
        lastNameText.setText(feedback.getLastName());
        emailText.setText(feedback.getEmail());
        phoneText.setText(feedback.getPhone());
        messageText.setText(feedback.getMessage());

        closeButton.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }
}