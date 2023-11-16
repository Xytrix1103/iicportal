package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.activity.ChatActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.Chat;
import com.iicportal.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ChatAdaptor extends FirebaseRecyclerAdapter<Chat, ChatAdaptor.ChatViewHolder> {
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef, chatsRef;

    private static final String CHAT_ADAPTOR_TAG = "ChatAdaptor";

    public ChatAdaptor(FirebaseRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = mAuth.getCurrentUser();
        this.database = MainActivity.database;
        this.usersRef = database.getReference("users/");
        this.chatsRef = database.getReference("chats/");
    }

    @NonNull
    @Override
    public ChatAdaptor.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position, Chat model) {
        HashMap<String, Boolean> members = (HashMap<String, Boolean>) model.getMembers();
        String recipientId = null;

        for (String key : members.keySet()) {
            if (!key.equals(currentUser.getUid())) {
                recipientId = key;
            }
        }

        database.getReference("users/" + recipientId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.child("fullName").getValue(String.class);
                String role = snapshot.child("role").getValue(String.class);
                String image = snapshot.child("image").getValue(String.class);

                holder.title.setText(fullName);
                MainActivity.loadImage(image, holder.userProfilePic, R.drawable.baseline_account_circle_24);

                if (role.equals("Admin")) {
                    holder.title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("messages/" + getRef(position).getKey()).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String latestMessage;
                Long latestMessageTimestamp;
                String latestMessageTime;

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    latestMessage = message.getMessage();
                    latestMessageTimestamp = message.getTimestamp();

                    if (DateUtils.isToday(latestMessageTimestamp)) {
                        latestMessageTime = new SimpleDateFormat("h:mm a").format(latestMessageTimestamp);
                    } else if (DateUtils.isToday(latestMessageTimestamp + DateUtils.DAY_IN_MILLIS)) {
                        latestMessageTime = "Yesterday";
                    } else {
                        latestMessageTime = new SimpleDateFormat("dd MMM yyyy").format(latestMessageTimestamp);
                    }

                    holder.latestMessage.setText(latestMessage);
                    holder.latestMessageTime.setText(latestMessageTime);

                    // Check if chat is unread
                    if (!message.isRead() && !message.getUid().equals(currentUser.getUid())) {
                        holder.title.setTypeface(null, Typeface.BOLD);
                        holder.latestMessage.setTypeface(null, Typeface.BOLD);
                        holder.latestMessageTime.setTypeface(null, Typeface.BOLD);
                    } else {
                        holder.title.setTypeface(null, Typeface.NORMAL);
                        holder.latestMessage.setTypeface(null, Typeface.NORMAL);
                        holder.latestMessageTime.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Go to ChatActivity when chat item is clicked
        holder.chatBody.setOnClickListener(view -> {
           Intent intent = new Intent(context, ChatActivity.class);
           intent.putExtra("CHAT_ID", getRef(position).getKey());
           context.startActivity(intent);
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    protected class ChatViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout chatBody;
        ImageView userProfilePic;
        TextView title;
        TextView latestMessage;
        TextView latestMessageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatBody = itemView.findViewById(R.id.chatBody);
            userProfilePic = itemView.findViewById(R.id.userProfilePic);
            title = itemView.findViewById(R.id.chatTitle);
            latestMessage = itemView.findViewById(R.id.latestMessage);
            latestMessageTime = itemView.findViewById(R.id.latestMessageTime);
        }
    }
}
