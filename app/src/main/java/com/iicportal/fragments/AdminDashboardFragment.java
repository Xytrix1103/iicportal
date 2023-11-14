package com.iicportal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.iicportal.activity.MainActivity;

import java.util.HashSet;
import java.util.Set;

public class AdminDashboardFragment extends Fragment {
    OpenDrawerInterface openDrawerInterface;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference usersRef, chatsRef, chatMessagesRef, feedbackRef;

    TextView userCountText;
    TextView chatCountText;
    TextView feedbackCountText;
    TextView emptyTasksText;
    RelativeLayout chatsLayout;
    RelativeLayout feedbackLayout;

    boolean unreadChats;
    boolean unreadFeedback;

    static final String ADMIN_DASHBOARD_TAG = "AdminDashboardFragment";

    public AdminDashboardFragment() {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = null;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = MainActivity.database;
        usersRef = database.getReference("users/");
        chatsRef = database.getReference("chats/");
        chatMessagesRef = database.getReference("messages/");
        feedbackRef = database.getReference("feedback/");
    }

    public AdminDashboardFragment(OpenDrawerInterface openDrawerInterface) {
        super(R.layout.admin_dashboard_fragment);
        this.openDrawerInterface = openDrawerInterface;
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        database = MainActivity.database;
        usersRef = database.getReference("users/");
        chatsRef = database.getReference("chats/");
        chatMessagesRef = database.getReference("messages/");
        feedbackRef = database.getReference("feedback/");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AdminDashboardFragment", "onCreateView: ");
        View view = inflater.inflate(R.layout.admin_dashboard_fragment, container, false);

        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        userCountText = view.findViewById(R.id.userCountText);
        chatCountText = view.findViewById(R.id.chatCountText);
        feedbackCountText = view.findViewById(R.id.feedbackCountText);
        emptyTasksText = view.findViewById(R.id.tasksEmptyText);
        chatsLayout = view.findViewById(R.id.chatsLayout);
        feedbackLayout = view.findViewById(R.id.feedbackLayout);

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
        Set<String> unreadChatIdSet = new HashSet<String>();
        chatsRef.orderByChild("/members/" + user.getUid()).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey().toString();

                    chatMessagesRef.child(chatId).orderByChild("read").equalTo(false).limitToLast(1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                unreadChatIdSet.add(chatId);
                            } else {
                                unreadChatIdSet.remove(chatId);
                            }

                            int unreadChatsCount = unreadChatIdSet.size();
                            if (unreadChatIdSet.size() > 0) {
                                chatsLayout.setVisibility(View.VISIBLE);
                                chatCountText.setText(unreadChatsCount + " unread chat" + ((unreadChatsCount != 1) ? "s" : ""));
                                unreadChats = true;
                            } else {
                                chatsLayout.setVisibility(View.GONE);
                                unreadChats = false;
                            }

                            displayEmptyTasksText();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(ADMIN_DASHBOARD_TAG, "loadChatMessages:onCancelled");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(ADMIN_DASHBOARD_TAG, "loadChats:onCancelled");
            }
        });

        // Get unread messages count
        unreadFeedback = false;
        feedbackRef.orderByChild("read").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long unreadMessagesCount = snapshot.getChildrenCount();

                if (unreadMessagesCount == 0) {
                    feedbackLayout.setVisibility(View.GONE);
                    unreadFeedback = false;
                } else {
                    feedbackLayout.setVisibility(View.VISIBLE);
                    feedbackCountText.setText(unreadMessagesCount + " unread feedback");
                    unreadFeedback = true;
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
        if (!unreadChats && !unreadFeedback) {
            emptyTasksText.setVisibility(View.VISIBLE);
        } else {
            emptyTasksText.setVisibility(View.GONE);
        }
    }

    public interface OpenDrawerInterface {
        void openDrawer();
    }
}
