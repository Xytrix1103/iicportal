package com.iicportal.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.ChatUserAdaptor;
import com.iicportal.models.User;

public class SelectChatRecipientActivity extends AppCompatActivity {
    ChatUserAdaptor chatUserAdaptor;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);
        database = MainActivity.database;

        TextInputEditText search = findViewById(R.id.searchWidgetEditText);
        RecyclerView recyclerView = findViewById(R.id.usersRecyclerView);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(database.getReference().child("users"), User.class)
                .setLifecycleOwner(this)
                .build();
        chatUserAdaptor = new ChatUserAdaptor(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatUserAdaptor);

        search.setOnEditorActionListener((v, actionId, event) -> {
            String query = search.getText().toString();
            if (query.isEmpty()) {
                chatUserAdaptor.updateOptions(options);
            } else {
                Log.i("Search", query);
                chatUserAdaptor.stopListening();

                FirebaseRecyclerOptions<User> searchOptions = new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(database.getReference().child("users").orderByChild("fullName").startAt(query).endAt(query + "\uf8ff"), User.class)
                        .setLifecycleOwner(this)
                        .build();

                chatUserAdaptor.updateOptions(searchOptions);

                chatUserAdaptor.startListening();
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        chatUserAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatUserAdaptor.stopListening();
    }
}