package com.iicportal.activity;

import android.content.Context;
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
import com.iicportal.adaptor.OrderAdaptor;
import com.iicportal.models.Order;

public class OrderHistoryActivity extends AppCompatActivity {

    Context context = this;

    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference ordersRef;

    RecyclerView ordersRecyclerView;
    OrderAdaptor orderAdaptor;

    TextView noOrderText;

    ImageView backBtnIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("orders/");
        ordersRef.keepSynced(true);

        backBtnIcon = findViewById(R.id.backBtnIcon);
        backBtnIcon.setOnClickListener(v -> finish());

        ordersRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setLifecycleOwner(this)
                .setQuery(ordersRef.orderByChild("uid").equalTo(user.getUid()), Order.class)
                .build();

        orderAdaptor = new OrderAdaptor(options, context);
        ordersRecyclerView.setAdapter(orderAdaptor);

        noOrderText = findViewById(R.id.orderHistoryEmptyText);

        orderAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (orderAdaptor.getItemCount() == 0) {
                    noOrderText.setVisibility(TextView.VISIBLE);
                } else {
                    noOrderText.setVisibility(TextView.GONE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        orderAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        orderAdaptor.stopListening();
    }
}