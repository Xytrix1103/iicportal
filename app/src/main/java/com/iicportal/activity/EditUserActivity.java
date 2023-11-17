package com.iicportal.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;

public class EditUserActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String key;

    TextInputEditText name, email, password, phone;
    Spinner role;
    ImageView nameEdit, emailEdit, passwordEdit, phoneEdit, roleEdit, deleteBtn;
    TextView cancelBtn;
    ImageView backBtn;
    Button saveBtn;
    boolean[] editable = {false, false, false, false, false};
    String[] initial = {"", "", "", ""};
    Integer initialRole;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        database = MainActivity.database;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.usersRef = database.getReference("users/");
        this.key = getIntent().getStringExtra("key");

        name = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        role = findViewById(R.id.role);
        nameEdit = findViewById(R.id.editBtn);
        emailEdit = findViewById(R.id.emailEditBtn);
        passwordEdit = findViewById(R.id.passwordEditBtn);
        phoneEdit = findViewById(R.id.phoneEditBtn);
        roleEdit = findViewById(R.id.roleEditBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtnIcon);
        deleteBtn = findViewById(R.id.deleteBtn);

        deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete this user?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                usersRef.child(key).removeValue();
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        backBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            builder.setTitle("Cancel");
            builder.setMessage("Are you sure you want to cancel? Changes will be discarded");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        //disable all fields
        name.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
        phone.setEnabled(false);
        role.setEnabled(false);

        String[] roles = getResources().getStringArray(R.array.userRole);

        usersRef.child(key).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                name.setText(task.getResult().child("fullName").getValue().toString());
                initial[0] = task.getResult().child("fullName").getValue().toString();
                email.setText(task.getResult().child("email").getValue().toString());
                initial[1] = task.getResult().child("email").getValue().toString();
                password.setText(task.getResult().child("password").getValue().toString());
                initial[2] = task.getResult().child("password").getValue().toString();
                phone.setText(task.getResult().child("phoneNumber").getValue().toString());
                initial[3] = task.getResult().child("phoneNumber").getValue().toString();
                int index = 0;
                for (int i = 0; i < roles.length; i++) {
                    if (roles[i].equals(task.getResult().child("role").getValue().toString())) {
                        index = i;
                        break;
                    }
                }
                role.setSelection(index);
                initialRole = index;
            }
        });

        //add on click listener to edit buttons, to show confirmation dialog before enabling fields
        nameEdit.setOnClickListener(v -> {
            if (!editable[0]) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle("Edit Name");
                builder.setMessage("Are you sure you want to edit name?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    name.setEnabled(true);
                    editable[0] = true;
                    nameEdit.setImageResource(R.drawable.outline_cancel_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel editing name? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    name.setText(initial[0]);
                    name.setEnabled(false);
                    editable[0] = false;
                    nameEdit.setImageResource(R.drawable.baseline_edit_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });

        emailEdit.setOnClickListener(v -> {
            if (!editable[1]) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle("Edit Email");
                builder.setMessage("Are you sure you want to edit email? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    email.setEnabled(true);
                    editable[1] = true;
                    emailEdit.setImageResource(R.drawable.outline_cancel_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel editing email? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    email.setText(initial[1]);
                    email.setEnabled(false);
                    editable[1] = false;
                    emailEdit.setImageResource(R.drawable.baseline_edit_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });

        passwordEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            if (!editable[2]) {
                builder.setTitle("Edit Password");
                builder.setMessage("Are you sure you want to edit password? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    password.setEnabled(true);
                    editable[2] = true;
                    passwordEdit.setImageResource(R.drawable.outline_cancel_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel editing password? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    password.setText(initial[2]);
                    password.setEnabled(false);
                    editable[2] = false;
                    passwordEdit.setImageResource(R.drawable.baseline_edit_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            }
            builder.show();
        });

        phoneEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            if (!editable[3]) {
                builder.setTitle("Edit Phone Number");
                builder.setMessage("Are you sure you want to edit phone number?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    phone.setEnabled(true);
                    editable[3] = true;
                    phoneEdit.setImageResource(R.drawable.outline_cancel_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel editing phone number? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    phone.setText(initial[3]);
                    phone.setEnabled(false);
                    editable[3] = false;
                    phoneEdit.setImageResource(R.drawable.baseline_edit_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            }
            builder.show();
        });

        roleEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            if (!editable[4]) {
                builder.setTitle("Edit Role");
                builder.setMessage("Are you sure you want to edit role?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    role.setEnabled(true);
                    editable[4] = true;
                    roleEdit.setImageResource(R.drawable.outline_cancel_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            } else {
                builder.setTitle("Cancel");
                builder.setMessage("Are you sure you want to cancel editing role? Changes will be discarded");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    role.setSelection(initialRole);
                    role.setEnabled(false);
                    editable[4] = false;
                    roleEdit.setImageResource(R.drawable.baseline_edit_24);
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            }
            builder.show();
        });

        cancelBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
            builder.setTitle("Cancel");
            builder.setMessage("Are you sure you want to cancel? Changes will be discarded");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        saveBtn.setOnClickListener(v -> {
            //make sure value change is saved in textinputedittext without pressing enter
            name.clearFocus();
            email.clearFocus();
            password.clearFocus();
            phone.clearFocus();

            if (editable[0] || editable[1] || editable[2] || editable[3] || editable[4]) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle("Save");

                String message = "Are you sure you want to save the following changes: ";
                if (editable[0]) {
                    message += "\nName: " + name.getText().toString();
                }
                if (editable[1]) {
                    message += "\nEmail: " + email.getText().toString();
                }
                if (editable[2]) {
                    message += "\nPassword: " + password.getText().toString();
                }
                if (editable[3]) {
                    message += "\nPhone Number: " + phone.getText().toString();
                }
                if (editable[4]) {
                    message += "\nRole: " + role.getSelectedItem().toString();
                }

                builder.setMessage(message);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    if (editable[0]) {
                        usersRef.child(key).child("fullName").setValue(name.getText().toString());
                    }
                    if (editable[1]) {
                        usersRef.child(key).child("email").setValue(email.getText().toString());

                        FirebaseApp adminApp = FirebaseApp.getInstance("admin");
                        FirebaseAuth adminAuth = FirebaseAuth.getInstance(adminApp);

                        adminAuth.signInWithEmailAndPassword(initial[1], initial[2])
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getUser() != null) {
                                            FirebaseUser newUser = adminAuth.getCurrentUser();
                                            usersRef.child(newUser.getUid()).child("email").setValue(email.getText().toString());
                                            adminAuth.getCurrentUser().updateEmail(email.getText().toString());
                                            adminAuth.signOut();
                                        }
                                    }
                                });
                    }
                    if (editable[2]) {
                        usersRef.child(key).child("password").setValue(password.getText().toString());
                        FirebaseApp adminApp = FirebaseApp.getInstance("admin");
                        FirebaseAuth adminAuth = FirebaseAuth.getInstance(adminApp);

                        adminAuth.signInWithEmailAndPassword(initial[1], initial[2])
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().getUser() != null) {
                                            FirebaseUser newUser = adminAuth.getCurrentUser();
                                            usersRef.child(newUser.getUid()).child("password").setValue(password.getText().toString());
                                            adminAuth.getCurrentUser().updatePassword(password.getText().toString());
                                            adminAuth.signOut();
                                        }
                                    }
                                });
                    }
                    if (editable[3]) {
                        usersRef.child(key).child("phoneNumber").setValue(phone.getText().toString());
                    }

                    if (editable[4]) {
                        usersRef.child(key).child("role").setValue(role.getSelectedItem().toString());
                    }

                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
                builder.setTitle("Save");
                builder.setMessage("No changes detected. Are you sure you want to save?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
    }
}
