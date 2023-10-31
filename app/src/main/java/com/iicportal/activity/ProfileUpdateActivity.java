package com.iicportal.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iicportal.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;

import javax.xml.transform.Result;

public class ProfileUpdateActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference usersRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    Uri profileUri;
    String key;

    TextInputEditText name, email, password, phone;
    ImageView nameEdit, emailEdit, passwordEdit, phoneEdit;
    ImageView picture;
    TextView cancelBtn;
    ImageView backBtn;
    ImageView addImageBtn, editImageBtn;
    Button saveBtn;
    boolean[] editable = {false, false, false, false, false};
    String[] initial = {"", "", "", ""};
    String fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        database = MainActivity.database;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.usersRef = database.getReference("users/");
        this.key = getIntent().getStringExtra("userID");
        Context context = this;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Activity activity = this;
        name = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        picture = findViewById(R.id.profileImage);
        nameEdit = findViewById(R.id.editBtn);
        emailEdit = findViewById(R.id.emailEditBtn);
        passwordEdit = findViewById(R.id.passwordEditBtn);
        phoneEdit = findViewById(R.id.phoneEditBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtnIcon);
        addImageBtn = findViewById(R.id.addImageBtn);
        editImageBtn = findViewById(R.id.editImageBtn);


        database.getReference("users/"+user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pictureURL = dataSnapshot.child("image").getValue(String.class);
                if (pictureURL != null && !pictureURL.equals("")){
                    if(!activity.isFinishing()) {
                        Glide.with(context).load(pictureURL).into(picture);
                    }
                    addImageBtn.setVisibility(View.GONE);
                    editImageBtn.setVisibility(View.VISIBLE);
                } else {
                    addImageBtn.setVisibility(View.VISIBLE);
                    editImageBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        profileUri = uri;
                        fileName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
                        Glide.with(context).load(uri).into(picture);
                        addImageBtn.setVisibility(View.GONE);
                        editImageBtn.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        PopupMenu popupMenu = new PopupMenu(this, editImageBtn);
        popupMenu.getMenuInflater().inflate(R.menu.food_item_image_menu, popupMenu.getMenu());

        editImageBtn.setOnClickListener(v -> {
            popupMenu.show();
        });

        addImageBtn.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            editable [4] = true;
        });

        popupMenu.setOnMenuItemClickListener(item -> {
            final int edit = R.id.edit_food_item_image;
            final int remove = R.id.remove_food_item_image;

            if (item.getItemId() == edit) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
                editImageBtn.setVisibility(View.VISIBLE);
                addImageBtn.setVisibility(View.GONE);
                editable [4] = true;
            } else if (item.getItemId() == remove) {
                profileUri = null;
                fileName = null;
                picture.setImageResource(R.color.grey_300);
                editImageBtn.setVisibility(View.GONE);
                addImageBtn.setVisibility(View.VISIBLE);
                editable [4] = true;
            } else {
                Log.d("ProfileUpdateActivity", "Error getting profile picture");
                editable [4] = false;
            }

            return true;
        });

        backBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
        picture.setEnabled(false);

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
            }
        });

        //add on click listener to edit buttons, to show confirmation dialog before enabling fields
        nameEdit.setOnClickListener(v -> {
            if (!editable[0]) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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

        cancelBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
            picture.clearFocus();
//            if (profileUri == null) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Error");
//                builder.setMessage("Please select an image");
//                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
            if (editable[0] || editable[1] || editable[2] || editable[3] || editable[4]) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
                }if (editable[4]){
                    message += "\nProfile Picture: " + picture.getDrawable().toString();
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
                        // Ask Shu Yi tmr about this problem of not being able to set the image to what you need
//                        if (profileUri == null){
//                            picture.setImageResource(R.drawable.baseline_person_24);
//                            String fileName = profileUri.getPath().substring(profileUri.getPath().lastIndexOf('/') + 1);
//                            Log.d("ProfileUpdateActivity", "fileName: " + fileName);
//                            StorageReference newImageRef = storage.getReference("profile_picture/" + fileName);
//                            newImageRef.putFile(profileUri).addOnSuccessListener(taskSnapshot -> {
//                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
//                                    usersRef = database.getReference("users/" + user.getUid() + "/image");
//                                    usersRef.setValue(uri.toString());
//                                    finish();
//                                });
//                            });
//                        } else {
                        Log.d("Profile URI", String.valueOf(profileUri));
                        if (profileUri == null) {
                            usersRef = database.getReference("users/" + user.getUid() + "/image");
                            usersRef.setValue(null);
                        } else {
                            String fileName = profileUri.getPath().substring(profileUri.getPath().lastIndexOf('/') + 1);
                            Log.d("ProfileUpdateActivity", "fileName: " + fileName);
                            StorageReference newImageRef = storage.getReference("profile_picture/" + fileName);
                            newImageRef.putFile(profileUri).addOnSuccessListener(taskSnapshot -> {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                    usersRef = database.getReference("users/" + user.getUid() + "/image");
                                    usersRef.setValue(uri.toString());
                                    finish();
                                });
                            });
                        }
                     }
                    finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdateActivity.this);
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
