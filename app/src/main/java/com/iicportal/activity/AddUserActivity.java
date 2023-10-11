package com.iicportal.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
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

public class AddUserActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser user;
    FirebaseAuth mAuth;

    FirebaseApp adminApp;
    FirebaseAuth adminAuth;

    TextInputEditText fullNameEdit, phoneNumberEdit, emailEdit, passwordEdit;
    Spinner roleEdit;
    TextView cancelBtn;
    Button saveBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        this.database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.usersRef = database.getReference("users/");

        fullNameEdit = findViewById(R.id.fullName);
        phoneNumberEdit = findViewById(R.id.phone);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        roleEdit = findViewById(R.id.role);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(v -> {
            Log.i("AddUserActivity", "Save button clicked");
            String fullName = fullNameEdit.getText().toString();
            String phone = phoneNumberEdit.getText().toString();
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            String role = roleEdit.getSelectedItem().toString();
            Log.i("Values", fullName + " " + phone + " " + email + " " + password + " " + role);

            //make sure all changes are registered by the edit text
            fullNameEdit.clearFocus();
            phoneNumberEdit.clearFocus();
            emailEdit.clearFocus();
            passwordEdit.clearFocus();

            if (validateFields(fullName, phone, email, password)) {
                Log.i("AddUserActivity", "Fields are valid");
                Query query = usersRef.orderByChild("email").equalTo(email);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("AddUserActivity", "onDataChange");
                        if (dataSnapshot.getChildrenCount() == 0) {
                            Log.i("AddUserActivity", "User does not exist");
                            createFirebaseUser(fullName, phone, email, password, role);
                            Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddUserActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("AddUserActivity", "onCancelled", databaseError.toException());
                    }
                });
            } else {
                Log.i("AddUserActivity", "Fields are invalid");
                Toast.makeText(AddUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFields(String fullName, String phoneNumber, String email, String password) {
        return validateField(fullName, fullNameEdit, "Full name is required") &&
                validateField(phoneNumber, phoneNumberEdit, "Phone number is required") &&
                validateField(email, emailEdit, "Email is required") &&
                validateField(password, passwordEdit, "Password is required") &&
                isValidEmail(email) &&
                isValidPhoneNumber(phoneNumber) &&
                isValidPassword(password);
    }

    private boolean createFirebaseUser(String fullName, String phoneNumber, String email, String password, String role) {
        usersRef.push().setValue(new User(fullName, phoneNumber, email, role, password));

        return true;
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
                "(?:student.newinti.edu.my|newinti.edu.my)$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return roleEdit.getSelectedItem().toString().equals("Vendor") || matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^01[1-46-9][0-9]{7,8}$";
        Pattern pattern = Pattern.compile(phoneNumberRegex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}