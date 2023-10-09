package com.iicportal.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.models.BookingStatus;
import com.iicportal.models.OrderStatus;
import com.iicportal.models.Status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StatusAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private static final int VIEW_TYPE_BOOKING_STATUS = 1;
    private static final int VIEW_TYPE_ORDER_STATUS = 2;
    private static final String STATUS_ADAPTOR_TAG = "StatusAdaptor";

    private ArrayList<Status> statusList;

    public StatusAdaptor(ArrayList<Status> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_BOOKING_STATUS) {
            View view = inflater.inflate(R.layout.booking_status_item, parent, false);
            return new BookingStatusViewHolder(view);
        } else if (viewType == VIEW_TYPE_ORDER_STATUS) {
            View view = inflater.inflate(R.layout.order_status_item, parent, false);
            return new OrderStatusViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Status item = statusList.get(position);

        if (holder instanceof BookingStatusViewHolder) {
            BookingStatusViewHolder castedHolder = (BookingStatusViewHolder) holder;
            BookingStatus bookingStatus = (BookingStatus) item;

            Glide.with(castedHolder.image.getContext()).load(bookingStatus.getFacilityImage()).into(castedHolder.image);
            castedHolder.facilityName.setText(bookingStatus.getFacilityName());
            castedHolder.title.setText(bookingStatus.getTitle());
            castedHolder.description.setText(bookingStatus.getDescription());
            castedHolder.time.setText(new SimpleDateFormat("hh:mm a").format(bookingStatus.getTimestamp()));
        } else if (holder instanceof OrderStatusViewHolder) {
            OrderStatusViewHolder castedHolder = (OrderStatusViewHolder) holder;
            OrderStatus orderStatus = (OrderStatus) item;

            castedHolder.orderId.setText("Order ID: " + orderStatus.getOrderId());
            castedHolder.title.setText(orderStatus.getTitle());
            castedHolder.orderStatus.setText(orderStatus.getOrderStatus());
            castedHolder.description.setText(orderStatus.getDescription());
            castedHolder.time.setText(new SimpleDateFormat("hh:mm a").format(orderStatus.getTimestamp()));

            if (orderStatus.getOrderStatus().equals("PREPARING"))
                castedHolder.orderStatus.setTextColor(Color.rgb(255, 165, 0));
            else if (orderStatus.getOrderStatus().equals("READY"))
                castedHolder.orderStatus.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Status item = statusList.get(position);

        if (item.getType().equals("booking"))
            return VIEW_TYPE_BOOKING_STATUS;
        else if (item.getType().equals("order"))
            return VIEW_TYPE_ORDER_STATUS;

        return 0;
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    private class BookingStatusViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView facilityName;
        TextView title;
        TextView description;
        TextView time;

        public BookingStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.statusImage);
            facilityName = itemView.findViewById(R.id.facilityName);
            title = itemView.findViewById(R.id.statusTitleText);
            description = itemView.findViewById(R.id.statusDescription);
            time = itemView.findViewById(R.id.time);
        }
    }

    private class OrderStatusViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView title;
        TextView orderStatus;
        TextView description;
        TextView time;

        public OrderStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            title = itemView.findViewById(R.id.statusTitleText);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            description = itemView.findViewById(R.id.statusDescription);
            time = itemView.findViewById(R.id.time);
        }
    }
}
