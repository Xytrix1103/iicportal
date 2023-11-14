package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.AdminOrderDetailsActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusOrderListAdaptor extends FirebaseRecyclerAdapter<Order, StatusOrderListAdaptor.StatusOrderViewHolder> {
    Context context;
    OrderItemAdaptor orderItemAdaptor;
    FirebaseDatabase database;
    DatabaseReference paymentMethodsRef, orderOptionsRef;

    public StatusOrderListAdaptor(FirebaseRecyclerOptions<Order> options, Context context) {
        super(options);
        this.context = context;
        database = MainActivity.database;
        paymentMethodsRef = database.getReference("ecanteen/paymentmethods/");
        orderOptionsRef = database.getReference("ecanteen/orderoptions/");
    }

    @Override
    protected void onBindViewHolder(@NonNull StatusOrderViewHolder holder, int position, @NonNull Order model) {
        int pos = position;
        holder.orderItems.setLayoutManager(new LinearLayoutManager(context));
        orderItemAdaptor = new OrderItemAdaptor(context, model.getItems());
        holder.orderItems.setAdapter(orderItemAdaptor);
        Glide.with(context).load(model.getPaymentMethod().getIcon()).placeholder(R.drawable.baseline_image_placeholdeer).into(holder.paymentMethodIcon);
        holder.orderOption.setText(model.getOrderOption().getOption());
        Glide.with(context).load(model.getOrderOption().getIcon()).placeholder(R.drawable.baseline_image_placeholdeer).into(holder.orderOptionIcon);

        if (model.isCompleted()) {
            holder.button.setVisibility(Button.GONE);
        } else {
            holder.button.setVisibility(Button.VISIBLE);
            if (model.isReady()) {
                holder.button.setText("Completed");
                holder.button.setOnClickListener(v -> {
                    model.setCompleted(true);
                    model.setCompletedTimestamp(System.currentTimeMillis());
                    model.setStatus("COMPLETED");
                    getRef(pos).setValue(model);
                });
            } else {
                holder.button.setText("Ready");
                holder.button.setOnClickListener(v -> {
                    model.setReady(true);
                    model.setReadyTimestamp(System.currentTimeMillis());
                    model.setStatus("READY");
                    getRef(pos).setValue(model);
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

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        holder.time.setText(timeFormat.format(date));

        holder.orderID.setText(model.getOrderID());

        holder.total.setText(String.format("RM %.2f", model.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetailsActivity.class);
            intent.putExtra("orderID", getRef(pos).getKey());
            context.startActivity(intent);
        });
    }

    @Override
    public StatusOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatusOrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_order, parent, false));
    }

    public void onDataChanged() {
        super.onDataChanged();
        Log.d("OrderAdaptor", "onDataChanged: " + getItemCount());
        notifyDataSetChanged();
    }

    public static class StatusOrderViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, orderID;
        TextView total;
        ImageView paymentMethodIcon;
        TextView orderOption;
        ImageView orderOptionIcon;
        RecyclerView orderItems;
        Button button;

        public StatusOrderViewHolder(View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            time = itemView.findViewById(R.id.time);
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