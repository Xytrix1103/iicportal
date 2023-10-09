package com.iicportal.adaptor;

import android.content.Context;
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
        public TextView role;
        public TextView status;
        public ImageView profilePic;

        public UserViewHolder(View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.user_email);
            role = itemView.findViewById(R.id.user_role);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.email.setText(model.getEmail());
        holder.role.setText(model.getRole());
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
