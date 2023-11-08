package com.iicportal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.adaptor.StatusAdaptor;
import com.iicportal.models.BookingStatus;
import com.iicportal.models.OrderStatus;
import com.iicportal.models.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import com.iicportal.activity.MainActivity;

public class StaffHomeFragment extends Fragment {
    private Context context;

    private BottomNavigationView bottomNavigationView;
    private CardView ecanteenButtonCard;
    private ImageView messageButtonIcon, userImage;
    private TextView usernameText, statusEmptyText;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference userRef, orderRef;

    private RecyclerView statusRecyclerView;
    private StatusAdaptor statusAdaptor;

    private ArrayList<Status> statusList;

    private static final String STAFF_HOME_TAG = "StaffHomeFragment";

    public StaffHomeFragment() {
        super(R.layout.staff_home_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_home_fragment, container, false);

        mAuth = MainActivity.mAuth;
        statusList = new ArrayList<Status>();

        // Initialize firebase objects
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users/").child(currentUser.getUid());
        orderRef = database.getReference("orders/");

        // Set reference to views
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        ecanteenButtonCard = view.findViewById(R.id.ecanteenCard);
        messageButtonIcon = view.findViewById(R.id.messageBtnIcon);
        userImage = view.findViewById(R.id.userImage);
        usernameText = view.findViewById(R.id.usernameText);
        statusEmptyText = view.findViewById(R.id.statusEmptyText);

        statusRecyclerView = view.findViewById(R.id.statusRecyclerView);
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        //region Welcome section
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                usernameText.setText(task.getResult().child("fullName").getValue().toString());
                Glide.with(userImage.getContext()).load(task.getResult().child("image").getValue().toString()).into(userImage);
            } else {
                Log.e(STAFF_HOME_TAG, "Error getting user data", task.getException());
            }
        });
        //endregion

        //region Services
        ecanteenButtonCard.setOnClickListener(v -> {
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, new ECanteenMenuFragment()).commit();
        });
        //endregion

        //region Status/Reminders
        // Retrieve the user's ongoing orders
        Query orderQuery = orderRef.orderByChild("uid").equalTo(currentUser.getUid());
        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotItem : snapshot.getChildren()) {
                    String orderKey = snapshotItem.getKey();
                    String orderId = snapshotItem.child("orderID").getValue().toString();
                    String statusValue = snapshotItem.child("status").getValue().toString();

                    // Only provide status reminder if order status is "PREPARING" or "READY"
                    if (statusValue.matches("PREPARING|READY")) {
                        String title = "ORDER STATUS:";
                        String description = "";
                        String type = "order";
                        Long timestamp = System.currentTimeMillis();

                        if (statusValue.equals("PREPARING")) {
                            description = "Just a quick note to inform you that your order is currently being prepared and will be ready soon.";
                            timestamp = Long.parseLong(snapshotItem.child("timestamp").getValue().toString());
                        } else if (statusValue.equals("READY")) {
                            description = "Just a quick note to inform you that your order is ready for pickup or delivery.";
                            timestamp = Long.parseLong(snapshotItem.child("readyTimestamp").getValue().toString());
                        }

                        OrderStatus newOrderStatus = new OrderStatus(
                                currentUser.getUid(), title, description, type, timestamp,
                                orderKey, orderId, statusValue
                        );
                        removeStatusFromList(statusList, orderKey);
                        statusList.add(newOrderStatus);
                        statusAdaptor.notifyDataSetChanged();
                    }
                    // Delete old status reminder if order status is "COMPLETED"
                    else if (statusValue.equals("COMPLETED")) {
                        removeStatusFromList(statusList, orderKey);
                        statusAdaptor.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(STAFF_HOME_TAG, "loadOrder:onCancelled", error.toException());
            }
        });

        // Populate RecyclerView
        statusAdaptor = new StatusAdaptor(statusList, context);
        statusRecyclerView.setAdapter(statusAdaptor);
        statusAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                Collections.sort(statusList, Comparator.comparingLong(Status::getTimestamp).reversed());

                if (statusAdaptor.getItemCount() == 0)
                    statusEmptyText.setVisibility(View.VISIBLE);
                else
                    statusEmptyText.setVisibility(View.GONE);
            }
        });

        // Button onClick listeners
        messageButtonIcon.setOnClickListener(v -> {
            // TODO: add navigation when messages are implemented
        });

        return view;
    }

    // Helper method to remove status item from statusList
    public void removeStatusFromList(ArrayList<Status> statusList, String key) {
        Iterator<Status> iterator = statusList.iterator();

        while (iterator.hasNext()) {
            Status item = iterator.next();

            if (item.getType().equals("booking")) {
                BookingStatus bookingStatus = (BookingStatus) item;
                if (bookingStatus.getBookingId().equals(key))
                    iterator.remove();
            } else if (item.getType().equals("order")) {
                OrderStatus orderStatus = (OrderStatus) item;
                if (orderStatus.getOrderId().equals(key))
                    iterator.remove();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
