package com.iicportal.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iicportal.R;
import com.iicportal.activity.ContactActivity;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;
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
    CardView contactButton;
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
                Glide.with(view.getContext()).load(dataSnapshot.child("image").getValue(String.class)).into(pfp);

                String email = dataSnapshot.child("email").getValue(String.class);
                int positionOfAtSymbol = email.indexOf("@");
                if (positionOfAtSymbol < 0) {
                    studentID.setText(email.toUpperCase());
                } else {
                    String frontSegmentOfEmail = email.substring(0, positionOfAtSymbol);
                    studentID.setText(frontSegmentOfEmail.toUpperCase());
                }
                String studentIDBarcode = (String) studentID.getText();
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(studentIDBarcode, BarcodeFormat.CODE_128,barcode.getWidth(),barcode.getHeight());
                    Bitmap bitmap = Bitmap.createBitmap(barcode.getWidth(),barcode.getHeight(),Bitmap.Config.RGB_565);
                    for (int i = 0; i < barcode.getWidth(); i++){
                        for (int j = 0; j < barcode.getHeight(); j++){
                            bitmap.setPixel(i,j,bitMatrix.get(i,j)? Color.BLACK:Color.WHITE);
                        }
                    }
                    barcode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                String role = dataSnapshot.child("role").getValue().toString();
                sharedPreferences.edit().putString("role", role).apply();
                if (role.equals("Admin") || role.equals("Vendor") || role.equals("Staff")) {
                    studentID.setVisibility(View.GONE);
                    barcode.setVisibility(View.GONE);
                } else if (role.equals("Student")) {
                    studentID.setVisibility(View.VISIBLE);
                    barcode.setVisibility(View.VISIBLE);
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

        contactButton = view.findViewById(R.id.contactBtn);

        contactButton.setOnClickListener(v -> {
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
