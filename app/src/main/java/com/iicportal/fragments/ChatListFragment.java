package com.iicportal.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.ChatAdaptor;
import com.iicportal.models.Chat;

public class ChatListFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference chatsRef;

    private ChatAdaptor chatAdaptor;
    private RecyclerView chatListRecyclerView;

    private AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    private ImageView menuButton;
    private TextInputEditText searchEditText;

    public ChatListFragment() {
        super(R.layout.chat_list_fragment);
        this.openDrawerInterface = null;
        this.database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.chatsRef = database.getReference("support/chats/");
    }

    public ChatListFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.chat_list_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.database = FirebaseDatabase.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.chatsRef = database.getReference("support/chats/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list_fragment, container, false);

        // Set reference to views
        menuButton = view.findViewById(R.id.menuIcon);
        searchEditText = view.findViewById(R.id.searchWidgetEditText);
        chatListRecyclerView = view.findViewById(R.id.chatListReyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);

        // onClick listeners
        menuButton.setOnClickListener(v -> {
            openDrawerInterface.openDrawer();
        });

        // Set up the chats RecyclerView and Adapter
        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatsRef.orderByChild("latestMessageTimestamp"), Chat.class)
                .setLifecycleOwner(this)
                .build();

        chatAdaptor = new ChatAdaptor(options, requireContext());
        chatListRecyclerView.setAdapter(chatAdaptor);

        // Set up the search widget
        searchEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            String query = searchEditText.getText().toString();

            if (query.isEmpty()) {
                chatAdaptor.updateOptions(options);
            } else {
                chatAdaptor.stopListening();

                FirebaseRecyclerOptions<Chat> searchOptions = new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(chatsRef.orderByChild("initiatorName").startAt(query).endAt(query + "\uf8ff"), Chat.class)
                        .setLifecycleOwner(this)
                        .build();

                chatAdaptor.updateOptions(searchOptions);
                chatAdaptor.startListening();
            }

            return false;
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatAdaptor.stopListening();
    }
}