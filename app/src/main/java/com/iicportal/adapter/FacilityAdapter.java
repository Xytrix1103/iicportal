package com.iicportal.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FacilityAdapter extends FirebaseRecyclerAdapter<BookingItem,FacilityAdapter.FacilityViewHolder> {
    Context context;
    FirebaseDatabase database;
    DatabaseReference bookingRef;
    DatabaseReference facilitiesRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    SharedPreferences sharedPreferences;


    public FacilityAdapter(@NonNull FirebaseRecyclerOptions<BookingItem> options, Context context) {
        super(options);
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.bookingRef = database.getReference("bookings");
        this.bookingRef.keepSynced(true);
        this.facilitiesRef = database.getReference("facilities/facility/");
        this.facilitiesRef.keepSynced(true);
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);

    }

    public void onDataChanged() {
        super.onDataChanged();
        if (super.getItemCount() > 0) {
            // Assuming getItem(0) returns a Facility object
            String facilityName = super.getItem(0).getName();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("facility", facilityName);
            editor.apply();
        }
        notifyDataSetChanged();
    }

//    create a function where we can reset the bookings for all facility when it is 12am or not equal to the current date
    public void resetBookings() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());

        // Get a reference to the "facilities" node
        DatabaseReference facilitiesRef = database.getReference("facilities/facility");

        facilitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot facilitiesSnapshot) {
                for (DataSnapshot facilitySnapshot : facilitiesSnapshot.getChildren()) {
                    String facilityName = facilitySnapshot.child("name").getValue(String.class);
                    if (facilityName != null) {
                        // Get the bookings for the current facility
                        Query query = bookingRef.child(facilityName);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Give me a log message
                                Log.d("BookingAdapter", "Resetting bookings for " + facilityName);
                                // Loop through the bookings
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Get the booking date
                                    String bookingDate = snapshot.child("selectedDate").getValue(String.class);
                                    // Check if the booking date is not equal to the current date
                                    if (!bookingDate.equals(currentDate)) {
                                        // Delete the booking
                                        snapshot.getRef().removeValue();
                                    } else {
                                        Log.d("BookingAdapter", "Booking date is equal to current date");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BookingAdapter", "Database error: " + databaseError.getMessage());
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("BookingAdapter", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull FacilityViewHolder holder, int position, @NonNull BookingItem model) {
        holder.facilityName.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.facilityImage);

        //Call the resetBookings function
        resetBookings();

        holder.booknowBtn.setOnClickListener(v -> {
            Log.d("BookingAdapter", "Book button clicked");
            Dialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.booking_dialog);

            TextView facilityName = dialog.findViewById(R.id.facility_name);
            ImageView facilityImage = dialog.findViewById(R.id.facility_image);
            Spinner bookingSpinner = dialog.findViewById(R.id.booking_time);
            TextView date = dialog.findViewById(R.id.booking_date);
            TextView price = dialog.findViewById(R.id.price);
            Button confirm = dialog.findViewById(R.id.confirmBtn);
            Button cancel = dialog.findViewById(R.id.cancelBtn);

            facilityName.setText(model.getName());
            Glide.with(context).load(model.getImage()).into(facilityImage);

            // Display the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(Calendar.getInstance().getTime());
            date.setText(currentDate);

            price.setText(String.valueOf(model.getPrice()));

            ArrayList<String> timeSlotsList = new ArrayList<>();
            String[] timeSlotsArray = context.getResources().getStringArray(R.array.timeslot);
            timeSlotsList.addAll(Arrays.asList(timeSlotsArray));

            // Assuming you have identified the bookedTimeSlot as a String
            timeSlotsList.remove(bookingSpinner.getSelectedItem().toString());

            // Populate the Spinner with time slots from resources
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeSlotsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            bookingSpinner.setAdapter(adapter);



            confirm.setOnClickListener(v1 -> {
                Log.d("BookingAdapter", "Confirm button clicked");

                // Retrieve the selected time slot from the Spinner
                String selectedBooking = bookingSpinner.getSelectedItem().toString();

                // Query the database to check if the selected time slot is available for this facility
                Query query = bookingRef.child(model.getName())
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

                            BookingItem bookingItem = new BookingItem(model.getName(), model.getImage(), model.getPrice(), selectedBooking, currentDate);

                            // Create a map to store the booking data
                            Map<String, Object> bookingData = new HashMap<>();
                            bookingData.put("userId", user.getUid());
                            bookingData.put("facilityName", model.getName());
                            bookingData.put("facilityImage", model.getImage());
                            bookingData.put("facilityPrice", model.getPrice());
                            bookingData.put("selectedDate", currentDate);
                            bookingData.put("selectedTimeSlot", selectedBooking);

                            // Save the booking data under the "bookings" node with the unique booking ID
                            bookingRef.child(model.getName()).child(bookingId).setValue(bookingData);
                            dialog.dismiss();
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
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    @NonNull
    @Override
    public FacilityAdapter.FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.facilities,parent,false);
        return new FacilityViewHolder(view);
    }

    public class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName;
        ImageView facilityImage;
        TextView booknowBtn;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
            booknowBtn = itemView.findViewById(R.id.booknowBtn);
        }
    }
}

