package com.iicportal.adaptor;

import android.content.Context;
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
import com.iicportal.fragments.AddToCartDialogFragment;
import com.iicportal.models.FoodItem;

public class MenuItemAdaptor extends FirebaseRecyclerAdapter<FoodItem, MenuItemAdaptor.MenuItemViewHolder> {
    Context context;

    FirebaseDatabase database;
    DatabaseReference cartRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FragmentManager childFragmentManager;

    public MenuItemAdaptor(FirebaseRecyclerOptions<FoodItem> options, Context context, FragmentManager childFragmentManager) {
        super(options);
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.cartRef = database.getReference("carts/");
        this.cartRef.keepSynced(true);
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.childFragmentManager = childFragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position, @NonNull FoodItem model) {
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());
        holder.price.setText(String.format("RM %.2f", model.getPrice()));
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            AddToCartDialogFragment addToCartDialogFragment = new AddToCartDialogFragment(model, getRef(position).getKey());
            addToCartDialogFragment.show(childFragmentManager, "AddToCartDialogFragment");
        });
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

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
        }
    }
}