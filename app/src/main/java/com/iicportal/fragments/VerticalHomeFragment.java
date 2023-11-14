package com.iicportal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.StatusAdaptor;
import com.iicportal.models.BookingStatus;
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

public class VerticalHomeFragment extends Fragment {
    private Context context;

    private BottomNavigationView bottomNavigationView;
    private CardView facilitiesButtonCard, ecanteenButtonCard;
    private ImageView messageButtonIcon, userImage;
    private TextView usernameText, statusEmptyText;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Button logoutButton;
    Button livechatButton;
    Button contactButton;
    private FirebaseDatabase database;
    private DatabaseReference userRef, facilityRef, bookingRef, orderRef;

    private RecyclerView statusRecyclerView;
    private StatusAdaptor statusAdaptor;

    private ArrayList<String> facilityNameList;
    private ArrayList<Status> statusList;

    private static final String STUDENT_HOME_TAG = "StudentHomeFragment";

    public VerticalHomeFragment() {
        super(R.layout.vertical_home_fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vertical_home_fragment, container, false);

        facilityNameList = new ArrayList<String>();
        statusList = new ArrayList<Status>();

        // Initialize firebase objects
        mAuth = MainActivity.mAuth;
        currentUser = mAuth.getCurrentUser();
        database = MainActivity.database;
        userRef = database.getReference("users/").child(currentUser.getUid());
        facilityRef = database.getReference("facilities/facility/");
        bookingRef = database.getReference("bookings/");
        orderRef = database.getReference("orders/");

        // Set reference to views
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        facilitiesButtonCard = view.findViewById(R.id.facilitiesCard);
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

                if (task.getResult().child("image").getValue() != null)
                    Glide.with(userImage.getContext()).load(task.getResult().child("image").getValue().toString()).placeholder(R.drawable.baseline_image_placeholdeer).into(userImage);
            } else {
                Log.e(STUDENT_HOME_TAG, "Error getting user data", task.getException());
            }
        });
        //endregion

        //region Services
        ecanteenButtonCard.setOnClickListener(v -> {
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, new ECanteenMenuFragment()).commit();
        });

        facilitiesButtonCard.setOnClickListener(v -> {
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.vertical_fragment_container, new FacilityMenuFragment()).commit();
        });
        //endregion

        //region Status/Reminders
        // Retrieve all facilities
        Query bookingQuery = bookingRef.orderByChild("userId").equalTo(currentUser.getUid());
        bookingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");

                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    String bookingId = bookingSnapshot.getKey();
                    String selectedDate = bookingSnapshot.child("date").getValue().toString();
                    String selectedTimeSlot = bookingSnapshot.child("time").getValue().toString();

                    // Get current date, booking date, start time, and end time
                    String[] timeParts = selectedTimeSlot.split(" - ");
                    Long currentTimestamp = System.currentTimeMillis();
                    Long startTimestamp, endTimestamp;
                    int currentDate, bookingDate, bookingMonth, bookingYear;
                    try {
                        currentDate = Calendar.getInstance().get(Calendar.DATE);

                        bookingDate = dateFormat.parse(selectedDate).getDate();
                        bookingMonth = dateFormat.parse(selectedDate).getMonth() + 1;
                        bookingYear = dateFormat.parse(selectedDate).getYear();

                        Date startTime = timeFormat.parse(timeParts[0].trim());
                        startTime.setDate(bookingDate);
                        startTime.setMonth(bookingMonth - 1);
                        startTime.setYear(bookingYear);
                        startTimestamp = startTime.getTime();

                        Date endTime = timeFormat.parse(timeParts[1].trim());
                        endTime.setDate(bookingDate);
                        endTime.setMonth(bookingMonth - 1);
                        endTime.setYear(bookingYear);
                        endTimestamp = endTime.getTime();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    // Provide status reminder for the current day's bookings
                    if (currentDate == bookingDate && currentTimestamp < endTimestamp) {
                        String title = "REMINDER!!!";
                        String description = "";
                        String type = "booking";
                        String facilityImage = bookingSnapshot.child("image").getValue().toString();
                        String facilityName = bookingSnapshot.child("name").getValue().toString();
                        String state = "";

                        if (currentTimestamp < startTimestamp) {
                            description = String.format("Hey there, don't pocket this reminder: Your %s booking for today at %s is just around the corner!", facilityName, selectedTimeSlot);
                            state = "before";
                        } else {
                            description = String.format("Hey there, just reminding you that your %s booking for today at %s has started.", facilityName, selectedTimeSlot);
                            state = "ongoing";
                        }

                        BookingStatus newBookingStatus = new BookingStatus(
                                currentUser.getUid(), title, description, type, currentTimestamp,
                                bookingId, facilityImage, facilityName, state, startTimestamp, endTimestamp
                        );
                        removeStatusFromList(statusList, bookingId);
                        statusList.add(newBookingStatus);
                        statusAdaptor.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(STUDENT_HOME_TAG, "loadBooking:onCancelled", error.toException());
            }
        });

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

        // Handler for periodically updating booking statuses
        Handler bookingStatusHandler = new Handler(Looper.getMainLooper());
        bookingStatusHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Iterator<Status> iterator = statusList.iterator();
                Long currentTimestamp = System.currentTimeMillis();

                boolean isUpdated = false;
                while (iterator.hasNext()) {
                    Status item = iterator.next();

                    if (item.getType().equals("booking")) {
                        BookingStatus bookingStatus = (BookingStatus) item;

                        // Update description if current time is within booking time range
                        if ((currentTimestamp >= bookingStatus.getStartTimestamp() && currentTimestamp < bookingStatus.getEndTimestamp()) && bookingStatus.getState().equals("before")) {
                            bookingStatus.setDescription(String.format("Hey there, your %s booking for today at %s has started. Just reminding you in case you forgot!",
                                    bookingStatus.getFacilityName(), new SimpleDateFormat("h:mma").format(bookingStatus.getStartTimestamp()) + " - " + new SimpleDateFormat("h:mma").format(bookingStatus.getEndTimestamp())));
                            bookingStatus.setTimestamp(System.currentTimeMillis());
                            bookingStatus.setState("ongoing");

                            isUpdated = true;
                        }
                        // Remove status if current time is past booking end time
                        else if (currentTimestamp > bookingStatus.getEndTimestamp()) {
                            iterator.remove();
                            isUpdated = true;
                        }
                    }
                }

                if (isUpdated)
                    statusAdaptor.notifyDataSetChanged();

                bookingStatusHandler.postDelayed(this, 5000);
            }
        }, 0);
        //endregion

        // Button onClick listeners
        messageButtonIcon.setOnClickListener(v -> {
            // TODO: add navigation when chat feature is implemented
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
                if (orderStatus.getOrderKey().equals(key))
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
