package com.iicportal.adaptor;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.CartItem;

public class CartItemAdaptor extends FirebaseRecyclerAdapter<CartItem, CartItemAdaptor.CartItemViewHolder> {
    Context context;

    FirebaseDatabase database;
    DatabaseReference cartRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public CartItemAdaptor(@NonNull FirebaseRecyclerOptions<CartItem> options, Context context) {
        super(options);
        this.context = context;
        database = MainActivity.database;
        this.cartRef = database.getReference("carts/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartItemViewHolder holder, int position, @NonNull CartItem model) {
        int pos = position;

        if (pos >= getItemCount()) {
            return;
        }

        holder.name.setText(model.getName());
        holder.price.setText(String.format("RM %.2f", model.getPrice()));
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);
        holder.quantity.setText(String.valueOf(model.getQuantity()));

        holder.plusBtn.setOnClickListener(v -> {
            getRef(pos).child("quantity").setValue(Integer.parseInt(holder.quantity.getText().toString()) + 1);
        });

        holder.minusBtn.setOnClickListener(v -> {
            if (Integer.parseInt(holder.quantity.getText().toString()) > 1) {
                getRef(pos).child("quantity").setValue(Integer.parseInt(holder.quantity.getText().toString()) - 1);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            Log.d("CartItemAdaptor", "Delete button clicked");

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Remove Item");
            builder.setMessage("Are you sure you want to remove this item from your cart?");
            builder.setPositiveButton("OK", (dialog, which) -> {
                getRef(pos).removeValue();
                dialog.dismiss();
                Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        holder.selected.setChecked(model.getSelected());

        holder.selected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getRef(pos).child("selected").setValue(true);
            } else {
                getRef(pos).child("selected").setValue(false);
            }
        });
    }

    public void onDataChanged() {
        super.onDataChanged();
        Log.d("CartItemAdaptor", "Cart items count: " + super.getItemCount());

        notifyDataSetChanged();
    }

    public int getSelectedItemsCount() {
        int count = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getSelected()) {
                count++;
            }
        }

        return count;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartItemViewHolder(view);
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price, quantity;
        ImageView image;
        TextView plusBtn, minusBtn;
        ImageButton deleteBtn;
        CheckBox selected;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            quantity = itemView.findViewById(R.id.quantity);
            plusBtn = itemView.findViewById(R.id.plus_button);
            minusBtn = itemView.findViewById(R.id.minus_button);
            deleteBtn = itemView.findViewById(R.id.delete_button);
            selected = itemView.findViewById(R.id.selected);
        }
    }
}