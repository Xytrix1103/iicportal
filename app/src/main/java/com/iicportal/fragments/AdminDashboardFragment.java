package com.iicportal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.activity.LoginActivity;
import com.iicportal.activity.MainActivity;

public class AdminDashboardFragment extends Fragment {
    OpenDrawerInterface openDrawerInterface;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference usersRef, chatsRef, messagesRef;

    TextView userCountText;
    TextView chatCountText;
    TextView messageCountText;
    TextView emptyTasksText;
    RelativeLayout chatsLayout;
    RelativeLayout messagesLayout;

    boolean unreadChats;
    boolean unreadMessages;

    static final String ADMIN_DASHBOARD_TAG = "AdminDashboardFragment";

    public AdminDashboardFragment() {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = null;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = MainActivity.database;
        usersRef = database.getReference("users/");
        chatsRef = database.getReference("support/chats/");
        messagesRef = database.getReference("messages/");
    }

    public AdminDashboardFragment(OpenDrawerInterface openDrawerInterface) {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = openDrawerInterface;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = MainActivity.database;
        usersRef = database.getReference("users/");
        chatsRef = database.getReference("support/chats/");
        messagesRef = database.getReference("messages/");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AdminDashboardFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);

        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        userCountText = view.findViewById(R.id.userCountText);
        chatCountText = view.findViewById(R.id.chatCountText);
        messageCountText = view.findViewById(R.id.messageCountText);
        emptyTasksText = view.findViewById(R.id.tasksEmptyText);
        chatsLayout = view.findViewById(R.id.chatsLayout);
        messagesLayout = view.findViewById(R.id.messagesLayout);

        // Get user count
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long userCount = snapshot.getChildrenCount();
                userCountText.setText(String.valueOf(userCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(ADMIN_DASHBOARD_TAG, "loadUsers:onCancelled");
            }
        });

        // TODO: Get unread chats count
        unreadChats = false;
        chatsRef.orderByChild("readByAdmin").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long unreadChatsCount = snapshot.getChildrenCount();

                if (unreadChatsCount == 0) {
                    chatsLayout.setVisibility(View.GONE);
                    unreadChats = false;
                } else {
                    chatsLayout.setVisibility(View.VISIBLE);
                    chatCountText.setText(unreadChatsCount + " unread chat" + ((unreadChatsCount != 1) ? "s" : ""));
                    unreadChats = true;
                }

                displayEmptyTasksText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(ADMIN_DASHBOARD_TAG, "loadChats:onCancelled");
            }
        });

        // Get unread messages count
        unreadMessages = false;
        messagesRef.orderByChild("read").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long unreadMessagesCount = snapshot.getChildrenCount();

                if (unreadMessagesCount == 0) {
                    messagesLayout.setVisibility(View.GONE);
                    unreadMessages = false;
                } else {
                    messagesLayout.setVisibility(View.VISIBLE);
                    messageCountText.setText(unreadMessagesCount + " unread message" + ((unreadMessagesCount != 1) ? "s" : ""));
                    unreadMessages = true;
                }

                displayEmptyTasksText();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(ADMIN_DASHBOARD_TAG, "loadMessages:onCancelled");
            }
        });

        menuBtn.setOnClickListener(v -> openDrawerInterface.openDrawer());

        return view;
    }

    private void displayEmptyTasksText() {
        if (!unreadChats && !unreadMessages) {
            emptyTasksText.setVisibility(View.VISIBLE);
        } else {
            emptyTasksText.setVisibility(View.GONE);
        }
    }

    public interface OpenDrawerInterface {
        void openDrawer();
    }
}
