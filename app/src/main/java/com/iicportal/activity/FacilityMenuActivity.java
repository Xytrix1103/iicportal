package com.iicportal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.FacilityAdaptor;
import com.iicportal.models.BookingItem;

public class FacilityMenuActivity extends AppCompatActivity  {
    Context context = this;
    RecyclerView facilitiesRecyclerView;
    FacilityAdaptor facilityAdaptor;

    FirebaseDatabase database;
    DatabaseReference timeslotRef;
    DatabaseReference facilitiesRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities_menu);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        timeslotRef = database.getReference("facilities/timeslot");
        timeslotRef.keepSynced(true);
        facilitiesRef = database.getReference("facilities/facility/");
        facilitiesRef.keepSynced(true);

        sharedPreferences = getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);

        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("facility")) {
                Log.d("FacilityMenuActivity", "Facility changed to " + sharedPreferences.getString("facility", ""));
                facilityAdaptor.notifyDataSetChanged();

//                bookingAdaptor.stopListening();
//                FirebaseRecyclerOptions<BookingItem> bookingOptions = new FirebaseRecyclerOptions.Builder<BookingItem>()
//                        .setQuery(timeslotRef.child(sharedPreferences.getString("facility", "")), BookingItem.class)
//                        .build();
//
//                bookingAdaptor = new BookingAdapter(bookingOptions, context);
//                bookingRecyclerView.setAdapter(bookingAdaptor);
//                bookingAdaptor.startListening();
//
//                bookingAdaptor.notifyDataSetChanged();
            }
        });

        FirebaseRecyclerOptions<BookingItem> facilityOptions = new FirebaseRecyclerOptions.Builder<BookingItem>()
                .setQuery(facilitiesRef, BookingItem.class)
                .build();

        facilitiesRecyclerView = findViewById(R.id.facilityRecyclerView);
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        facilityAdaptor = new FacilityAdaptor(facilityOptions, context);
        facilitiesRecyclerView.setAdapter(facilityAdaptor);

//        FirebaseRecyclerOptions<BookingItem> bookingOptions = new FirebaseRecyclerOptions.Builder<BookingItem>()
//                .setQuery(timeslotRef.orderByChild("facility").equalTo(sharedPreferences.getString("facility","")),BookingItem.class)
//                .build();
//
//        bookingRecyclerView = findViewById(R.id.bookingRecyclerView);
//        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        bookingAdaptor = new BookingAdapter(bookingOptions, context);
//        bookingRecyclerView.setAdapter(bookingAdaptor);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.facilities);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.ecanteen) {
                startActivity(new Intent(this, ECanteenMenuActivity.class));
                finish();
                return true;
            } else if (id == R.id.facilities) {
                startActivity(new Intent(this, FacilityMenuActivity.class));
                finish();
                return true;
            } else if (id == R.id.profile) {
                Log.d("MainActivity", "Profile");
                return true;
            }

            return false;
        });
    }

        @Override
        protected void onStart() {
            super.onStart();
            sharedPreferences.edit().remove("facility").apply();
            facilityAdaptor.startListening();
        }

        @Override
        protected void onStop() {
            super.onStop();
            sharedPreferences.edit().remove("facility").apply();
            facilityAdaptor.stopListening();
        }
    }
