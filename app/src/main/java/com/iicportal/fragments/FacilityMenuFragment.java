package com.iicportal.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.FacilityAdaptor;
import com.iicportal.models.BookingItem;

public class FacilityMenuFragment extends Fragment {
    Context context;
    RecyclerView facilitiesRecyclerView;
    FacilityAdaptor facilityAdaptor;

    FirebaseDatabase database;
    DatabaseReference timeslotRef;
    DatabaseReference facilitiesRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;

    public FacilityMenuFragment() {
        super(R.layout.facilities_menu_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facilities_menu_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        timeslotRef = database.getReference("facilities/timeslot");
        timeslotRef.keepSynced(true);
        facilitiesRef = database.getReference("facilities/facility/");
        facilitiesRef.keepSynced(true);

        sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);

        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("facility")) {
                Log.d("FacilityMenuActivity", "Facility changed to " + sharedPreferences.getString("facility", ""));
                facilityAdaptor.notifyDataSetChanged();
            }
        });

        FirebaseRecyclerOptions<BookingItem> facilityOptions = new FirebaseRecyclerOptions.Builder<BookingItem>()
                .setQuery(facilitiesRef, BookingItem.class)
                .build();

        facilitiesRecyclerView = view.findViewById(R.id.facilityRecyclerView);
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        facilityAdaptor = new FacilityAdaptor(facilityOptions, context);
        facilitiesRecyclerView.setAdapter(facilityAdaptor);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        facilityAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        facilityAdaptor.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        facilityAdaptor.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        facilityAdaptor.stopListening();
    }
}
