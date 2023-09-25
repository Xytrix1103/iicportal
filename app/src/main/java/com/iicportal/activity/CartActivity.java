package com.iicportal.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    SharedPreferences sharedPreferences;

    TextView cartEmptyText, cartTotalPrice;

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

        sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        cartEmptyText = findViewById(R.id.cartEmptyText);
        cartTotalPrice = findViewById(R.id.cartTotalPrice);

        backBtnIcon = findViewById(R.id.backBtnIcon);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CartItem> options = new FirebaseRecyclerOptions.Builder<CartItem>()
                .setQuery(cartRef.child(user.getUid()), CartItem.class)
                .setLifecycleOwner(this)
                .build();

        cartItemAdaptor = new CartItemAdaptor(options, context);
        cartRecyclerView.setAdapter(cartItemAdaptor);

        backBtnIcon.setOnClickListener(v -> {
            finish();
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
