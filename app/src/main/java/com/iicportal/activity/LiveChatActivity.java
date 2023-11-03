package com.iicportal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.iicportal.models.Chat;
import com.iicportal.models.ChatMessage;

public class LiveChatActivity extends AppCompatActivity {
    Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference usersRef, chatsRef, chatMessagesRef;

    private RecyclerView chatMessageRecyclerView;
    private ChatMessageAdaptor chatMessageAdaptor;

    private ImageView backButtonIcon, sendButtonIcon;
    private TextView emptyChatText;
    private EditText messageEditText;

    private static final String LIVE_CHAT_TAG = "LiveChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        context = this;

        Intent intent = getIntent();

        // Initialize firebase objects
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users/");
        chatsRef = database.getReference("support/chats/");
        chatMessagesRef = database.getReference("support/chatmessages/");

        // Set reference to views
        backButtonIcon = findViewById(R.id.backBtnIcon);
        sendButtonIcon = findViewById(R.id.sendBtnIcon);
        emptyChatText = findViewById(R.id.emptyChatText);
        messageEditText = findViewById(R.id.messageEditText);

        chatMessageRecyclerView = findViewById(R.id.messageRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatMessageRecyclerView.setLayoutManager(linearLayoutManager);

        // Determine if chat already exists
        chatsRef.orderByChild("initiatorUid").equalTo(intent.getStringExtra("INITIATOR_UID")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Existing chat
                    String chatId = "";

                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        chatId = chatSnapshot.getKey().toString();
                    }

                    chatMessagesRef = database.getReference("support/chatmessages/").child(chatId);

                    setSendButtonListenerForExistingChat(chatId);
                    setChatMessageAdaptor();
                } else {
                    // New chat
                    usersRef.child(currentUser.getUid()).get().addOnCompleteListener(getUserTask -> {
                        if (getUserTask.isSuccessful()) {
                            String username = getUserTask.getResult().child("fullName").getValue().toString();

                            sendButtonIcon.setOnClickListener(view -> {
                                String message = messageEditText.getText().toString();

                                // Start new chat
                                Chat newChat = new Chat(currentUser.getUid(), username, message, System.currentTimeMillis(), currentUser.getUid(), false);
                                String chatKey = chatsRef.push().getKey();

                                chatsRef.child(chatKey).setValue(newChat).addOnCompleteListener(createChatTask -> {
                                    if (createChatTask.isSuccessful()) {
                                        // Send message to newly created chat
                                        ChatMessage newChatMessage = new ChatMessage(currentUser.getUid(), message, System.currentTimeMillis());
                                        chatMessagesRef = database.getReference("support/chatmessages/").child(chatKey);

                                        chatMessagesRef.push().setValue(newChatMessage).addOnCompleteListener(sendMessageTask -> {
                                            if (sendMessageTask.isSuccessful()) {
                                                messageEditText.getText().clear();
                                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                                                setSendButtonListenerForExistingChat(chatKey);
                                                setChatMessageAdaptor();
                                            } else {
                                                Toast.makeText(context, "Unable to send your message, please try again later.", Toast.LENGTH_SHORT).show();
                                                Log.e(LIVE_CHAT_TAG, "Error sending message", sendMessageTask.getException());
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Unable to start new chat, please try again later.", Toast.LENGTH_SHORT).show();
                                        Log.e(LIVE_CHAT_TAG, "Error starting new chat", createChatTask.getException());
                                    }
                                });
                            });
                        } else {
                            Log.e(LIVE_CHAT_TAG, "Error getting user data", getUserTask.getException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(LIVE_CHAT_TAG, "loadChat:onCancelled", error.toException());
            }
        });

        // Enable send button when user enters text
        sendButtonIcon.setEnabled(false);
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
                .setQuery(chatMessagesRef, ChatMessage.class)
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
        });
    }

    private void setSendButtonListenerForExistingChat(String chatId) {
        sendButtonIcon.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.iicportal", 0);
            String role = sharedPreferences.getString("role", "");

            // Update chat latest message, latest message timestamp, and readByAdmin
            chatsRef.child(chatId).child("latestMessage").setValue(messageEditText.getText().toString());
            chatsRef.child(chatId).child("latestMessageTimestamp").setValue(System.currentTimeMillis());
            chatsRef.child(chatId).child("latestMessageSenderUid").setValue(currentUser.getUid());
            if (role.equals("Student")) {
                chatsRef.child(chatId).child("readByAdmin").setValue(false);
            }

            // Send message in existing chat
            ChatMessage newChatMessage = new ChatMessage(currentUser.getUid(), messageEditText.getText().toString(), System.currentTimeMillis());
            chatMessagesRef.push().setValue(newChatMessage).addOnCompleteListener(sendMessageTask -> {
                if (sendMessageTask.isSuccessful()) {
                    messageEditText.getText().clear();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    Toast.makeText(context, "Unable to send your message, please try again later.", Toast.LENGTH_SHORT).show();
                    Log.e(LIVE_CHAT_TAG, "Error sending message", sendMessageTask.getException());
                }
            });
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