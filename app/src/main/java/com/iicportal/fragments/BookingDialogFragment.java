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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

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
import com.iicportal.activity.MainActivity;
import com.iicportal.models.BookingItem;
import com.iicportal.workers.BookingNotificationWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingDialogFragment extends BottomSheetDialogFragment {
    BookingItem bookingItem;
    FirebaseDatabase database;
    DatabaseReference bookingRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String key;
    String name;
    Context context;

    public BookingDialogFragment() {}

    public BookingDialogFragment(BookingItem bookingItem, Context context, String key, String name) {
        this.bookingItem = bookingItem;
        database = MainActivity.database;
        this.bookingRef = database.getReference("bookings/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.key = key;
        this.name = name;
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.booking_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,  savedInstanceState);

        TextView facilityName = view.findViewById(R.id.facility_name);
        ImageView facilityImage = view.findViewById(R.id.facility_image);
        Spinner bookingSpinner = view.findViewById(R.id.booking_time);
        TextView date = view.findViewById(R.id.booking_date);
        TextView price = view.findViewById(R.id.price);
        TextView confirm = view.findViewById(R.id.confirmBtn);
        TextView cancel = view.findViewById(R.id.cancelBtn);

        facilityName.setText(bookingItem.getName());
        MainActivity.loadImage(bookingItem.getImage(), facilityImage);
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
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
        String currentTime = timeFormat.format(Calendar.getInstance().getTime());
        String thresholdTime = "08:00AM";

        for (int i = 0; i < timeSlotsList.size(); i++) {
            String timeSlot = timeSlotsList.get(i);
            if (isTimeSlotPassed(currentTime, timeSlot) || isBeforeThreshold(currentTime, thresholdTime)) {
                // If the time slot has passed or it's before the threshold time, remove it from the list
                Log.d("BookingAdapter", "Removing time slot: " + timeSlot + isTimeSlotPassed(currentTime, timeSlot) + isBeforeThreshold(currentTime, thresholdTime));
                timeSlotsList.remove(i);
                i--; // Adjust the index to account for the removed item
            }
        }

        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    for (int i = 0; i < timeSlotsList.size(); i++) {
                        String timeSlot = timeSlotsList.get(i);
                        String bookingTime = bookingSnapshot.child("time").getValue(String.class);
                        String bookingDate = bookingSnapshot.child("date").getValue(String.class);
                        Log.d("BookingAdapter", "Booking name: " + bookingSnapshot.child("name").getValue(String.class) + name);
                        if (bookingDate.equals(currentDate) && bookingTime.equals(timeSlot) && bookingSnapshot.child("name").getValue(String.class).equals(name)) {
                            timeSlotsList.remove(timeSlot);
                            i--; // Adjust the index to account for the removed item
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //populate the spinner with the time slots
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeSlotsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookingSpinner.setAdapter(adapter);

        confirm.setOnClickListener(v1 -> {
            Log.d("BookingAdapter", "Confirm button clicked");

            // Retrieve the selected time slot from the Spinner
            String selectedBooking = bookingSpinner.getSelectedItem().toString();

            // Query the database to check if the selected time slot is available for this facility
            Query query = bookingRef.child(bookingItem.getName()).getRef().orderByChild("date").equalTo(currentDate).getRef().orderByChild("time").equalTo(selectedBooking);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The selected time slot is already taken, show an error message
                        Toast.makeText(context.getApplicationContext(), "Time slot is already booked for this facility", Toast.LENGTH_SHORT).show();
                    } else {
                        // The time slot is available, and you can proceed to create a new booking
                        // Generate a unique booking ID
                        String bookingId = bookingRef.push().getKey();

                        BookingItem newBookingItem = new BookingItem(bookingItem.getName(), bookingItem.getImage(), bookingItem.getPrice(), selectedBooking, currentDate, "Paid", user.getUid());

                        // Save the booking data under the "bookings" node with the unique booking ID
                        bookingRef.child(bookingId).setValue(newBookingItem);

                        scheduleNotification(selectedBooking, currentDate);

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
    }

    private void scheduleNotification(String selectedTimeSlot, String currentDate) {
        // Get booking start timestamp and one hour before booking timestamp for notification scheduling
        Long currentTimestamp = System.currentTimeMillis();
        Long oneHourBeforeTimestamp, bookingStartTimestamp;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
        String[] timeParts = selectedTimeSlot.split(" - ");
        try {
            Date startTime = timeFormat.parse(timeParts[0].trim());
            startTime.setDate(dateFormat.parse(currentDate).getDate());
            startTime.setMonth(dateFormat.parse(currentDate).getMonth());
            startTime.setYear(dateFormat.parse(currentDate).getYear());

            bookingStartTimestamp = startTime.getTime();
            oneHourBeforeTimestamp = bookingStartTimestamp - (60 * 60 * 1000);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Schedule booking reminder notifications by sending WorkRequests to WorkManager
        Data oneHourBeforeData = new Data.Builder()
                .putString("TITLE", "Facility Booking Reminder")
                .putString("MESSAGE", String.format("Hey there, just reminding you that your %s booking for today at %s is starting in an hour.", bookingItem.getName(), selectedTimeSlot))
                .build();
        Data bookingStartData = new Data.Builder()
                .putString("TITLE", "Facility Booking Started")
                .putString("MESSAGE", String.format("Hey there, just reminding you that your %s booking for today at %s has started.", bookingItem.getName(), selectedTimeSlot))
                .build();

        WorkRequest oneHourBeforeWork = new OneTimeWorkRequest.Builder(BookingNotificationWorker.class)
                .setInputData(oneHourBeforeData)
                .setInitialDelay(oneHourBeforeTimestamp - currentTimestamp, TimeUnit.MILLISECONDS)
                .build();
        WorkRequest bookingStartWork = new OneTimeWorkRequest.Builder(BookingNotificationWorker.class)
                .setInputData(bookingStartData)
                .setInitialDelay(bookingStartTimestamp - currentTimestamp, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context).enqueue(oneHourBeforeWork);
        WorkManager.getInstance(context).enqueue(bookingStartWork);
    }

    private boolean isTimeSlotPassed(String currentTime, String timeSlot) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
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
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
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
