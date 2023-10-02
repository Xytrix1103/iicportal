package com.iicportal.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.adaptor.CheckoutItemAdaptor;
import com.iicportal.models.CartItem;

import java.text.SimpleDateFormat;

public class OrderDetailsActivity extends AppCompatActivity {
    Context context = this;

    FirebaseDatabase database;
    DatabaseReference orderRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    RecyclerView recyclerView;
    CheckoutItemAdaptor itemAdaptor;
    CardView itemsCard;

    TextView paymentMethod;
    TextView orderOption;
    TextView orderID;
    TextView orderTotal;
    TextView takeawayFee;
    TextView total;
    ImageView backBtnIcon;

    TextView sentToKitchenDateTime, readyForPickupDateTime, completedDateTime, status;
    RelativeLayout sentToKitchenBody, readyForPickupBody, completedBody;

    LinearProgressIndicator progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        context = this;

        String orderId = getIntent().getStringExtra("orderID");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("orders/").child(orderId);
        orderRef.keepSynced(true);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.suppressLayout(true);
        recyclerView.setFocusable(false);
        recyclerView.setClickable(false);

        FirebaseRecyclerOptions<CartItem> options = new FirebaseRecyclerOptions.Builder<CartItem>()
                .setQuery(orderRef.child("items"), CartItem.class)
                .build();

        itemAdaptor = new CheckoutItemAdaptor(options, context);
        recyclerView.setAdapter(itemAdaptor);
        itemsCard = findViewById(R.id.itemsCard);

        paymentMethod = findViewById(R.id.paymentMethod);
        orderOption = findViewById(R.id.orderOption);
        orderID = findViewById(R.id.orderID);
        orderTotal = findViewById(R.id.orderTotal);
        takeawayFee = findViewById(R.id.takeawayFee);
        total = findViewById(R.id.total);

        sentToKitchenDateTime = findViewById(R.id.sentToKitchenDateTime);
        readyForPickupDateTime = findViewById(R.id.readyForPickupDateTime);
        completedDateTime = findViewById(R.id.completedDateTime);

        sentToKitchenBody = findViewById(R.id.sentToKitchenBody);
        readyForPickupBody = findViewById(R.id.readyForPickupBody);
        completedBody = findViewById(R.id.completedBody);

        status = findViewById(R.id.status);

        progressBar.setIndicatorColor(context.getResources().getColor(R.color.light_green_800));
        progressBar.setTrackColor(context.getResources().getColor(R.color.light_green_200));

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("timestamp").exists()) {
                    sentToKitchenDateTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(Long.parseLong(snapshot.child("timestamp").getValue().toString())));
                    sentToKitchenBody.setVisibility(TextView.VISIBLE);
                    status.setText("PREPARING");
                    status.setTextColor(context.getResources().getColor(R.color.black));
                    progressBar.setProgress(1);
                } else {
                    sentToKitchenBody.setVisibility(TextView.GONE);
                }

                if (snapshot.child("readyTimestamp").exists()) {
                    readyForPickupDateTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(Long.parseLong(snapshot.child("readyTimestamp").getValue().toString())));
                    readyForPickupBody.setVisibility(TextView.VISIBLE);
                    status.setText("READY");
                    status.setTextColor(context.getResources().getColor(R.color.black));
                    progressBar.setProgress(2);
                } else {
                    readyForPickupBody.setVisibility(TextView.GONE);
                }

                if (snapshot.child("completedTimestamp").exists()) {
                    completedDateTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(Long.parseLong(snapshot.child("completedTimestamp").getValue().toString())));
                    completedBody.setVisibility(TextView.VISIBLE);

                    status.setText("COMPLETED");
                    status.setTextColor(context.getResources().getColor(R.color.light_green_800));
                    progressBar.setProgress(3);
                } else {
                    completedBody.setVisibility(TextView.GONE);
                }

                orderID.setText(snapshot.getKey());
                orderTotal.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("total").getValue().toString())));
                takeawayFee.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("takeawayFee").getValue().toString())));
                total.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("total").getValue().toString()) + Double.parseDouble(snapshot.child("takeawayFee").getValue().toString())));

                database.getReference("ecanteen/paymentmethods").child(snapshot.child("paymentMethod").getValue().toString()).child("method").get().addOnSuccessListener(dataSnapshot -> {
                    paymentMethod.setText(dataSnapshot.getValue().toString());
                });

                database.getReference("ecanteen/orderoptions").child(snapshot.child("orderOption").getValue().toString()).child("option").get().addOnSuccessListener(dataSnapshot -> {
                    orderOption.setText(dataSnapshot.getValue().toString());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        backBtnIcon = findViewById(R.id.backBtnIcon);

        backBtnIcon.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdaptor.stopListening();
    }
}
