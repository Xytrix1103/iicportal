package com.iicportal.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.models.Feedback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedbackActivity extends AppCompatActivity {
    private TextInputEditText firstNameEdit, lastNameEdit, emailEdit, phoneEdit, messageEdit;
    private Button submitButton;
    private ImageView backButtonIcon;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference usersRef, feedbackRef;

    private static final String FEEDBACK_TAG = "FeedbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize firebase objects
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users/");
        feedbackRef = database.getReference("feedback/");

        // Set reference to views
        firstNameEdit = findViewById(R.id.firstName);
        lastNameEdit = findViewById(R.id.lastName);
        emailEdit = findViewById(R.id.email);
        phoneEdit = findViewById(R.id.phone);
        messageEdit = findViewById(R.id.message);
        submitButton = findViewById(R.id.submitBtn);
        backButtonIcon = findViewById(R.id.backBtnIcon);

        // onClick listeners
        submitButton.setOnClickListener(view -> {
            // Get inputs
            String firstName = firstNameEdit.getText().toString();
            String lastName = lastNameEdit.getText().toString();
            String email = emailEdit.getText().toString();
            String phone = phoneEdit.getText().toString();
            String message = messageEdit.getText().toString();

            // Validate input fields
            if (validateField(!firstName.isEmpty(), firstNameEdit, "First name is required") &&
                    validateField(!lastName.isEmpty(), lastNameEdit, "Last name is required") &&
                    validateField(!email.isEmpty(), emailEdit, "E-mail is required") &&
                    validateField(!message.isEmpty(), messageEdit, "Message is required") &&
                    validateField(isValidEmail(email), emailEdit, "Invalid email format") &&
                    validateField(isValidPhoneNumber(phone), phoneEdit, "Invalid phone number format")) {
                usersRef.child(user.getUid()).get().addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        // Get user's profile picture link
                        String profilePicture = userTask.getResult().child("image").getValue() != null
                                ? userTask.getResult().child("image").getValue().toString()
                                : null;

                        // Push new contact message
                        Feedback newFeedback = new Feedback(user.getUid(), firstName, lastName, firstName + " " + lastName, email, phone, message, profilePicture, false, System.currentTimeMillis());

                        feedbackRef.push().setValue(newFeedback).addOnCompleteListener(feedbackTask -> {
                            if (feedbackTask.isSuccessful()) {
                                firstNameEdit.getText().clear();
                                lastNameEdit.getText().clear();
                                emailEdit.getText().clear();
                                phoneEdit.getText().clear();
                                messageEdit.getText().clear();

                                Toast.makeText(this, "We've received your feedback, thank you for reaching out to us!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.e(FEEDBACK_TAG, "loadUser:");
                    }
                });
            }
        });

        backButtonIcon.setOnClickListener(view -> {
            // Return to previous activity
            finish();
        });
    }

    private boolean validateField(boolean validation, TextInputEditText editText, String errorMessage) {
        if (!validation) {
            editText.setError(errorMessage);
            editText.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^01[0-46-9][0-9]{7,8}$";
        Pattern pattern = Pattern.compile(phoneNumberRegex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }
}