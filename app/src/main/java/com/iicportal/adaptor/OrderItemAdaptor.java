package com.iicportal.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iicportal.R;
import com.iicportal.models.CartItem;

import java.util.List;

public class OrderItemAdaptor extends RecyclerView.Adapter<OrderItemAdaptor.OrderItemViewHolder> {
    Context context;
    List<CartItem> cartItems;

    public OrderItemAdaptor(Context context, List<CartItem> items) {
        this.context = context;
        this.cartItems = items;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.item.setText(item.getQuantity() + "x " + item.getName());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
//        TextView name, price, quantity;
//        ImageView image;
        TextView item;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
        }
    }
}