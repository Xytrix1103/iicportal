package com.iicportal.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.iicportal.activity.SelectChatRecipientActivity;
import com.iicportal.adaptor.ChatAdaptor;
import com.iicportal.models.Chat;

public class ChatListFragment extends Fragment {
    private ImageView addIcon;
    private Context context;
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
        this.database = MainActivity.database;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = mAuth.getCurrentUser();
        this.chatsRef = database.getReference("chats/");
    }

    public ChatListFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.chat_list_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.database = MainActivity.database;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = mAuth.getCurrentUser();
        this.chatsRef = database.getReference("chats/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list_fragment, container, false);
        context = requireContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Set reference to views
        menuButton = view.findViewById(R.id.menuIcon);
        addIcon = view.findViewById(R.id.addIcon);
        chatListRecyclerView = view.findViewById(R.id.chatListReyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);

        // Set up the chats RecyclerView and Adapter
        FirebaseRecyclerOptions<Chat> options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatsRef.orderByChild("/members/" + currentUser.getUid()).equalTo(true), Chat.class)
                .setLifecycleOwner(this)
                .build();

        chatAdaptor = new ChatAdaptor(options, context);
        chatListRecyclerView.setAdapter(chatAdaptor);

        addIcon.setOnClickListener(v -> {
            startActivity(new Intent(context, SelectChatRecipientActivity.class));
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        if (sharedPreferences.getString("role", "").equals("Admin") || sharedPreferences.getString("role", "").equals("Vendor")) {
            menuButton.setVisibility(View.VISIBLE);
            menuButton.setOnClickListener(v -> {
                if (openDrawerInterface != null) {
                    openDrawerInterface.openDrawer();
                }
            });
        } else {
            menuButton.setVisibility(View.GONE);
        }
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