package com.iicportal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.models.BookingItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookingDialogFragment extends BottomSheetDialogFragment {
    BookingItem bookingItem;
    FirebaseDatabase database;
    DatabaseReference bookingRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String key;
    Context context;

    public BookingDialogFragment(BookingItem bookingItem, Context context, String key) {
        this.bookingItem = bookingItem;
        this.database = FirebaseDatabase.getInstance();
        this.bookingRef = database.getReference("bookings/");
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.key = key;
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_dialog, container, false);
        TextView facilityName = view.findViewById(R.id.facility_name);
        ImageView facilityImage = view.findViewById(R.id.facility_image);
        Spinner bookingSpinner = view.findViewById(R.id.booking_time);
        TextView date = view.findViewById(R.id.booking_date);
        TextView price = view.findViewById(R.id.price);
        TextView confirm = view.findViewById(R.id.confirmBtn);
        TextView cancel = view.findViewById(R.id.cancelBtn);

        facilityName.setText(bookingItem.getName());
        Glide.with(context).load(bookingItem.getImage()).into(facilityImage);

        // Display the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        date.setText(currentDate);

        price.setText(String.format("RM %.2f", bookingItem.getPrice()));

        ArrayList<String> timeSlotsList = new ArrayList<>();
        String[] timeSlotsArray = context.getResources().getStringArray(R.array.timeslot);
        timeSlotsList.addAll(Arrays.asList(timeSlotsArray));

        // Assuming you have identified the bookedTimeSlot as a String
        timeSlotsList.remove(bookingSpinner.getSelectedItem().toString());

        // Disable time slots that have already passed
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.getDefault());
        String currentTime = timeFormat.format(Calendar.getInstance().getTime());
        String thresholdTime = "08:00AM";

        for (int i = 0; i < timeSlotsList.size(); i++) {
            String timeSlot = timeSlotsList.get(i);
            if (isTimeSlotPassed(currentTime, timeSlot) || isBeforeThreshold(currentTime, thresholdTime)) {
                // If the time slot has passed or it's before the threshold time, remove it from the list
                timeSlotsList.remove(i);
                i--; // Adjust the index to account for the removed item
            }
        }

        //populate the spinner with the time slots
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeSlotsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookingSpinner.setAdapter(adapter);

        confirm.setOnClickListener(v1 -> {
            Log.d("BookingAdapter", "Confirm button clicked");

            // Retrieve the selected time slot from the Spinner
            String selectedBooking = bookingSpinner.getSelectedItem().toString();

            // Query the database to check if the selected time slot is available for this facility
            Query query = bookingRef.child(bookingItem.getName())
                    .orderByChild("selectedTimeSlot")
                    .equalTo(selectedBooking);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The selected time slot is already taken, show an error message
                        Toast.makeText(context.getApplicationContext(), "Time slot is already booked for this facility", Toast.LENGTH_SHORT).show();

                    } else {
                        // Generate a unique booking ID
                        String bookingId = bookingRef.push().getKey();

                        BookingItem newBookingItem = new BookingItem(bookingItem.getName(), bookingItem.getImage(), bookingItem.getPrice(), selectedBooking, currentDate);

                        // Create a map to store the booking data
                        Map<String, Object> bookingData = new HashMap<>();
                        bookingData.put("userId", user.getUid());
                        bookingData.put("facilityName", bookingItem.getName());
                        bookingData.put("facilityImage", bookingItem.getImage());
                        bookingData.put("facilityPrice", bookingItem.getPrice());
                        bookingData.put("selectedDate", currentDate);
                        bookingData.put("selectedTimeSlot", selectedBooking);

                        // Save the booking data under the "bookings" node with the unique booking ID
                        bookingRef.child(bookingItem.getName()).child(bookingId).setValue(bookingData);
                        dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("BookingAdapter", "Database error: " + databaseError.getMessage());
                }
            });
        });

        cancel.setOnClickListener(v1 -> {
            Log.d("BookingAdapter", "Cancel button clicked");
            dismiss();
        });

        return view;
    }

    private boolean isTimeSlotPassed(String currentTime, String timeSlot) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.getDefault());
        try {
            Date currentTimeDate = timeFormat.parse(currentTime);
            Date timeSlotDate = timeFormat.parse(timeSlot);
            return currentTimeDate.after(timeSlotDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Handle parsing error as needed
        }
    }

    // Helper method to check if a time is before the threshold time
    private boolean isBeforeThreshold(String currentTime, String thresholdTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.getDefault());
        try {
            Date currentTimeDate = timeFormat.parse(currentTime);
            Date thresholdTimeDate = timeFormat.parse(thresholdTime);
            return currentTimeDate.before(thresholdTimeDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Handle parsing error as needed
        }
    }
}
