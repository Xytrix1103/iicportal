package com.iicportal.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.activity.BookingHistoryActivity;
import com.iicportal.activity.ContactActivity;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.activity.OrderHistoryActivity;
import com.iicportal.activity.ProfileUpdateActivity;

public class ProfileFragment extends Fragment {
    private Context context;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser user;
    SharedPreferences sharedPreferences;
    TextView name, studentID;
    ImageView barcode, pfp;
    ImageView logoutButtonIcon, profileUpdateIcon, contactIcon;
    String[] initial = {"", "", "", ""};
    public ProfileFragment() {
        super(R.layout.profile_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = MainActivity.mAuth;
        database = MainActivity.database;
        user = MainActivity.user;

        name = view.findViewById(R.id.nameText);
        studentID = view.findViewById(R.id.studentIDText);

        barcode = view.findViewById(R.id.barcodeImage);
        profileUpdateIcon = view.findViewById(R.id.profileUpdateIcon);
        pfp = view.findViewById(R.id.profileImage);
        sharedPreferences = context.getSharedPreferences("com.iicportal", MODE_PRIVATE);

        database.getReference("users/"+user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                name.setText(dataSnapshot.child("fullName").getValue(String.class));
                String role = dataSnapshot.child("role").getValue().toString();
                sharedPreferences.edit().putString("role", role).apply();
                if (role.equals("Admin") || role.equals("Vendor") || role.equals("Staff")) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    int positionOfAtSymbol = email.indexOf("@");
                    if (positionOfAtSymbol < 0) {
                        studentID.setText(email.toUpperCase());
                        studentID.setVisibility(View.GONE);
                        barcode.setVisibility(View.GONE);
                    } else {
                        String frontSegmentOfEmail = email.substring(0, positionOfAtSymbol);
                        studentID.setText(frontSegmentOfEmail.toUpperCase());
                        studentID.setVisibility(View.GONE);
                        barcode.setVisibility(View.GONE);
                    }
                    Glide.with(view.getContext()).load(dataSnapshot.child("image").getValue(String.class)).into(pfp);
                } else if (role.equals("Student")) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    int positionOfAtSymbol = email.indexOf("@");
                    if (positionOfAtSymbol < 0) {
                        studentID.setText(email.toUpperCase());
                        studentID.setVisibility(View.VISIBLE);
                        barcode.setVisibility(View.VISIBLE);
                    } else {
                        String frontSegmentOfEmail = email.substring(0, positionOfAtSymbol);
                        studentID.setText(frontSegmentOfEmail.toUpperCase());
                        studentID.setVisibility(View.VISIBLE);
                        barcode.setVisibility(View.VISIBLE);
                    }
                    Glide.with(view.getContext()).load(dataSnapshot.child("image").getValue(String.class)).into(pfp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        profileUpdateIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileUpdateActivity.class);
            intent.putExtra("userID",user.getUid());
            context.startActivity(intent);
        });

        contactIcon = view.findViewById(R.id.contactIcon);

        contactIcon.setOnClickListener(v -> {
            startActivity(new Intent(context, ContactActivity.class));
        });

        logoutButtonIcon = view.findViewById(R.id.logoutBtnIcon);

        logoutButtonIcon.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        });
    }
}
