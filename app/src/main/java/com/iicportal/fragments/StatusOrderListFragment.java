package com.iicportal.fragments;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.iicportal.R;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.StatusOrderListAdaptor;
import com.iicportal.models.Order;

public class StatusOrderListFragment extends Fragment {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    DatabaseReference ordersRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    StatusOrderListAdaptor statusOrderListAdaptor;
    int status;

    Query query;

    public StatusOrderListFragment() {
        super(R.layout.status_order_list_fragment);
        database = MainActivity.database;
        ordersRef = database.getReference("orders/");
        mAuth = MainActivity.mAuth;
        user = MainActivity.user;
        status = 0;
        query = ordersRef.orderByChild("completed").equalTo(false).getRef().orderByChild("ready").equalTo(false);
    }

    public void setStatus(int status) {
        this.status = status;
        Log.d("Status", "Status: " + status);

        switch(status) {
            case 0:
                query = ordersRef.orderByChild("completed").equalTo(false).getRef().orderByChild("ready").equalTo(false);
                break;
            case 1:
                query = ordersRef.orderByChild("ready").equalTo(true).getRef().orderByChild("completed").equalTo(false);
                break;
            case 2:
                query = ordersRef.orderByChild("completed").equalTo(true);
                break;
            default:
                Log.d("Status", "Default");
                break;
        }

        statusOrderListAdaptor.stopListening();

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .setLifecycleOwner(this)
                .build();

        statusOrderListAdaptor.updateOptions(options);

        statusOrderListAdaptor.startListening();

        statusOrderListAdaptor.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        return inflater.inflate(R.layout.status_order_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, android.os.Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.orderRecyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .setLifecycleOwner(this)
                .build();

        statusOrderListAdaptor = new StatusOrderListAdaptor(options, getContext());
        recyclerView.setAdapter(statusOrderListAdaptor);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(statusOrderListAdaptor != null) {
            statusOrderListAdaptor.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(statusOrderListAdaptor != null) {
            statusOrderListAdaptor.stopListening();
        }
    }
}
