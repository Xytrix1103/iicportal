package com.iicportal.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Date;

public class BookingAdapter extends FirebaseRecyclerAdapter<BookingItem, BookingAdapter.BookingViewHolder> {

    Context context;

    FirebaseDatabase database;
    DatabaseReference timeslotRef;
    DatabaseReference facilitiesRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    public BookingAdapter(@NonNull FirebaseRecyclerOptions<BookingItem> options, Context context) {
        super(options);
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.timeslotRef = database.getReference("facilities/timeslot/");
        this.timeslotRef.keepSynced(true);
        this.facilitiesRef = database.getReference("facilities/facility/");
        this.facilitiesRef.keepSynced(true);
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingViewHolder holder, int position, @NonNull BookingItem model) {
        holder.facilityName.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.facilityImage);
        holder.time.setText(model.getTime());
        holder.date.setText(model.getDate());
        holder.price.setText(String.valueOf(model.getPrice()));

        // Get the current date
        Date currentDate = new Date();

        // Format the current date as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String bookingDate = dateFormat.format(currentDate);

        holder.book.setOnClickListener(v -> {
            Log.d("BookingAdapter", "Book button clicked");
            Dialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.booking_dialog);

            TextView facilityName = dialog.findViewById(R.id.facility_name);
            ImageView facilityImage = dialog.findViewById(R.id.facility_image);
            TextView time = dialog.findViewById(R.id.booking_time);
            TextView date = dialog.findViewById(R.id.booking_date);
            TextView price = dialog.findViewById(R.id.price);
            Button confirm = dialog.findViewById(R.id.confirmBtn);
            Button cancel = dialog.findViewById(R.id.cancelBtn);

            facilityName.setText(model.getName());
            Glide.with(context).load(model.getImage()).into(facilityImage);
            time.setText(model.getTime());
            date.setText(model.getDate());
            price.setText(String.valueOf(model.getPrice()));


            confirm.setOnClickListener(v1 -> {
                Log.d("BookingAdapter", "Confirm button clicked");
                BookingItem bookingItem = new BookingItem(model.getName(), model.getImage(), model.getPrice(), model.getTime(), model.getDate());
                timeslotRef.child(model.getTime()).child(model.getDate()).child("booked").setValue(true);
                facilitiesRef.child(model.getName()).child("timeslot").child(model.getTime()).child(model.getDate()).child("booked").setValue(true);
                facilitiesRef.child(model.getName()).child("timeslot").child(model.getTime()).child(model.getDate()).child("bookedBy").setValue(user.getUid());
                dialog.dismiss();
            });

            cancel.setOnClickListener(v1 -> {
                Log.d("BookingAdapter", "Cancel button clicked");
                dialog.dismiss();
            });
        });

    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_dialog, parent, false);
        return new BookingViewHolder(view);
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName;
        ImageView facilityImage;
        TextView time;
        TextView date;
        TextView price;
        Button book;
        Button confirm;
        Button cancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
            time = itemView.findViewById(R.id.booking_time);
            date = itemView.findViewById(R.id.booking_date);
            price = itemView.findViewById(R.id.price);
            book = itemView.findViewById(R.id.booknowBtn);
            confirm = itemView.findViewById(R.id.confirmBtn);
            cancel = itemView.findViewById(R.id.cancelBtn);
        }
    }
}
