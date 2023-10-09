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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.iicportal.models.Facilities;
import com.iicportal.models.OrderStatus;
import com.iicportal.models.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

public class StudentHomeFragment extends Fragment {

    private Context context;

    private ImageView messageButtonIcon, userImage;
    private TextView usernameText, statusEmptyText;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference userRef, facilityRef, bookingRef, orderRef;

    private RecyclerView statusRecyclerView;
    private StatusAdaptor statusAdaptor;

    private ArrayList<String> facilityNameList;
    private ArrayList<Status> statusList;

    private static final String STUDENT_HOME_TAG = "StudentHomeFragment";

    public StudentHomeFragment() {
        super(R.layout.student_home_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_home_fragment, container, false);

        facilityNameList = new ArrayList<String>();
        statusList = new ArrayList<Status>();

        // Initialize firebase objects
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users/").child(currentUser.getUid());
        facilityRef = database.getReference("facilities/facility/");
        bookingRef = database.getReference("bookings/");
        orderRef = database.getReference("orders/");

        // Set reference to views
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
                // TODO: uncomment this when user profile pictures are implemented
                // Glide.with(userImage.getContext()).load(task.getResult().child("image").getValue().toString().into(userImage);
            } else {
                Log.e(STUDENT_HOME_TAG, "Error getting user data", task.getException());
            }
        });
        //endregion

        //region Services
        // TODO: Implement Facilities and E-Canteen expandable recycler views
        //endregion

        //region Status/Reminders
        // Retrieve all facilities
        facilityRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot facilitySnapshot : task.getResult().getChildren())
                    facilityNameList.add(facilitySnapshot.child("name").getValue().toString());

                // Retrieve the user's upcoming facility bookings
                for (String facility : facilityNameList) {
                    Query bookingQuery = bookingRef.child(facility).orderByChild("userId").equalTo(currentUser.getUid());
                    bookingQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Calendar calendar = Calendar.getInstance();

                            for (DataSnapshot snapshotItem : snapshot.getChildren()) {
                                String bookingId = snapshotItem.getKey();
                                Long currentTimestamp = System.currentTimeMillis();

                                Date selectedDate = null;
                                try {
                                    selectedDate = new SimpleDateFormat("dd/MM/yyyy").parse(snapshotItem.child("selectedDate").getValue().toString());
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                Long bookingTimestamp = selectedDate.getTime();

                                calendar.setTimeInMillis(currentTimestamp);
                                int currentDay = calendar.get(Calendar.DATE);
                                calendar.setTimeInMillis(bookingTimestamp);
                                int bookingDay = calendar.get(Calendar.DATE);

                                // Provide status reminder for the current day's bookings
                                if (currentDay == bookingDay) {
                                    String facilityImage = snapshotItem.child("facilityImage").getValue().toString();
                                    String facilityName = snapshotItem.child("facilityName").getValue().toString();
                                    String bookingTime = snapshotItem.child("selectedTimeSlot").getValue().toString();
                                    String description = String.format("Hey there, don't pocket this reminder: Your %s booking for today at %s is just around the corner!",
                                            facilityName, bookingTime);

                                    BookingStatus newBookingStatus = new BookingStatus(currentUser.getUid(), bookingId, "REMINDER!!!", description, "booking", System.currentTimeMillis(), facilityImage, facilityName);
                                    removeStatusFromList(statusList, bookingId);
                                    statusList.add(newBookingStatus);
                                    statusAdaptor.notifyDataSetChanged();
                                }

                                // Delete old status reminder if booking time is past current time
                                if (currentTimestamp > bookingTimestamp) {
                                    //removeStatusFromList(statusList, bookingId);
                                    //statusAdaptor.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w(STUDENT_HOME_TAG, "loadBooking:onCancelled", error.toException());
                        }
                    });
                }
            } else {
                Log.w(STUDENT_HOME_TAG, "loadFacilities:onCancelled", task.getException());
            }
        });

        // Retrieve the user's ongoing orders
        Query orderQuery = orderRef.orderByChild("uid").equalTo(currentUser.getUid());
        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotItem : snapshot.getChildren()) {
                    String orderId = snapshotItem.getKey();
                    String statusValue = snapshotItem.child("status").getValue().toString();

                    // Only provide status reminder if order status is "PREPARING" or "READY"
                    if (statusValue.matches("PREPARING|READY")) {
                        String description = "";
                        Long timestamp = System.currentTimeMillis();

                        if (statusValue.equals("PREPARING")) {
                            description = "Just a quick note to inform you that your order is currently being prepared and will be ready soon.";
                            timestamp = Long.parseLong(snapshotItem.child("timestamp").getValue().toString());
                        } else if (statusValue.equals("READY")) {
                            description = "Just a quick note to inform you that your order is ready for pickup or delivery.";
                            // TODO: uncomment this when Vendor features are complete
                            //timestamp = Long.parseLong(snapshotItem.child("readyTimestamp").getValue().toString());
                        }

                        OrderStatus newOrderStatus = new OrderStatus(currentUser.getUid(), orderId, "ORDER STATUS:", description, "order", timestamp, statusValue);
                        removeStatusFromList(statusList, orderId);
                        statusList.add(newOrderStatus);
                        statusAdaptor.notifyDataSetChanged();
                    }
                    // Delete old status reminder if order status is "COMPLETED"
                    else if (statusValue.equals("COMPLETED")) {
                        removeStatusFromList(statusList, orderId);
                        statusAdaptor.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(STUDENT_HOME_TAG, "loadOrder:onCancelled", error.toException());
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
        //endregion

        // Button onClick listeners
        messageButtonIcon.setOnClickListener(v -> {
            // TODO: add navigation when messages are implemented
        });

        return view;
    }

    // Helper method to remove status item from statusList
    public void removeStatusFromList(ArrayList<Status> statusList, String id) {
        Iterator<Status> iterator = statusList.iterator();

        while (iterator.hasNext()) {
            Status item = iterator.next();

            if (item.getType().equals("booking")) {
                BookingStatus bookingStatus = (BookingStatus) item;
                if (bookingStatus.getBookingId().equals(id))
                    iterator.remove();
            } else if (item.getType().equals("order")) {
                OrderStatus orderStatus = (OrderStatus) item;
                if (orderStatus.getOrderId().equals(id))
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
