package com.iicportal.adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.iicportal.fragments.BookingDialogFragment;
import com.iicportal.models.BookingItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FacilityAdaptor extends FirebaseRecyclerAdapter<BookingItem, FacilityAdaptor.FacilityViewHolder> {
    Context context;
    FirebaseDatabase database;
    DatabaseReference bookingRef;
    DatabaseReference facilitiesRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    SharedPreferences sharedPreferences;
    FragmentManager childFragmentManager;


    public FacilityAdaptor(@NonNull FirebaseRecyclerOptions<BookingItem> options, Context context, FragmentManager childFragmentManager) {
        super(options);
        this.context = context;
        database = MainActivity.database;
        this.bookingRef = database.getReference("bookings");
        this.facilitiesRef = database.getReference("facilities/facility/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
        this.childFragmentManager = childFragmentManager;
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
            BookingDialogFragment bookingDialogFragment = new BookingDialogFragment(model, context, getRef(position).getKey());
            bookingDialogFragment.show(childFragmentManager, "BookingDialogFragment");
        });
    }

    @NonNull
    @Override
    public FacilityAdaptor.FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

