package com.iicportal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText fullNameEdit, phoneNumberEdit, emailEdit, passwordEdit, confirmPasswordEdit;

    Button registerButton;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    String fullName;
    String phoneNumber;
    String email;
    String password;
    String confirmPassword;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        fullNameEdit = findViewById(R.id.fullName);
        phoneNumberEdit = findViewById(R.id.phoneNumber);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        confirmPasswordEdit = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerBtn);

        String TAG = "RegisterActivity";

        registerButton.setOnClickListener(v -> {
            fullName = fullNameEdit.getText().toString();
            phoneNumber = phoneNumberEdit.getText().toString();
            email = emailEdit.getText().toString();
            password = passwordEdit.getText().toString();
            confirmPassword = confirmPasswordEdit.getText().toString();

            if (validateFields(fullName, phoneNumber, email, password, confirmPassword)) {
                Query query = usersRef.orderByChild("email").equalTo(email);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            emailEdit.setError("Email already exists");
                            emailEdit.requestFocus();
                        } else {
                            if (createFirebaseUser(fullName, phoneNumber, email, password)) {
                                Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User creation failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean createFirebaseUser(String fullName, String phoneNumber, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("RegisterActivity", "createUserWithEmail:success");
                        currentUser = mAuth.getCurrentUser();
                        usersRef.child(currentUser.getUid()).setValue(new User(fullName, phoneNumber, email, password));
                        updateUI(currentUser);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });

        return true;
    }

    private boolean validateFields(String fullName, String phoneNumber, String email, String password, String confirmPassword) {
//        if (!validateField(fullName, fullNameEdit, "Full name is required")) {
//            return false;
//        }
//
//        if (!validateField(phoneNumber, phoneNumberEdit, "Phone number is required")) {
//            return false;
//        }
//
//        if (!validateField(email, emailEdit, "Email is required")) {
//            return false;
//        }
//
//        if (!validateField(password, passwordEdit, "Password is required")) {
//            return false;
//        }
//
//        if (!validateField(confirmPassword, confirmPasswordEdit, "Confirm password is required")) {
//            return false;
//        }
//
//        if (!isValidEmail(email)) {
//            emailEdit.setError("Invalid email");
//            emailEdit.requestFocus();
//            return false;
//        }
//
//        if (!isValidPhoneNumber(phoneNumber)) {
//            phoneNumberEdit.setError("Invalid phone number");
//            phoneNumberEdit.requestFocus();
//            return false;
//        }
//
//        if (!isValidPassword(password)) {
//            passwordEdit.setError("Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character");
//            passwordEdit.requestFocus();
//        }
//
//        if (!isValidEmail(email)) {
//            emailEdit.setError("Invalid email");
//            emailEdit.requestFocus();
//            return false;
//        }
//
//        if (!isValidPhoneNumber(phoneNumber)) {
//            phoneNumberEdit.setError("Invalid phone number");
//            phoneNumberEdit.requestFocus();
//            return false;
//        }
//
//        if (!isValidPassword(password)) {
//            passwordEdit.setError("Invalid password");
//            passwordEdit.requestFocus();
//            return false;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            confirmPasswordEdit.setError("Password and confirm password must be the same");
//            confirmPasswordEdit.requestFocus();
//            return false;
//        }
//
//        return true;
        //shortened version of above code
        return validateField(fullName, fullNameEdit, "Full name is required") &&
                validateField(phoneNumber, phoneNumberEdit, "Phone number is required") &&
                validateField(email, emailEdit, "Email is required") &&
                validateField(password, passwordEdit, "Password is required") &&
                validateField(confirmPassword, confirmPasswordEdit, "Confirm password is required") &&
                isValidEmail(email) &&
                isValidPhoneNumber(phoneNumber) &&
                isValidPassword(password) &&
                password.equals(confirmPassword);
    }

    private boolean validateField(String field, TextInputEditText editText, String errorMessage) {
        if (field.isEmpty()) {
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

    private boolean isValidPassword(String password) {
//        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        // Password must be at least 8 characters long
        String passwordRegex = "^(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}