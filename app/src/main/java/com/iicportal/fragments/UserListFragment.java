package com.iicportal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.AddUserActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.UserAdaptor;
import com.iicportal.models.User;

public class UserListFragment extends Fragment {
    UserAdaptor userAdaptor;
    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FirebaseDatabase database;

    public UserListFragment() {
        super(R.layout.user_list_fragment);
        this.openDrawerInterface = null;
        database = MainActivity.database;
    }

    public UserListFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface) {
        super(R.layout.user_list_fragment);
        this.openDrawerInterface = openDrawerInterface;
        database = MainActivity.database;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list_fragment, container, false);
        TextInputEditText search = view.findViewById(R.id.searchWidgetEditText);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        ImageView addUserBtn = view.findViewById(R.id.addUser);
        RecyclerView recyclerView = view.findViewById(R.id.userListRecyclerView);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(database.getReference().child("users"), User.class)
                .setLifecycleOwner(this)
                .build();
        userAdaptor = new UserAdaptor(options, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(userAdaptor);

        search.setOnEditorActionListener((v, actionId, event) -> {
            String query = search.getText().toString();
            if (query.isEmpty()) {
                userAdaptor.updateOptions(options);
            } else {
                Log.i("Search", query);
                userAdaptor.stopListening();

                FirebaseRecyclerOptions<User> searchOptions = new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(database.getReference().child("users").orderByChild("fullName").startAt(query).endAt(query + "\uf8ff"), User.class)
                        .setLifecycleOwner(this)
                        .build();

                userAdaptor.updateOptions(searchOptions);

                userAdaptor.startListening();
            }
            return false;
        });
        menuBtn.setOnClickListener(v -> openDrawerInterface.openDrawer());

        addUserBtn.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddUserActivity.class));
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        userAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userAdaptor.stopListening();
    }
}
