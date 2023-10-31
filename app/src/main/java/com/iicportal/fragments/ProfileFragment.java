package com.iicportal.fragments;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
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
    Button logoutButton;
    Button profileUpdateButton;
    Button orderHistoryButton;
    Button bookingHistoryButton;
    Button contactButton;
    TextView name, studentID;
    ImageView barcode, pfp;
    String[] initial = {"", "", "", ""};
    public ProfileFragment() {
        super(R.layout.activity_profile);
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
        profileUpdateButton = view.findViewById(R.id.profileUpdateBtn);
        pfp = view.findViewById(R.id.profileImage);

        database.getReference("users/"+user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                name.setText(dataSnapshot.child("fullName").getValue(String.class));
                String email = dataSnapshot.child("email").getValue(String.class);
                int positionOfAtSymbol = email.indexOf("@");
                if (positionOfAtSymbol < 0) {
                    studentID.setText(email.toUpperCase());
                } else {
                    String frontSegmentOfEmail = email.substring(0, positionOfAtSymbol);
                    studentID.setText(frontSegmentOfEmail.toUpperCase());
                }
                Glide.with(view.getContext()).load(dataSnapshot.child("image").getValue(String.class)).into(pfp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        profileUpdateButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileUpdateActivity.class);
            intent.putExtra("userID",user.getUid());
            context.startActivity(intent);
        });
        orderHistoryButton = view.findViewById(R.id.orderHistoryBtn);

        orderHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            context.startActivity(intent);
        });

        bookingHistoryButton = view.findViewById(R.id.bookingHistoryBtn);

        bookingHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileUpdateActivity.class);
            intent.putExtra("userID",user.getUid());
            context.startActivity(intent);
        });

        contactButton = view.findViewById(R.id.contactBtn);

        contactButton.setOnClickListener(v -> {
            startActivity(new Intent(context, ContactActivity.class));
        });

        logoutButton = view.findViewById(R.id.logoutBtn);

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        });
    }
}
