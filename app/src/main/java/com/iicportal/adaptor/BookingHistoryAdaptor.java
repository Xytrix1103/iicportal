package com.iicportal.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.models.BookingItem;

public class BookingHistoryAdaptor extends FirebaseRecyclerAdapter<BookingItem, BookingHistoryAdaptor.BookingViewHolder> {
    Context context;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference bookingRef;

    public BookingHistoryAdaptor(FirebaseRecyclerOptions<BookingItem> options, Context context) {
        super(options);
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        // Get the current user's ID
        String userId = auth.getCurrentUser().getUid();
        this.bookingRef = FirebaseDatabase.getInstance().getReference("bookings");
        this.bookingRef.keepSynced(true);
    }

    @Override
    protected void onBindViewHolder(BookingViewHolder holder, int position, BookingItem model) {
        holder.facilityName.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.facilityImage);
        holder.timeslot.setText(model.getTime());

        // Set the payment method icon (you can modify this logic as needed)
        holder.paymentMethodIcon.setImageResource(R.drawable.tngewallet);

        holder.date.setText(model.getDate());

        holder.status.setText(model.getStatus());
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.b_history, parent, false);
        return new BookingViewHolder(view);
    }

    public void onDataChanged() {
        super.onDataChanged();
        Log.d("BookingHistoryAdaptor", "onDataChanged: ");
        notifyDataSetChanged();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName,status, date, total, timeslot;
        ImageView paymentMethodIcon, facilityImage;

        public BookingViewHolder(View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
            total = itemView.findViewById(R.id.total);
            paymentMethodIcon = itemView.findViewById(R.id.payment_method_icon);
            timeslot = itemView.findViewById(R.id.timeslot);

        }
    }
}
