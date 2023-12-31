package com.iicportal.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.iicportal.adaptor.CheckoutItemAdaptor;
import com.iicportal.adaptor.OrderOptionAdaptor;
import com.iicportal.adaptor.PaymentMethodAdaptor;
import com.iicportal.models.CartItem;
import com.iicportal.models.Order;
import com.iicportal.models.OrderOption;
import com.iicportal.models.PaymentMethod;

public class CheckoutActivity extends AppCompatActivity {
    Context context = this;

    FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference cartRef;
    DatabaseReference paymentRef;
    DatabaseReference orderOptionsRef;

    RecyclerView paymentMethodRecyclerView;
    RecyclerView itemsRecyclerView;
    RecyclerView orderOptionsRecyclerView;
    CheckoutItemAdaptor checkoutItemAdaptor;

    PaymentMethodAdaptor paymentMethodAdapter;
    OrderOptionAdaptor orderOptionAdaptor;

    PaymentMethod paymentMethod;
    OrderOption orderOption;

    double orderTotal, takeawayFee, total;

    int quantity;

    TextView orderTotalTextView, takeawayFeeTextView, totalTextView;

    Button checkoutBtn;

    ImageView backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = MainActivity.mAuth;
        user = MainActivity.user;

        database = MainActivity.database;
        cartRef = database.getReference("carts/");
        paymentRef = database.getReference("ecanteen/paymentmethods/");
        orderOptionsRef = database.getReference("ecanteen/orderoptions/");

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<CartItem> options = new FirebaseRecyclerOptions.Builder<CartItem>()
                .setQuery(cartRef.child(user.getUid()).orderByChild("selected").equalTo(true), CartItem.class)
                .build();

        checkoutItemAdaptor = new CheckoutItemAdaptor(options, context);
        itemsRecyclerView.setAdapter(checkoutItemAdaptor);

        paymentMethodRecyclerView = findViewById(R.id.paymentMethodRecyclerView);
        paymentMethodRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<PaymentMethod> paymentMethodOptions = new FirebaseRecyclerOptions.Builder<PaymentMethod>()
                .setQuery(paymentRef, PaymentMethod.class)
                .build();

        paymentMethodAdapter = new PaymentMethodAdaptor(paymentMethodOptions, context);
        paymentMethodRecyclerView.setAdapter(paymentMethodAdapter);

        orderOptionsRecyclerView = findViewById(R.id.dineInTakeAwayRecyclerView);
        orderOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        FirebaseRecyclerOptions<OrderOption> orderOptionsOptions = new FirebaseRecyclerOptions.Builder<OrderOption>()
                .setQuery(orderOptionsRef, OrderOption.class)
                .build();

        orderOptionAdaptor = new OrderOptionAdaptor(orderOptionsOptions, context);
        orderOptionsRecyclerView.setAdapter(orderOptionAdaptor);

        orderTotalTextView = findViewById(R.id.orderTotal);
        takeawayFeeTextView = findViewById(R.id.takeawayFee);
        totalTextView = findViewById(R.id.total);

        checkoutItemAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("CheckoutActivity", "onChanged: " + checkoutItemAdaptor.getItemCount());
                updatePrices();
            }
        });

        orderOptionAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("CheckoutActivity", "onChanged: " + orderOptionAdaptor.getItemCount());
                updatePrices();
            }
        });

        checkoutBtn = findViewById(R.id.checkoutBtn);

        checkoutBtn.setOnClickListener(v -> {
            Order order = new Order("", user.getUid(), System.currentTimeMillis(), null, null, paymentMethod, orderOption, orderTotal, takeawayFee, total, "PREPARING", checkoutItemAdaptor.getItems());

            database.getReference("orders/").limitToLast(1).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String lastOrderID;
                    if (task.getResult() != null && task.getResult().getChildren().iterator().hasNext()) {
                        lastOrderID = task.getResult().getChildren().iterator().next().getValue(Order.class).getOrderID();
                        lastOrderID = lastOrderID.replaceAll("[^\\d.]", "");
                    } else {
                        lastOrderID = "0";
                    }

                    int orderID = Integer.parseInt(lastOrderID) + 1;
                    String orderIDString = String.valueOf(orderID);
                    while (orderIDString.length() < 5) {
                        orderIDString = "0" + orderIDString;
                    }
                    order.setOrderID("ORDER" + orderIDString);
                    
                    database.getReference("orders/").push().setValue(order).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            for (int i = 0; i < checkoutItemAdaptor.getItemCount(); i++) {
                                if (checkoutItemAdaptor.getItem(i).getSelected()) {
                                    cartRef.child(user.getUid()).child(checkoutItemAdaptor.getKey(i)).removeValue();
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Order Successful");
                            builder.setMessage("Your order has been placed successfully. Please wait for your order to be prepared.");
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                finish();
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }
            });
        });

        backBtn = findViewById(R.id.backBtnIcon);

        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkoutItemAdaptor.startListening();
        paymentMethodAdapter.startListening();
        orderOptionAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkoutItemAdaptor.stopListening();
        paymentMethodAdapter.stopListening();
        orderOptionAdaptor.stopListening();
    }

    public void updatePrices() {
        orderTotal = 0;
        quantity = 0;

        for (int i = 0; i < checkoutItemAdaptor.getItemCount(); i++) {
            orderTotal += checkoutItemAdaptor.getItem(i).getPrice() * checkoutItemAdaptor.getItem(i).getQuantity();
            quantity += checkoutItemAdaptor.getItem(i).getQuantity();
        }

        if(orderOption == null) {
            takeawayFee = 0;
        } else {
            takeawayFee = orderOption.getOption().equals("Takeaway") ? quantity * 0.5 : 0;
        }

        total = orderTotal + takeawayFee;

        orderTotalTextView.setText(String.format("RM %.2f", orderTotal));
        takeawayFeeTextView.setText(String.format("RM %.2f", takeawayFee));
        totalTextView.setText(String.format("RM %.2f", total));
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setOrderOption(OrderOption orderOption) {
        this.orderOption = orderOption;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setTakeawayFee(double takeawayFee) {
        this.takeawayFee = takeawayFee;
    }

    public double getTakeawayFee() {
        return takeawayFee;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}