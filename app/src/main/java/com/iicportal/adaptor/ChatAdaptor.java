package com.iicportal.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.iicportal.activity.LiveChatActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.database = MainActivity.database;
        this.usersRef = database.getReference("users/");
        this.chatsRef = database.getReference("support/chats/");
    }

    @NonNull
    @Override
    public ChatAdaptor.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position, Chat model) {
        // Determine if latest message was sent today, yesterday, or more than 1 day ago
        String latestMessageTime;
        Long latestMessageTimestamp  = model.getLatestMessageTimestamp();

        if (DateUtils.isToday(latestMessageTimestamp)) {
           latestMessageTime = new SimpleDateFormat("h:mm a").format(latestMessageTimestamp);
        } else if (DateUtils.isToday(latestMessageTimestamp + DateUtils.DAY_IN_MILLIS)) {
           latestMessageTime = "Yesterday";
        } else {
           latestMessageTime = new SimpleDateFormat("dd MMM yyyy").format(latestMessageTimestamp);
        }

        if (model.getUserProfilePicture() != null)
            Glide.with(context).load(model.getUserProfilePicture()).into(holder.userProfilePic);
        else
            holder.userProfilePic.setImageResource(R.drawable.baseline_account_circle_24);
        if (model.getLatestMessageSenderUid().equals(currentUser.getUid()))
            holder.latestMessage.setText(String.format("You: %s", model.getLatestMessage()));
        else
            holder.latestMessage.setText(model.getLatestMessage());

        holder.title.setText(model.getInitiatorName());
        holder.latestMessageTime.setText(latestMessageTime);

        // Check if chat is unread
        if (!model.isReadByAdmin()) {
           holder.title.setTypeface(null, Typeface.BOLD);
           holder.latestMessage.setTypeface(null, Typeface.BOLD);
           holder.latestMessageTime.setTypeface(null, Typeface.BOLD);
        } else {
           holder.title.setTypeface(null, Typeface.NORMAL);
           holder.latestMessage.setTypeface(null, Typeface.NORMAL);
           holder.latestMessageTime.setTypeface(null, Typeface.NORMAL);
        }

        // Go to LiveChatActivity when chat item is clicked
        holder.chatBody.setOnClickListener(view -> {
           chatsRef.child(getRef(position).getKey()).child("readByAdmin").setValue(true);

           Intent intent = new Intent(context, LiveChatActivity.class);
           intent.putExtra("INITIATOR_UID", model.getInitiatorUid());
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
