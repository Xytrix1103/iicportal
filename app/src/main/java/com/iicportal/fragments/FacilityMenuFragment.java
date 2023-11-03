package com.iicportal.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.BookingHistoryActivity;
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
    private ImageView historyBtnIcon, editBtn;
    String facility;

    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FragmentManager childFragmentManager;

    public FacilityMenuFragment() {
        super(R.layout.facilities_menu_fragment);
        openDrawerInterface = null;
        childFragmentManager = null;
    }

    public FacilityMenuFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface,FragmentManager childfragmentManager) {
        super(R.layout.facilities_menu_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.childFragmentManager = childfragmentManager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facilities_menu_fragment, container, false);

        menuBtn = view.findViewById(R.id.menuIcon);
        editBtn = view.findViewById(R.id.editBtn);
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;

        database = MainActivity.database;
        timeslotRef = database.getReference("facilities/timeslot");
        facilitiesRef = database.getReference("facilities/facility/");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");


        if (!role.equals("Admin") && !role.equals("Vendor")) {
            editBtn.setVisibility(View.GONE);
        } else {
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(v -> {
                EditFacilityMenuFragment editFacilityMenuFragment = new EditFacilityMenuFragment(openDrawerInterface, childFragmentManager);
                childFragmentManager.beginTransaction().replace(R.id.facility_fragment_container, editFacilityMenuFragment).commit();
            });
        }

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
                .setQuery(facilitiesRef, BookingItem.class)
                .build();

        facilitiesRecyclerView = view.findViewById(R.id.facilityRecyclerView);
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        facilityAdaptor = new FacilityAdaptor(facilityOptions, requireContext(), getChildFragmentManager(), false);
        facilitiesRecyclerView.setAdapter(facilityAdaptor);

        historyBtnIcon = view.findViewById(R.id.historyBtnIcon);
        historyBtnIcon.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BookingHistoryActivity.class);
            startActivity(intent);
        });
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
}
