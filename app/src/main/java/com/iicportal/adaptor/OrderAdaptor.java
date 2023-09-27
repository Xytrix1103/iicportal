package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
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
import com.iicportal.activity.OrderDetailsActivity;
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

        holder.orderID.setText(String.format("ORDER-%08d", position + 1));
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date(Long.parseLong(model.getTimestamp()));
        holder.datetime.setText(datetimeFormat.format(date));

        holder.status.setText(model.getStatus());

        if (model.getStatus().equals("COMPLETED")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.light_blue_900));
        } else if (model.getStatus().equals("PREPARING")) {
            holder.status.setTextColor(context.getResources().getColor(R.color.yellow_900));
        }

        holder.total.setText(String.format("RM %.2f", model.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderID", getRef(position).getKey());
            context.startActivity(intent);
        });
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order, parent, false);
        return new OrderViewHolder(view);
    }

    public void onDataChanged() {
        super.onDataChanged();
        Log.d("OrderAdaptor", "onDataChanged: ");
        notifyDataSetChanged();

        if (orderItemAdaptor != null) {
            orderItemAdaptor.onDataChanged();
            orderItemAdaptor.notifyDataSetChanged();
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderID;
        TextView status;
        TextView datetime;
        TextView total;
        RecyclerView orderItems;

        public OrderViewHolder(View itemView) {
            super(itemView);

            orderID = itemView.findViewById(R.id.orderID);
            status = itemView.findViewById(R.id.status);
            datetime = itemView.findViewById(R.id.datetime);
            total = itemView.findViewById(R.id.total);
            orderItems = itemView.findViewById(R.id.recyclerView);
        }
    }
}
