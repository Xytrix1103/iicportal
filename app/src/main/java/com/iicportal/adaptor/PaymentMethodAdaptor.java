package com.iicportal.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.CheckoutActivity;
import com.iicportal.models.PaymentMethod;

public class PaymentMethodAdaptor extends FirebaseRecyclerAdapter<PaymentMethod, PaymentMethodAdaptor.PaymentMethodViewHolder> {
    Context context;

    FirebaseDatabase database;
    DatabaseReference paymentRef;
    DatabaseReference cartRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    int selectedPosition = 0;

    public PaymentMethodAdaptor(@NonNull FirebaseRecyclerOptions<PaymentMethod> options, Context context) {
        super(options);
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.paymentRef = database.getReference("ecanteen/paymentmethods/");
        this.cartRef = database.getReference("carts/");
        this.paymentRef.keepSynced(true);
        this.cartRef.keepSynced(true);
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
    }

    @Override
    protected void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position, @NonNull PaymentMethod model) {
        int pos = position;
        holder.method.setText(model.getMethod());
        Glide.with(context).load(model.getIcon()).into(holder.icon);
        holder.radioButton.setChecked(pos == selectedPosition);

        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPosition = pos;
                ((CheckoutActivity) context).setPaymentMethod(getRef(pos).getKey());
                ((CheckoutActivity) context).updatePrices();
                notifyDataSetChanged();
            }
        });
    }

    public void onDataChanged() {
        super.onDataChanged();
        Log.d("PaymentMethodAdaptor", "Payment methods count: " + super.getItemCount());

        if (super.getItemCount() > 0) {
            ((CheckoutActivity) context).setPaymentMethod(getRef(0).getKey());
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    public class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        TextView method;
        ImageView icon;
        RadioButton radioButton;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            method = itemView.findViewById(R.id.method);
            icon = itemView.findViewById(R.id.icon);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}