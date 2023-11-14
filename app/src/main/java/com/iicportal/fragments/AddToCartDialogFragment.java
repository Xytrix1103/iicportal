package com.iicportal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.CartItem;
import com.iicportal.models.FoodItem;

public class AddToCartDialogFragment extends BottomSheetDialogFragment {
    FoodItem foodItem;
    FirebaseDatabase database;
    DatabaseReference cartRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String key;

    public AddToCartDialogFragment() {
    }

    
    public AddToCartDialogFragment(FoodItem foodItem, String key) {
        this.foodItem = foodItem;
        database = MainActivity.database;
        this.cartRef = database.getReference("carts/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        this.key = key;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_menu_item_dialog, container, false);

        TextView title = view.findViewById(R.id.order_menu_item_dialog_title);
        TextView description = view.findViewById(R.id.order_menu_item_dialog_description);
        TextView price = view.findViewById(R.id.order_menu_item_dialog_price);
        ImageView image = view.findViewById(R.id.order_menu_item_dialog_image);
        TextView quantity = view.findViewById(R.id.order_menu_item_dialog_quantity);
        TextView plusBtn = view.findViewById(R.id.order_menu_item_dialog_plus_button);
        TextView minusBtn = view.findViewById(R.id.order_menu_item_dialog_minus_button);
        Button addToCartBtn = view.findViewById(R.id.order_menu_item_dialog_add_button);
        CartItem cartItem = new CartItem(foodItem);

        title.setText(cartItem.getName());
        description.setText(cartItem.getDescription());
        price.setText(String.format("RM %.2f", cartItem.getPrice()));
        Glide.with(image.getContext()).load(cartItem.getImage()).placeholder(R.drawable.baseline_image_placeholdeer).into(image);
        quantity.setText(String.valueOf(cartItem.getQuantity()));

        plusBtn.setOnClickListener(v1 -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            quantity.setText(String.valueOf(cartItem.getQuantity()));
        });

        minusBtn.setOnClickListener(v1 -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                quantity.setText(String.valueOf(cartItem.getQuantity()));
            }
        });

        addToCartBtn.setOnClickListener(v1 -> {
            cartRef.child(user.getUid()).child(key).child("quantity").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() != null) {
                        cartRef.child(user.getUid()).child(key).child("quantity").setValue(Integer.parseInt(quantity.getText().toString()) + Integer.parseInt(task.getResult().getValue().toString()));
                    } else {
                        cartItem.setSelected(false);
                        cartRef.child(user.getUid()).child(key).setValue(cartItem);
                    }

                    dismiss();
                } else {
                    Log.e("AddToCartDialogFragment", "Error getting data", task.getException());
                }
            });
        });

        getDialog().setOnDismissListener(dialog -> {
            cartItem.resetQuantity();
        });

        return view;
    }
}
