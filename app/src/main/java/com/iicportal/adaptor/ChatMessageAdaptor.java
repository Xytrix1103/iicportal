package com.iicportal.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.models.ChatMessage;

public class ChatMessageAdaptor extends FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {
    private Context context;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    private static final int VIEW_TYPE_RECEIVED_MESSAGE = 1;
    private static final int VIEW_TYPE_SENT_MESSAGE = 2;
    private static final String CHAT_MESSAGE_ADAPTOR_TAG = "ChatMessageAdaptor";

    public ChatMessageAdaptor(FirebaseRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.database = MainActivity.database;
        this.usersRef = database.getReference("users/");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_RECEIVED_MESSAGE) {
            View view = inflater.inflate(R.layout.chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_SENT_MESSAGE) {
            View view = inflater.inflate(R.layout.chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, ChatMessage model) {
        if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder castedHolder = (ReceivedMessageViewHolder) holder;
            castedHolder.receivedMessage.setText(model.getMessage());
            castedHolder.receivedMessage.setText(model.getMessage());
        } else if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder castedHolder = (SentMessageViewHolder) holder;
            castedHolder.sentMessage.setText(model.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        String currentUserUid = currentUser.getUid();

        if (!item.getUid().equals(currentUserUid)) {
            return VIEW_TYPE_RECEIVED_MESSAGE;
        } else if (item.getUid().equals(currentUserUid)) {
            return VIEW_TYPE_SENT_MESSAGE;
        }

        return 0;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMessage = itemView.findViewById(R.id.receivedMessageText);
        }
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessage;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessage = itemView.findViewById(R.id.sentMessageText);
        }
    }
}