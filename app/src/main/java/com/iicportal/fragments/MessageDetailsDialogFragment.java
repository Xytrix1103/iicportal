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
import com.iicportal.models.Message;

import java.text.SimpleDateFormat;

public class MessageDetailsDialogFragment extends BottomSheetDialogFragment {
    private Message message;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference messagesRef;

    public MessageDetailsDialogFragment() {
    }

    public MessageDetailsDialogFragment(Message message) {
        this.message = message;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = MainActivity.user;
        this.database = MainActivity.database;
        this.messagesRef = database.getReference("message/");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_details_dialog, container, false);

        TextView messageDateText = view.findViewById(R.id.messageDetailsDate);
        TextView firstNameText = view.findViewById(R.id.firstNameValue);
        TextView lastNameText = view.findViewById(R.id.lastNameValue);
        TextView emailText = view.findViewById(R.id.emailValue);
        TextView phoneText = view.findViewById(R.id.phoneValue);
        TextView messageText = view.findViewById(R.id.messageValue);
        TextView closeButton = view.findViewById(R.id.closeBtn);

        messageDateText.setText(new SimpleDateFormat("dd MMM yyyy").format(message.getTimestamp()));
        firstNameText.setText(message.getFirstName());
        lastNameText.setText(message.getLastName());
        emailText.setText(message.getEmail());
        phoneText.setText(message.getPhone());
        messageText.setText(message.getMessage());

        closeButton.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }
}