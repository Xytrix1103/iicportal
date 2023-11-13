package com.iicportal.adaptor;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.activity.ChatActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.User;

import java.util.HashMap;
import java.util.Map;

public class ChatUserAdaptor extends FirebaseRecyclerAdapter<User, ChatUserAdaptor.ChatUserViewHolder> {
    private final Context context;

    public ChatUserAdaptor(FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    public static class ChatUserViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView profileImage;
        public TextView name;
        public TextView email;
        public TextView role;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.user_icon);
            email = itemView.findViewById(R.id.user_email);
            role = itemView.findViewById(R.id.user_role);
            name = itemView.findViewById(R.id.user_name);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatUserAdaptor.ChatUserViewHolder holder, int pos, @NonNull User model) {
        int position = holder.getAdapterPosition();
        holder.email.setText(model.getEmail());
        holder.role.setText(model.getRole());
        holder.name.setText(model.getFullName());
        if (model.getImage() != null) {
            Glide.with(context).load(model.getImage()).into(holder.profileImage);
        }
        FirebaseUser currentUser = MainActivity.mAuth.getCurrentUser();

        holder.itemView.setOnClickListener(v -> {
            DatabaseReference chatRef = MainActivity.database.getReference("chats/");

            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean chatExists = false;
                    String existingChatId = null;
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                        };
                        Map<String, Boolean> members = chatSnapshot.child("members").getValue(t);
                        if (members.containsKey(currentUser.getUid()) && members.containsKey(getRef(position).getKey())) {
                            chatExists = true;
                            existingChatId = chatSnapshot.getKey();
                        }
                    }

                    if (!chatExists) {
                        Map<String, Boolean> members = new HashMap<>();
                        members.put(currentUser.getUid(), true);
                        members.put(getRef(position).getKey(), true);
                        String chatId = chatRef.push().getKey();
                        chatRef.child(chatId).setValue(new HashMap<String, Object>() {{
                            put("members", members);
                        }});
                        startActivity(context, new Intent(context, ChatActivity.class).putExtra("CHAT_ID", chatId), null);
                    } else {
                        startActivity(context, new Intent(context, ChatActivity.class).putExtra("CHAT_ID", existingChatId), null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

    @NonNull
    @Override
    public ChatUserAdaptor.ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user, parent, false);
        return new ChatUserAdaptor.ChatUserViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
