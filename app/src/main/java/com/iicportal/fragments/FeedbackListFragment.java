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
import com.iicportal.adaptor.FeedbackAdaptor;
import com.iicportal.models.Feedback;

public class FeedbackListFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference feedbackRef;

    private FeedbackAdaptor feedbackAdaptor;
    private RecyclerView feedbackRecyclerView;

    private AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    private ImageView menuButton;
    private TextInputEditText searchEditText;

    public FeedbackListFragment() {
        super(R.layout.feedback_list_fragment);
        this.openDrawerInterface = null;
        this.database = MainActivity.database;
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.feedbackRef = database.getReference("feedback/");
    }

    public FeedbackListFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.chat_list_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.database = MainActivity.database;
        this.mAuth = MainActivity.mAuth;
        this.currentUser = mAuth.getCurrentUser();
        this.feedbackRef = database.getReference("feedback/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_list_fragment, container, false);

        // Set reference to views
        menuButton = view.findViewById(R.id.menuIcon);
        searchEditText = view.findViewById(R.id.searchWidgetEditText);
        feedbackRecyclerView = view.findViewById(R.id.feedbackListRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        feedbackRecyclerView.setLayoutManager(linearLayoutManager);

        // onClick listeners
        menuButton.setOnClickListener(v -> {
            openDrawerInterface.openDrawer();
        });

        // Set up the chats RecyclerView and Adapter
        FirebaseRecyclerOptions<Feedback> options = new FirebaseRecyclerOptions.Builder<Feedback>()
                .setQuery(feedbackRef, Feedback.class)
                .setLifecycleOwner(this)
                .build();

        feedbackAdaptor = new FeedbackAdaptor(options, requireContext(), getChildFragmentManager());
        feedbackRecyclerView.setAdapter(feedbackAdaptor);

        // Set up the search widget
        searchEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            String query = searchEditText.getText().toString();

            if (query.isEmpty()) {
                feedbackAdaptor.updateOptions(options);
            } else {
                feedbackAdaptor.stopListening();

                FirebaseRecyclerOptions<Feedback> searchOptions = new FirebaseRecyclerOptions.Builder<Feedback>()
                        .setQuery(feedbackRef.orderByChild("fullName").startAt(query).endAt(query + "\uf8ff"), Feedback.class)
                        .setLifecycleOwner(this)
                        .build();

                feedbackAdaptor.updateOptions(searchOptions);
                feedbackAdaptor.startListening();
            }

            return false;
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        feedbackAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedbackAdaptor.stopListening();
    }
}