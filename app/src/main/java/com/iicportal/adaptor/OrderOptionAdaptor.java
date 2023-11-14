package com.iicportal.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iicportal.R;
import com.iicportal.activity.CheckoutActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.OrderOption;

public class OrderOptionAdaptor extends FirebaseRecyclerAdapter<OrderOption, OrderOptionAdaptor.OrderOptionViewHolder> {
    Context context;
    int selectedPosition = 0;

    public OrderOptionAdaptor(FirebaseRecyclerOptions<OrderOption> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(OrderOptionViewHolder holder, int position, OrderOption model) {
        int pos = position;
        holder.option.setText(model.getOption());
        MainActivity.loadImage(model.getIcon(), holder.icon);
        holder.radioButton.setChecked(pos == selectedPosition);

        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPosition = pos;
                OrderOption orderOption = getItem(pos);
                ((CheckoutActivity) context).setOrderOption(orderOption);
                ((CheckoutActivity) context).updatePrices();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public OrderOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_option, parent, false);
        return new OrderOptionViewHolder(view);
    }

    public void onDataChanged() {
        super.onDataChanged();

        if (super.getItemCount() > 0) {
            ((CheckoutActivity) context).setOrderOption(getItem(0));
        }

        notifyDataSetChanged();
    }

    public static class OrderOptionViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView option;

        RadioButton radioButton;

        public OrderOptionViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            option = itemView.findViewById(R.id.option);

            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
