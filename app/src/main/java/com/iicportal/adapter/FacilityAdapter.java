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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.models.BookingItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FacilityAdapter extends FirebaseRecyclerAdapter<BookingItem,FacilityAdapter.FacilityViewHolder> {
    Context context;

    FirebaseDatabase database;
    DatabaseReference timeslotRef;
    DatabaseReference facilitiesRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;


    public FacilityAdapter(@NonNull FirebaseRecyclerOptions<BookingItem> options, Context context) {
        super(options);
        this.context = context;
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

    @Override
    protected void onBindViewHolder(@NonNull FacilityViewHolder holder, int position, @NonNull BookingItem model) {
        holder.facilityName.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.facilityImage);

        holder.booknowBtn.setOnClickListener(v -> {
            Log.d("BookingAdapter", "Book button clicked");
            Dialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.booking_dialog);

            TextView facilityName = dialog.findViewById(R.id.facility_name);
            ImageView facilityImage = dialog.findViewById(R.id.facility_image);
            Spinner timeSlotSpinner = dialog.findViewById(R.id.booking_time);
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

            // Populate the Spinner with time slots from resources
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.timeslot, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeSlotSpinner.setAdapter(adapter);


            confirm.setOnClickListener(v1 -> {
                Log.d("BookingAdapter", "Confirm button clicked");
                // Retrieve the selected time slot from the Spinner
                String selectedTimeSlot = timeSlotSpinner.getSelectedItem().toString();
                BookingItem bookingItem = new BookingItem(model.getName(), model.getImage(), model.getPrice(), selectedTimeSlot, currentDate);
                timeslotRef.child(model.getTime()).child(model.getDate()).child("booked").setValue(true);
                facilitiesRef.child(model.getName()).child("timeslot").child(model.getTime()).child(model.getDate()).child("booked").setValue(true);
                facilitiesRef.child(model.getName()).child("timeslot").child(model.getTime()).child(model.getDate()).child("bookedBy").setValue(user.getUid());
                dialog.dismiss();
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
        Button booknowBtn;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
            booknowBtn = itemView.findViewById(R.id.booknowBtn);
        }
    }
}

