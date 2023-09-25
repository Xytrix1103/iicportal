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
import com.iicportal.activity.CartActivity;
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

        this.database = FirebaseDatabase.getInstance();
        this.cartRef = database.getReference("carts/");
        this.cartRef.keepSynced(true);
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
    }

    public void onDataChanged() {
        super.onDataChanged();
        TextView cartEmptyText = ((CartActivity) context).findViewById(R.id.cartEmptyText);
        TextView cartTotalPrice = ((CartActivity) context).findViewById(R.id.cartTotalPrice);

        if (super.getItemCount() == 0) {
            Log.d("CartActivity", "Cart is empty");
            ((CartActivity) context).findViewById(R.id.cartCheckoutBtn).setEnabled(false);
            cartEmptyText.setVisibility(TextView.VISIBLE);
        } else {
            Log.d("CartActivity", "Cart is not empty");
            ((CartActivity) context).findViewById(R.id.cartCheckoutBtn).setEnabled(true);
            cartEmptyText.setVisibility(TextView.GONE);
        }

        double total = 0;
        for (int i = 0; i < super.getItemCount(); i++) {
            if(super.getItem(i).getSelected()) {
                Log.d("CartActivity", "Item " + i + " is selected");
                Log.d("CartActivity", "Item " + i + " price: " + super.getItem(i).getPrice());
                Log.d("CartActivity", "Item " + i + " quantity: " + super.getItem(i).getQuantity());
                total += super.getItem(i).getPrice() * super.getItem(i).getQuantity();
            }
        }

        Log.d("CartActivity", "Total price: " + total);
        ((CartActivity) context).totalPrice = total;
        cartTotalPrice.setText(String.format("RM %.2f", total));
    }

    @Override
    protected void onBindViewHolder(@NonNull CartItemViewHolder holder, int position, @NonNull CartItem model) {
        holder.name.setText(model.getName());
        holder.price.setText(String.format("RM %.2f", model.getPrice()));
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);
        holder.quantity.setText(String.valueOf(model.getQuantity()));

        holder.plusBtn.setOnClickListener(v -> {
            getRef(position).child("quantity").setValue(Integer.parseInt(holder.quantity.getText().toString()) + 1);
        });

        holder.minusBtn.setOnClickListener(v -> {
            if (Integer.parseInt(holder.quantity.getText().toString()) > 1) {
                getRef(position).child("quantity").setValue(Integer.parseInt(holder.quantity.getText().toString()) - 1);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            Log.d("CartItemAdaptor", "Delete button clicked");

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Remove Item");
            builder.setMessage("Are you sure you want to remove this item from your cart?");
            builder.setPositiveButton("OK", (dialog, which) -> {
                getRef(position).removeValue();
                dialog.dismiss();
                Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        holder.selected.setChecked(model.getSelected());

        holder.selected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getRef(position).child("selected").setValue(true);
            } else {
                getRef(position).child("selected").setValue(false);
            }
        });
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