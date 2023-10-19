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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.fragments.BookingDialogFragment;
import com.iicportal.models.BookingItem;

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
        this.database = FirebaseDatabase.getInstance();
        this.bookingRef = database.getReference("bookings");
        this.bookingRef.keepSynced(true);
        this.facilitiesRef = database.getReference("facilities/facility/");
        this.facilitiesRef.keepSynced(true);
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
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

    //create a function where we can reset the bookings for all facility when it is 12am or not equal to the current date
//    public void resetBookings() {
//        // Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
//
//        DatabaseReference userBookingsRef = bookingRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        userBookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot userBookingsSnapshot) {
//                for (DataSnapshot bookingSnapshot : userBookingsSnapshot.getChildren()) {
//                    String bookingDate = bookingSnapshot.child("selectedDate").getValue(String.class);
//                    if (bookingDate != null && !bookingDate.equals(currentDate)) {
//                        // Remove the booking entry from the user's history
//                        bookingSnapshot.getRef().removeValue();
//                    } else {
//                        Log.d("BookingAdapter", "Booking date is equal to current date");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("BookingAdapter", "Database error: " + databaseError.getMessage());
//            }
//        });
//    }


    @Override
    protected void onBindViewHolder(@NonNull FacilityViewHolder holder, int position, @NonNull BookingItem model) {
        holder.facilityName.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.facilityImage);
        //Call the resetBookings function
        //resetBookings();

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
        ImageView historyBtnIcon;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
            booknowBtn = itemView.findViewById(R.id.booknowBtn);
            historyBtnIcon = itemView.findViewById(R.id.historyBtnIcon);
        }
    }
}

