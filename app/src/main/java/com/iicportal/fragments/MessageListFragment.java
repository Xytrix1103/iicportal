package com.iicportal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.MessageAdaptor;
import com.iicportal.models.Message;

public class MessageListFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference messagesRef;

    private MessageAdaptor messageAdaptor;
    private RecyclerView messageListRecyclerView;

    private AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    private ImageView menuButton;
    private TextInputEditText searchEditText;

    public MessageListFragment() {
        super(R.layout.chat_list_fragment);
        this.openDrawerInterface = null;
        this.database = MainActivity.database;
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.messagesRef = database.getReference("messages/");
    }

    public MessageListFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.chat_list_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.database = MainActivity.database;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = mAuth.getCurrentUser();
        this.messagesRef = database.getReference("messages/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_list_fragment, container, false);

        // Set reference to views
        menuButton = view.findViewById(R.id.menuIcon);
        searchEditText = view.findViewById(R.id.searchWidgetEditText);
        messageListRecyclerView = view.findViewById(R.id.messageListRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        messageListRecyclerView.setLayoutManager(linearLayoutManager);

        // onClick listeners
        menuButton.setOnClickListener(v -> {
            openDrawerInterface.openDrawer();
        });

        // Set up the chats RecyclerView and Adapter
        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(messagesRef, Message.class)
                .setLifecycleOwner(this)
                .build();

        messageAdaptor = new MessageAdaptor(options, requireContext(), getChildFragmentManager());
        messageListRecyclerView.setAdapter(messageAdaptor);

        // Set up the search widget
        searchEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            String query = searchEditText.getText().toString();

            if (query.isEmpty()) {
                messageAdaptor.updateOptions(options);
            } else {
                messageAdaptor.stopListening();

                FirebaseRecyclerOptions<Message> searchOptions = new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(messagesRef.orderByChild("fullName").startAt(query).endAt(query + "\uf8ff"), Message.class)
                        .setLifecycleOwner(this)
                        .build();

                messageAdaptor.updateOptions(searchOptions);
                messageAdaptor.startListening();
            }

            return false;
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        messageAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageAdaptor.stopListening();
    }
}