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
        int pos = position;
        holder.orderItems.setLayoutManager(new LinearLayoutManager(context));
        orderItemAdaptor = new OrderItemAdaptor(context, model.getItems());
        holder.orderItems.setAdapter(orderItemAdaptor);

        int totalQuantity = 0;
        for (int i = 0; i < model.getItems().size(); i++) {
            totalQuantity += model.getItems().get(i).getQuantity();
        }

        holder.orderID.setText(String.format("ORDER-%08d", pos + 1));
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date date = new Date(model.getTimestamp());
        holder.datetime.setText(datetimeFormat.format(date));

        holder.status.setText(model.getStatus());

        Log.d("OrderAdaptor", "onBindViewHolder: " + model.getTimestamp() + " " + model.getReadyTimestamp() + " " + model.getCompletedTimestamp());

        if(model.getCompletedTimestamp() != null) {
            holder.status.setText("COMPLETED");
            holder.status.setTextColor(context.getResources().getColor(R.color.light_green_800));
        } else if(model.getReadyTimestamp() != null) {
            holder.status.setText("READY FOR PICKUP");
            holder.status.setTextColor(context.getResources().getColor(R.color.black));
        } else if(model.getTimestamp() != null) {
            holder.status.setText("PREPARING");
            holder.status.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.total.setText(String.format("RM %.2f", model.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderID", getRef(pos).getKey());
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
