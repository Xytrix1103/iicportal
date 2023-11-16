package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
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
        database = MainActivity.database;
        paymentMethodsRef = database.getReference("ecanteen/paymentmethods/");
        orderOptionsRef = database.getReference("ecanteen/orderoptions/");
    }

    @Override
    protected void onBindViewHolder(OrderViewHolder holder, int position, Order model) {
        int pos = position;
        holder.orderItems.setLayoutManager(new LinearLayoutManager(context));
        orderItemAdaptor = new OrderItemAdaptor(context, model.getItems());
        holder.orderItems.setAdapter(orderItemAdaptor);
        MainActivity.loadImage(model.getPaymentMethod().getIcon(), holder.paymentMethodIcon);
        holder.orderOption.setText(model.getOrderOption().getOption());
        MainActivity.loadImage(model.getOrderOption().getIcon(), holder.orderOptionIcon);

        int totalQuantity = 0;
        for (int i = 0; i < model.getItems().size(); i++) {
            totalQuantity += model.getItems().get(i).getQuantity();
        }

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date(model.getTimestamp());
        holder.date.setText(datetimeFormat.format(date));

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        holder.time.setText(timeFormat.format(date));

        holder.orderID.setText(model.getOrderID());
        holder.status.setText(model.getStatus());

        Log.d("OrderAdaptor", "onBindViewHolder: " + model.getTimestamp() + " " + model.getReadyTimestamp() + " " + model.getCompletedTimestamp());

        if(model.getCompletedTimestamp() != null) {
            holder.status.setText("Completed");
            holder.status.setTextColor(context.getResources().getColor(R.color.light_green_800));
        } else if(model.getReadyTimestamp() != null) {
            holder.status.setText("Ready For Pickup");
            holder.status.setTextColor(context.getResources().getColor(R.color.black));
        } else if(model.getTimestamp() != null) {
            holder.status.setText("Preparing");
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
        TextView date, time;
        TextView total;
        ImageView paymentMethodIcon;
        TextView orderOption;
        ImageView orderOptionIcon;
        RecyclerView orderItems;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            total = itemView.findViewById(R.id.total);
            paymentMethodIcon = itemView.findViewById(R.id.payment_method_icon);
            orderOption = itemView.findViewById(R.id.order_option);
            orderOptionIcon = itemView.findViewById(R.id.order_option_icon);
            orderItems = itemView.findViewById(R.id.recyclerView);
        }
    }
}
