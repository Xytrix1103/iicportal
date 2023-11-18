package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iicportal.R;
import com.iicportal.activity.EditUserActivity;
import com.iicportal.models.User;

public class UserAdaptor extends FirebaseRecyclerAdapter<User, UserAdaptor.UserViewHolder> {
    private final Context context;

    public UserAdaptor(FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView email;
        public ImageView edit;

        public UserViewHolder(View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.user_email);
            edit = itemView.findViewById(R.id.edit_user);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.email.setText(model.getEmail());
        holder.edit.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EditUserActivity.class).putExtra("key", getRef(position).getKey()));
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
