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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iicportal.R;
import com.iicportal.models.CartItem;

import java.util.List;

public class CheckoutItemAdaptor extends FirebaseRecyclerAdapter<CartItem, CheckoutItemAdaptor.CheckoutItemViewHolder> {
    Context context;

    public CheckoutItemAdaptor(@NonNull FirebaseRecyclerOptions<CartItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkout_item, parent, false);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position, @NonNull CartItem model) {
        holder.name.setText(model.getName());
        holder.price.setText(String.format("RM %.2f", model.getPrice() * model.getQuantity()));
        holder.quantity.setText("x" + model.getQuantity());
        Glide.with(context).load(model.getImage()).into(holder.image);
    }

    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    public class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image;

        public CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            image = itemView.findViewById(R.id.image);
        }
    }

    public List<CartItem> getItems() {
        return getSnapshots();
    }

    public String getKey(int position) {
        return getRef(position).getKey();
    }
}
