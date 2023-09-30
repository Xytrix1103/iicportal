package com.iicportal.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adapter.BookingAdapter;
import com.iicportal.adapter.FacilityAdapter;
import com.iicportal.models.BookingItem;

public class FacilityMenuActivity extends AppCompatActivity  {
    Context context = this;
    RecyclerView facilitiesRecyclerView;
    RecyclerView bookingRecyclerView;

    FacilityAdapter facilityAdaptor;
    BookingAdapter bookingAdaptor;

    FirebaseDatabase database;
    DatabaseReference timeslotRef;
    DatabaseReference facilitiesRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;

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
        facilityAdaptor = new FacilityAdapter(facilityOptions, context);
        facilitiesRecyclerView.setAdapter(facilityAdaptor);

//        FirebaseRecyclerOptions<BookingItem> bookingOptions = new FirebaseRecyclerOptions.Builder<BookingItem>()
//                .setQuery(timeslotRef.orderByChild("facility").equalTo(sharedPreferences.getString("facility","")),BookingItem.class)
//                .build();
//
//        bookingRecyclerView = findViewById(R.id.bookingRecyclerView);
//        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        bookingAdaptor = new BookingAdapter(bookingOptions, context);
//        bookingRecyclerView.setAdapter(bookingAdaptor);
    }

        @Override
        protected void onStart() {
            super.onStart();
            sharedPreferences.edit().remove("facility").apply();
            facilityAdaptor.startListening();
//            bookingAdaptor.startListening();
        }

        @Override
        protected void onStop() {
            super.onStop();
            sharedPreferences.edit().remove("facility").apply();
            facilityAdaptor.stopListening();
//            bookingAdaptor.stopListening();
        }
    }
