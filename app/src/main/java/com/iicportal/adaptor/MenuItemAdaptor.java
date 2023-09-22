package com.iicportal.adaptor;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iicportal.R;
import com.iicportal.models.FoodItem;

public class MenuItemAdaptor extends FirebaseRecyclerAdapter<FoodItem, MenuItemAdaptor.MenuItemViewHolder> {
    Context context;

    public MenuItemAdaptor(FirebaseRecyclerOptions<FoodItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position, @NonNull FoodItem model) {
        holder.name.setText(model.getName());
        holder.description.setText(model.getDescription());
        holder.price.setText(model.getPrice().toString());
        Glide.with(holder.image.getContext()).load(model.getImage()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Dialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.order_menu_item_dialog);

            TextView title = dialog.findViewById(R.id.order_menu_item_dialog_title);
            TextView description = dialog.findViewById(R.id.order_menu_item_dialog_description);
            TextView price = dialog.findViewById(R.id.order_menu_item_dialog_price);
            ImageView image = dialog.findViewById(R.id.order_menu_item_dialog_image);
            TextView quantity = dialog.findViewById(R.id.order_menu_item_dialog_quantity);
            Button plusBtn = dialog.findViewById(R.id.order_menu_item_dialog_plus_button);
            Button minusBtn = dialog.findViewById(R.id.order_menu_item_dialog_minus_button);
            Button addToCartBtn = dialog.findViewById(R.id.order_menu_item_dialog_add_button);

            title.setText(model.getName());
            description.setText(model.getDescription());
            price.setText(String.format("RM %.2f", model.getPrice()));
            Glide.with(image.getContext()).load(model.getImage()).into(image);

            plusBtn.setOnClickListener(v1 -> {
                quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) + 1));
            });

            minusBtn.setOnClickListener(v1 -> {
                if (Integer.parseInt(quantity.getText().toString()) > 1) {
                    quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) - 1));
                }
            });

            addToCartBtn.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d("MenuItemAdaptor", "Data changed");
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        TextView price;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
        }
    }
}