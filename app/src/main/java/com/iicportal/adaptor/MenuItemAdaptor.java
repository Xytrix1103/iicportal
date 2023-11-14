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
import com.iicportal.activity.EditFoodItemActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.fragments.AddToCartDialogFragment;
import com.iicportal.models.FoodItem;

public class MenuItemAdaptor extends FirebaseRecyclerAdapter<FoodItem, MenuItemAdaptor.MenuItemViewHolder> {
    Context context;

    FirebaseDatabase database;
    DatabaseReference cartRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FragmentManager childFragmentManager;
    boolean isEdit;

    public MenuItemAdaptor(FirebaseRecyclerOptions<FoodItem> options, Context context, FragmentManager childFragmentManager, boolean isEdit) {
        super(options);
        this.context = context;
        database = MainActivity.database;
        this.cartRef = database.getReference("carts/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.childFragmentManager = childFragmentManager;
        this.isEdit = isEdit;
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position, @NonNull FoodItem model) {
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());
        holder.price.setText(String.format("RM %.2f", model.getPrice()));
        Glide.with(holder.image.getContext()).load(model.getImage()).placeholder(R.drawable.baseline_image_placeholdeer).into(holder.image);

        //get role from shared preferences
        //if role is admin, show edit button
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");

        if(isEdit) {
            if (role.equals("Admin") || role.equals("Vendor")) {
                holder.editBtn.setVisibility(View.VISIBLE);
                holder.editBtn.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, EditFoodItemActivity.class).putExtra("key", getRef(position).getKey()));
                });
            } else {
                holder.editBtn.setVisibility(View.GONE);
            }
        } else {
            holder.itemView.setOnClickListener(v -> {
                AddToCartDialogFragment addToCartDialogFragment = new AddToCartDialogFragment(model, getRef(position).getKey());
                addToCartDialogFragment.show(childFragmentManager, "AddToCartDialogFragment");
            });
            holder.editBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d("MenuItemAdaptor", "Data changed");
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        TextView price;
        ImageView editBtn;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            editBtn = itemView.findViewById(R.id.editMenuItemBtn);
        }
    }
}