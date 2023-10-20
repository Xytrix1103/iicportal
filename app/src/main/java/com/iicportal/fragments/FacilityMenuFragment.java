package com.iicportal.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.FacilityAdaptor;
import com.iicportal.models.BookingItem;

public class FacilityMenuFragment extends Fragment {
    Context context;
    RecyclerView facilitiesRecyclerView;
    FacilityAdaptor facilityAdaptor;
    ImageView menuBtn;

    FirebaseDatabase database;
    DatabaseReference timeslotRef;
    DatabaseReference facilitiesRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;

    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;

    public FacilityMenuFragment() {
        super(R.layout.facilities_menu_fragment);
        openDrawerInterface = null;
    }

    public FacilityMenuFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.facilities_menu_fragment);
        this.openDrawerInterface = openDrawerInterface;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facilities_menu_fragment, container, false);

        mAuth = MainActivity.mAuth;
        user = MainActivity.user;

        database = MainActivity.database;
        timeslotRef = database.getReference("facilities/timeslot");
        facilitiesRef = database.getReference("facilities/facility/");

        sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);

        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("facility")) {
                Log.d("FacilityMenuActivity", "Facility changed to " + sharedPreferences.getString("facility", ""));
                facilityAdaptor.notifyDataSetChanged();
            }
        });

        menuBtn = view.findViewById(R.id.menuIcon);

        sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("role", "").equals("Admin") || sharedPreferences.getString("role", "").equals("Vendor")) {
            menuBtn.setVisibility(View.VISIBLE);
            menuBtn.setOnClickListener(v -> {
                if (openDrawerInterface != null) {
                    openDrawerInterface.openDrawer();
                }
            });
        } else {
            menuBtn.setVisibility(View.GONE);
        }

        FirebaseRecyclerOptions<BookingItem> facilityOptions = new FirebaseRecyclerOptions.Builder<BookingItem>()
                .setLifecycleOwner(this)
                .setQuery(facilitiesRef, BookingItem.class)
                .build();

        facilitiesRecyclerView = view.findViewById(R.id.facilityRecyclerView);
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        facilityAdaptor = new FacilityAdaptor(facilityOptions, context, getChildFragmentManager());
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
