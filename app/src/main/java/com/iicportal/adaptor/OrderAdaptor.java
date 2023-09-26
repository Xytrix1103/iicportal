package com.iicportal.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderAdaptor extends FirebaseRecyclerAdapter<Order, OrderAdaptor.OrderViewHolder> {
    Context context;

    FirebaseDatabase database;
    DatabaseReference paymentMethodsRef, orderOptionsRef;
    OrderItemAdaptor orderItemAdaptor;

    public OrderAdaptor(FirebaseRecyclerOptions<Order> options, Context context) {
        super(options);
        this.context = context;
        database = FirebaseDatabase.getInstance();
        paymentMethodsRef = database.getReference("ecanteen/paymentmethods/");
        orderOptionsRef = database.getReference("ecanteen/orderoptions/");
    }

    @Override
    protected void onBindViewHolder(OrderViewHolder holder, int position, Order model) {
        holder.orderItems.setLayoutManager(new LinearLayoutManager(context));
        orderItemAdaptor = new OrderItemAdaptor(context, model.getItems());
        holder.orderItems.setAdapter(orderItemAdaptor);

        int totalQuantity = 0;
        for (int i = 0; i < model.getItems().size(); i++) {
            totalQuantity += model.getItems().get(i).getQuantity();
        }

        holder.orderID.setText(getRef(position).getKey());
        //am/pm in capital letters
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date(Long.parseLong(model.getTimestamp()));
        holder.datetime.setText(datetimeFormat.format(date));

        orderOptionsRef.child(model.getOrderOption()).child("option").get().addOnSuccessListener(snapshot -> {
            holder.orderOption.setText(snapshot.getValue().toString());
        });

        paymentMethodsRef.child(model.getPaymentMethod()).child("method").get().addOnSuccessListener(snapshot -> {
            holder.paymentMethod.setText(snapshot.getValue().toString());
        });

        holder.status.setText(model.getStatus());

        if (model.getStatus().equals("COMPLETED")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.light_blue_900));
        } else if (model.getStatus().equals("PREPARING")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.yellow_900));
        }

        holder.quantity.setText(String.valueOf(totalQuantity));
        holder.total.setText(String.format("RM %.2f", model.getTotal()));
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order, parent, false);
        return new OrderViewHolder(view);
    }

    public void onDataChanged() {
        Log.d("OrderAdaptor", "onDataChanged: ");
        notifyDataSetChanged();

        if (orderItemAdaptor != null) {
            orderItemAdaptor.notifyDataSetChanged();
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderID, status, datetime, orderOption, paymentMethod, quantity, orderTotal, takeawayFee, total;
        RecyclerView orderItems;

        public OrderViewHolder(View itemView) {
            super(itemView);

            orderID = itemView.findViewById(R.id.orderID);
            status = itemView.findViewById(R.id.status);
            datetime = itemView.findViewById(R.id.datetime);
            orderOption = itemView.findViewById(R.id.orderOption);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
            quantity = itemView.findViewById(R.id.quantity);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            takeawayFee = itemView.findViewById(R.id.takeawayFee);
            total = itemView.findViewById(R.id.total);
            orderItems = itemView.findViewById(R.id.recyclerView);
        }
    }
}
