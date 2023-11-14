package com.iicportal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.adaptor.ChatMessageAdaptor;
import com.iicportal.models.ChatMessage;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef, chatRef, messagesRef;

    private RecyclerView chatMessageRecyclerView;
    private ChatMessageAdaptor chatMessageAdaptor;

    private ImageView backButtonIcon, sendButtonIcon, userProfilePic;
    private TextView emptyChatText;
    private EditText messageEditText;
    private TextView chatNameTextView;

    private static final String LIVE_CHAT_TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = this;

        Intent intent = getIntent();
        String chatId = intent.getStringExtra("CHAT_ID");

        // Initialize firebase objects
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users/");
        chatRef = database.getReference("chats/" + chatId);
        messagesRef = database.getReference("messages/" + chatId);

        // Set reference to views
        backButtonIcon = findViewById(R.id.backBtnIcon);
        sendButtonIcon = findViewById(R.id.sendBtnIcon);
        userProfilePic = findViewById(R.id.userProfilePic);
        emptyChatText = findViewById(R.id.emptyChatText);
        messageEditText = findViewById(R.id.messageEditText);
        chatNameTextView = findViewById(R.id.contactHeaderText);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Boolean> members = (HashMap<String, Boolean>) snapshot.child("members").getValue();
                for (String member : members.keySet()) {
                    if (!member.equals(currentUser.getUid())) {
                        usersRef.child(member).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                MainActivity.loadImage(snapshot.child("image").getValue(String.class), userProfilePic, R.drawable.baseline_people_outline_24);
                                chatNameTextView.setText(snapshot.child("fullName").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.w(LIVE_CHAT_TAG, "loadChat:onCancelled", error.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(LIVE_CHAT_TAG, "loadChat:onCancelled", error.toException());
            }
        });

        chatMessageRecyclerView = findViewById(R.id.messageRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatMessageRecyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setLifecycleOwner(this)
                .setQuery(messagesRef, ChatMessage.class)
                .build();

        chatMessageAdaptor = new ChatMessageAdaptor(options, context);
        chatMessageRecyclerView.setAdapter(chatMessageAdaptor);
        chatMessageAdaptor.startListening();

        chatMessageAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (chatMessageAdaptor.getItemCount() == 0) {
                    emptyChatText.setVisibility(TextView.VISIBLE);
                } else {
                    emptyChatText.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatMessageRecyclerView.smoothScrollToPosition(chatMessageAdaptor.getItemCount() - 1);
            }
        });

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot message : snapshot.getChildren()) {
                    if (Boolean.FALSE.equals(message.child("read").getValue())) {
                        messagesRef.child(message.getKey()).child("read").setValue(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(LIVE_CHAT_TAG, "loadChat:onCancelled", error.toException());
            }
        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (messageEditText.getText().toString().isEmpty()) {
                    sendButtonIcon.setClickable(false);
                    sendButtonIcon.setEnabled(false);
                    sendButtonIcon.setImageResource(R.drawable.baseline_send_disabled_24);
                } else {
                    sendButtonIcon.setClickable(true);
                    sendButtonIcon.setEnabled(true);
                    sendButtonIcon.setImageResource(R.drawable.baseline_send_24);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendButtonIcon.setOnClickListener(view -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage(currentUser.getUid(), message, System.currentTimeMillis(), false);
                messagesRef.push().setValue(chatMessage);
                messageEditText.setText("");
            }
        });

        // onClick listeners
        backButtonIcon.setOnClickListener(view -> {
            // Return to homepage
            finish();
        });
    }

    private void setChatMessageAdaptor() {
        // Set up the messages RecyclerView and Adapter
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setLifecycleOwner(this)
                .setQuery(messagesRef, ChatMessage.class)
                .build();

        chatMessageAdaptor = new ChatMessageAdaptor(options, context);
        chatMessageRecyclerView.setAdapter(chatMessageAdaptor);
        chatMessageAdaptor.startListening();

        chatMessageAdaptor.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (chatMessageAdaptor.getItemCount() == 0) {
                    emptyChatText.setVisibility(TextView.VISIBLE);
                } else {
                    emptyChatText.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatMessageRecyclerView.smoothScrollToPosition(chatMessageAdaptor.getItemCount() - 1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (chatMessageAdaptor != null)
            chatMessageAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (chatMessageAdaptor != null)
            chatMessageAdaptor.stopListening();
    }
}