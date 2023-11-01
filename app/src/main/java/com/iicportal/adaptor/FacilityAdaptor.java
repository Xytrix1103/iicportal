package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
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
import com.iicportal.activity.EditFacilityActivity;
import com.iicportal.activity.MainActivity;
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
    FragmentManager childfragmentManager;
    boolean isEdit;


    public FacilityAdaptor(@NonNull FirebaseRecyclerOptions<BookingItem> options, Context context, FragmentManager childfragmentManager, boolean isEdit) {
        super(options);
        this.context = context;
        database = MainActivity.database;
        this.bookingRef = database.getReference("bookings");
        this.facilitiesRef = database.getReference("facilities/facility/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
        this.childfragmentManager = childfragmentManager;
        this.isEdit = isEdit;
    }

    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull FacilityViewHolder holder, int position, @NonNull BookingItem model) {
        holder.facilityName.setText(model.getName());
        Glide.with(holder.facilityImage.getContext()).load(model.getImage()).into(holder.facilityImage);

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");

        if (isEdit) {
            if (!role.equals("Admin") || !role.equals("Vendor")) {
                holder.editFacility.setVisibility(View.VISIBLE);
                holder.booknowBtn.setVisibility(View.GONE);
                holder.editFacility.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, EditFacilityActivity.class).putExtra("key", getRef(position).getKey()));
                });
            } else {
                holder.editFacility.setVisibility(View.GONE);
            }
        } else {
            holder.booknowBtn.setOnClickListener(v -> {
                Log.d("BookingAdapter", "Book button clicked");
                //also pass facility name
                BookingDialogFragment bookingDialogFragment = new BookingDialogFragment(model, context, getRef(position).getKey(), model.getName());
                bookingDialogFragment.show(childfragmentManager, "BookingDialogFragment");
            });
            holder.editFacility.setVisibility(View.GONE);
        }
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
        TextView editFacility;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
            booknowBtn = itemView.findViewById(R.id.booknowBtn);
            historyBtnIcon = itemView.findViewById(R.id.historyBtnIcon);
            editFacility = itemView.findViewById(R.id.editFacilityBtn);
        }
    }
}

