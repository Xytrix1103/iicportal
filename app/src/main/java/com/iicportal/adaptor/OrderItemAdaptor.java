package com.iicportal.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        holder.name.setText(cartItems.get(position).getName());
        holder.price.setText(String.format("RM %.2f", cartItems.get(position).getPrice() * cartItems.get(position).getQuantity()));
        holder.quantity.setText("x" + cartItems.get(position).getQuantity());
        Glide.with(context).load(cartItems.get(position).getImage()).into(holder.image);
    }

    public void onDataChanged() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            image = itemView.findViewById(R.id.image);
        }
    }
}