package com.iicportal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.CartItemAdaptor;
import com.iicportal.models.CartItem;

public class CartActivity extends AppCompatActivity {

    Context context = this;
    RecyclerView cartRecyclerView;

    FirebaseAuth mAuth;
    FirebaseUser user;

    CartItemAdaptor cartItemAdaptor;

    FirebaseDatabase database;
    DatabaseReference cartRef;

    TextView cartEmptyText, cartTotalPrice;

    Button checkoutBtn;

    ImageView backBtnIcon;

    public double totalPrice = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        context = this;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        cartRef = database.getReference("carts/");
        cartRef.keepSynced(true);
        cartEmptyText = findViewById(R.id.cartEmptyText);
        cartTotalPrice = findViewById(R.id.cartTotalPrice);

        backBtnIcon = findViewById(R.id.backBtnIcon);
        checkoutBtn = findViewById(R.id.cartCheckoutBtn);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CartItem> options = new FirebaseRecyclerOptions.Builder<CartItem>()
                .setQuery(cartRef.child(user.getUid()), CartItem.class)
                .setLifecycleOwner(this)
                .build();

        cartItemAdaptor = new CartItemAdaptor(options, context);
        cartRecyclerView.setAdapter(cartItemAdaptor);

        cartItemAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (cartItemAdaptor.getItemCount() == 0) {
                    cartEmptyText.setVisibility(TextView.VISIBLE);
                    checkoutBtn.setEnabled(false);
                } else {
                    cartEmptyText.setVisibility(TextView.GONE);
                    if (cartItemAdaptor.getSelectedItemsCount() == 0) {
                        checkoutBtn.setEnabled(false);
                    } else {
                        checkoutBtn.setEnabled(true);
                    }
                }

                double total = 0;
                for (int i = 0; i < cartItemAdaptor.getItemCount(); i++) {
                    if(cartItemAdaptor.getItem(i).getSelected()) {
                        total += cartItemAdaptor.getItem(i).getPrice() * cartItemAdaptor.getItem(i).getQuantity();
                    }
                }

                totalPrice = total;
                cartTotalPrice.setText(String.format("RM %.2f", total));
            }
        });

        backBtnIcon.setOnClickListener(v -> {
            finish();
        });

        checkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        cartItemAdaptor.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartItemAdaptor.startListening();
    }
}
