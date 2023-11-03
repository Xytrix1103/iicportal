package com.iicportal.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.AddFacilityActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.FacilityAdaptor;
import com.iicportal.models.BookingItem;

public class EditFacilityMenuFragment extends Fragment {

    private FacilityAdaptor facilityAdaptor;

    FrameLayout menuFragmentContainer;
    String facility;
    Context context;

    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FragmentManager childfragmentManager;

    public EditFacilityMenuFragment() {
        super(R.layout.edit_facility_menu_fragment);
        openDrawerInterface = null;
        childfragmentManager = null;
    }

    public EditFacilityMenuFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface, FragmentManager childfragmentManager) {
        super(R.layout.edit_facility_menu_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.childfragmentManager = childfragmentManager;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_facility_menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();

        RecyclerView facilityRecyclerView = view.findViewById(R.id.facilityRecyclerView);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        ImageView stopEditBtn = view.findViewById(R.id.stopEditBtn);
        ImageView addBtn = view.findViewById(R.id.addFacilityBtn);
        menuFragmentContainer = view.findViewById(R.id.menu_Fragment_Container);

        FirebaseDatabase database = MainActivity.database;
        DatabaseReference facilitiesRef = database.getReference("facilities/facility");

        facilityRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        FirebaseRecyclerOptions<BookingItem> options = new FirebaseRecyclerOptions.Builder<BookingItem>().setQuery(facilitiesRef, BookingItem.class).build();
        facilityAdaptor = new FacilityAdaptor(options, getContext(), getChildFragmentManager(), true);
        facilityRecyclerView.setAdapter(facilityAdaptor);


        String role = requireActivity().getSharedPreferences("com.iicportal", 0).getString("role", "Student");

        if (role.equals("Admin")) {
            stopEditBtn.setVisibility(View.VISIBLE);

            stopEditBtn.setOnClickListener(v -> {
                FacilityMenuFragment facilityMenuFragment = new FacilityMenuFragment(openDrawerInterface, childfragmentManager);
                childfragmentManager.beginTransaction().replace(R.id.facility_fragment_container, facilityMenuFragment).commit();
            });
        } else {
            stopEditBtn.setVisibility(View.GONE);
        }

        menuBtn.setOnClickListener(v -> {
            if (openDrawerInterface != null) {
                openDrawerInterface.openDrawer();
            }
        });

        addBtn.setOnClickListener(v -> {
            context.startActivity(new Intent(context, AddFacilityActivity.class).putExtra("key", "new"));
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("facility", facility);
    }

    @Override
    public void onStart() {
        super.onStart();
        facilityAdaptor.startListening();
    }

    @Override
    public void onResume(){
        super.onResume();
        facilityAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        facilityAdaptor.stopListening();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (facility != null){
            MenuFragment menuFragment = new MenuFragment(facility, context, getChildFragmentManager(), true);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container, menuFragment).commit();
        }
    }

}
