package com.iicportal.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.iicportal.models.Order;
import com.iicportal.models.OrderOption;
import com.iicportal.models.PaymentMethod;

import java.text.SimpleDateFormat;

public class OrderDetailsActivity extends AppCompatActivity {
    Context context = this;

    FirebaseDatabase database;
    DatabaseReference orderRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    RecyclerView recyclerView;
    CheckoutItemAdaptor itemAdaptor;
    TextView orderedAtDate, orderedAtTime;

    TextView paymentMethod;
    TextView orderOption;
    TextView orderID;
    TextView orderTotal;
    TextView takeawayFee;
    TextView total;
    ImageView backBtnIcon;
    Button button;

    TextView sentToKitchenDateTime, readyForPickupDateTime, completedDateTime, status, headerText;
    RelativeLayout sentToKitchenBody, readyForPickupBody, completedBody, takeawayFeesBody;

    LinearProgressIndicator progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        context = this;

        String orderId = getIntent().getStringExtra("orderID");

        mAuth = MainActivity.mAuth;
        user = MainActivity.user;

        database = MainActivity.database;
        orderRef = database.getReference("orders/").child(orderId);

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

        headerText = findViewById(R.id.headerText);
        progressBar = findViewById(R.id.progressBar);
        paymentMethod = findViewById(R.id.payment_method);
        orderOption = findViewById(R.id.collect_by);
        orderID = findViewById(R.id.orderID);
        orderTotal = findViewById(R.id.subtotal);
        takeawayFee = findViewById(R.id.takeawayFees);
        total = findViewById(R.id.total);
        takeawayFeesBody = findViewById(R.id.takeawayFeesBody);
        sentToKitchenDateTime = findViewById(R.id.sentToKitchenDateTime);
        readyForPickupDateTime = findViewById(R.id.readyForPickupDateTime);
        completedDateTime = findViewById(R.id.completedDateTime);
        sentToKitchenBody = findViewById(R.id.sentToKitchenBody);
        readyForPickupBody = findViewById(R.id.readyForPickupBody);
        completedBody = findViewById(R.id.completedBody);
        status = findViewById(R.id.status);
        orderedAtDate = findViewById(R.id.orderedAtDate);
        orderedAtTime = findViewById(R.id.orderedAtTime);
        button = findViewById(R.id.button);
        button.setVisibility(Button.GONE);

        progressBar.setIndicatorColor(context.getResources().getColor(R.color.light_green_800));
        progressBar.setTrackColor(context.getResources().getColor(R.color.light_green_200));


        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                headerText.setText(snapshot.getValue(Order.class).getOrderID());

                if (snapshot.child("timestamp").exists()) {
                    sentToKitchenDateTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(
                            Long.parseLong(snapshot.child("timestamp").getValue().toString())));
                    sentToKitchenBody.setVisibility(TextView.VISIBLE);
                    status.setText("PREPARING");
                    status.setTextColor(context.getResources().getColor(R.color.black));
                    progressBar.setProgress(1);
                } else {
                    sentToKitchenBody.setVisibility(TextView.GONE);
                }

                if (snapshot.child("ready").exists() && Boolean.TRUE.equals(snapshot.child("ready").getValue(Boolean.class))) {
                    readyForPickupDateTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(
                            Long.parseLong(snapshot.child("readyTimestamp").getValue().toString())));
                    readyForPickupBody.setVisibility(TextView.VISIBLE);
                    status.setText("READY");
                    status.setTextColor(context.getResources().getColor(R.color.black));
                    progressBar.setProgress(2);
                } else {
                    readyForPickupBody.setVisibility(TextView.GONE);
                }

                if (snapshot.child("completed").exists() && Boolean.TRUE.equals(snapshot.child("completed").getValue(Boolean.class))) {
                    completedDateTime.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format
                            (Long.parseLong(snapshot.child("completedTimestamp").getValue().toString())));
                    completedBody.setVisibility(TextView.VISIBLE);

                    status.setText("COMPLETED");
                    status.setTextColor(context.getResources().getColor(R.color.light_green_800));
                    progressBar.setProgress(3);
                } else {
                    completedBody.setVisibility(TextView.GONE);
                }

                orderID.setText(snapshot.getValue(Order.class).getOrderID());
                orderedAtDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(Long.parseLong(snapshot.child("timestamp").getValue().toString())));
                orderedAtTime.setText(new SimpleDateFormat("hh:mm a").format(Long.parseLong(snapshot.child("timestamp").getValue().toString())));
                orderTotal.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("total").getValue().toString())));
                if(Double.parseDouble(snapshot.child("takeawayFee").getValue().toString()) > 0) {
                    takeawayFeesBody.setVisibility(TextView.VISIBLE);
                    takeawayFee.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("takeawayFee").getValue().toString())));
                } else {
                    takeawayFeesBody.setVisibility(TextView.GONE);
                }
                takeawayFee.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("takeawayFee").getValue().toString())));
                total.setText(String.format("RM %.2f", Double.parseDouble(snapshot.child("total").getValue().toString()) + Double.parseDouble(snapshot.child("takeawayFee").getValue().toString())));

                paymentMethod.setText(snapshot.child("paymentMethod").getValue(PaymentMethod.class).getMethod());
                orderOption.setText(snapshot.child("orderOption").getValue(OrderOption.class).getOption());
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
