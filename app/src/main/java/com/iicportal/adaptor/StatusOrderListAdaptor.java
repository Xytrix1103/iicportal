package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.OrderDetailsActivity;
import com.iicportal.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//public class StatusOrderListAdaptor extends FirebaseRecyclerAdapter<Order, StatusOrderListAdaptor.OrderViewHolder> {
//    Context context;
//    OrderItemAdaptor orderItemAdaptor;
//    FirebaseDatabase database;
//    DatabaseReference paymentMethodsRef, orderOptionsRef;
//
//    public StatusOrderListAdaptor(FirebaseRecyclerOptions<Order> options, Context context) {
//        super(options);
//        this.context = context;
//        database = FirebaseDatabase.getInstance();
//        paymentMethodsRef = database.getReference("ecanteen/paymentmethods/");
//        orderOptionsRef = database.getReference("ecanteen/orderoptions/");
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
//        int pos = position;
//        holder.orderItems.setLayoutManager(new LinearLayoutManager(context));
//        orderItemAdaptor = new OrderItemAdaptor(context, model.getItems());
//        holder.orderItems.setAdapter(orderItemAdaptor);
//        paymentMethodsRef.child(model.getPaymentMethod()).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()) {
//                Glide.with(context).load(task.getResult().child("icon").getValue()).into(holder.paymentMethodIcon);
//            }
//        });
//
//        orderOptionsRef.child(model.getOrderOption()).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()) {
//                holder.orderOption.setText(task.getResult().child("option").getValue().toString());
//                Glide.with(context).load(task.getResult().child("icon").getValue()).into(holder.orderOptionIcon);
//            }
//        });
//
//        if (model.getCompletedTimestamp() != null) {
//            holder.button.setVisibility(View.GONE);
//        } else {
//            holder.button.setVisibility(View.VISIBLE);
//
//            if (model.getReadyTimestamp() != null) {
//                holder.button.setText("Completed");
//
//                holder.button.setOnClickListener(v -> {
//                    model.setCompletedTimestamp(System.currentTimeMillis());
//                    getRef(pos).setValue(model);
//                });
//            } else {
//                holder.button.setText("Ready");
//
//                holder.button.setOnClickListener(v -> {
//                    model.setReadyTimestamp(System.currentTimeMillis());
//                    getRef(pos).setValue(model);
//                });
//            }
//        }
//
//        int totalQuantity = 0;
//        for (int i = 0; i < model.getItems().size(); i++) {
//            totalQuantity += model.getItems().get(i).getQuantity();
//        }
//
//        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Date date = new Date(model.getTimestamp());
//        holder.date.setText(datetimeFormat.format(date));
//        holder.total.setText(String.format("RM %.2f", model.getTotal()));
//
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, OrderDetailsActivity.class);
//            intent.putExtra("orderID", getRef(pos).getKey());
//            context.startActivity(intent);
//        });
//    }
//
//    @Override
//    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_order, parent, false));
//    }
//
//    public static class OrderViewHolder extends RecyclerView.ViewHolder {
//        TextView date;
//        TextView total;
//        ImageView paymentMethodIcon;
//        TextView orderOption;
//        ImageView orderOptionIcon;
//        RecyclerView orderItems;
//        Button button;
//
//        public OrderViewHolder(View itemView) {
//            super(itemView);
//            date = itemView.findViewById(R.id.date);
//            total = itemView.findViewById(R.id.total);
//            paymentMethodIcon = itemView.findViewById(R.id.payment_method_icon);
//            orderOption = itemView.findViewById(R.id.order_option);
//            orderOptionIcon = itemView.findViewById(R.id.order_option_icon);
//            orderItems = itemView.findViewById(R.id.recyclerView);
//            button = itemView.findViewById(R.id.button);
//        }
//    }
//}

public class StatusOrderListAdaptor extends RecyclerView.Adapter<StatusOrderListAdaptor.OrderViewHolder> {
    HashMap<String, Order> orders;
    Context context;
    OrderItemAdaptor orderItemAdaptor;
    FirebaseDatabase database;
    DatabaseReference paymentMethodsRef, orderOptionsRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public StatusOrderListAdaptor(HashMap<String, Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    public StatusOrderListAdaptor() {
        this.orders = new HashMap<String, Order>();
        this.context = null;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vendor_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        int pos = position;
        Order model = (Order) orders.values().toArray()[position];
        holder.orderItems.setLayoutManager(new LinearLayoutManager(context));
        orderItemAdaptor = new OrderItemAdaptor(context, model.getItems());
        holder.orderItems.setAdapter(orderItemAdaptor);
        paymentMethodsRef.child(model.getPaymentMethod()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Glide.with(context).load(task.getResult().child("icon").getValue()).into(holder.paymentMethodIcon);
            }
        });

        orderOptionsRef.child(model.getOrderOption()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                holder.orderOption.setText(task.getResult().child("option").getValue().toString());
                Glide.with(context).load(task.getResult().child("icon").getValue()).into(holder.orderOptionIcon);
            }
        });

        if (model.getCompletedTimestamp() != null) {
            holder.button.setVisibility(View.GONE);
        } else {
            holder.button.setVisibility(View.VISIBLE);

            if (model.getReadyTimestamp() != null) {
                holder.button.setText("Completed");

                holder.button.setOnClickListener(v -> {
                    model.setCompletedTimestamp(System.currentTimeMillis());
                    database.getReference("orders/" + orders.keySet().toArray()[pos]).setValue(model);
                });
            } else {
                holder.button.setText("Ready");

                holder.button.setOnClickListener(v -> {
                    model.setReadyTimestamp(System.currentTimeMillis());
                    database.getReference("orders/" + orders.keySet().toArray()[pos]).setValue(model);
                });
            }
        }

        int totalQuantity = 0;
        for (int i = 0; i < model.getItems().size(); i++) {
            totalQuantity += model.getItems().get(i).getQuantity();
        }

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(model.getTimestamp());
        holder.date.setText(datetimeFormat.format(date));
        holder.total.setText(String.format("RM %.2f", model.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderID", orders.keySet().toArray()[pos].toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView total;
        ImageView paymentMethodIcon;
        TextView orderOption;
        ImageView orderOptionIcon;
        RecyclerView orderItems;
        Button button;

        public OrderViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            total = itemView.findViewById(R.id.total);
            paymentMethodIcon = itemView.findViewById(R.id.payment_method_icon);
            orderOption = itemView.findViewById(R.id.order_option);
            orderOptionIcon = itemView.findViewById(R.id.order_option_icon);
            orderItems = itemView.findViewById(R.id.recyclerView);
            button = itemView.findViewById(R.id.button);
        }
    }
}
