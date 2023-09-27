package com.iicportal.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iicportal.R;
import com.iicportal.models.Facilities;

public class FacilityAdapter extends FirebaseRecyclerAdapter<Facilities,FacilityAdapter.FacilityViewHolder> {
    Context context;

    SharedPreferences sharedPreferences;
    public FacilityAdapter(@NonNull FirebaseRecyclerOptions<Facilities> options, Context context) {
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
    protected void onBindViewHolder(@NonNull FacilityViewHolder holder, int position, @NonNull Facilities model) {
        holder.facilityName.setText(model.getName());
        Glide.with(context).load(model.getImage()).into(holder.facilityImage);
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

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facility_name);
            facilityImage = itemView.findViewById(R.id.facility_image);
        }
    }
}

