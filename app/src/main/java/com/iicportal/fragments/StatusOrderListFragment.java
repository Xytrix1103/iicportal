package com.iicportal.fragments;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.adaptor.StatusOrderListAdaptor;
import com.iicportal.models.Order;

import java.util.HashMap;

public class StatusOrderListFragment extends Fragment {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    DatabaseReference ordersRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    StatusOrderListAdaptor statusOrderListAdaptor;
    int status;
    HashMap<String, Order> orders;

    public StatusOrderListFragment() {
        super(R.layout.status_order_list_fragment);
        database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("orders/");
        ordersRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        status = 0;
        orders = new HashMap<>();
    }

    public StatusOrderListFragment(int status) {
        super(R.layout.status_order_list_fragment);
        database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("orders/");
        ordersRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        this.status = status;
        orders = new HashMap<>();
    }

    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        return inflater.inflate(R.layout.status_order_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, android.os.Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.orderRecyclerView);

        Log.d("StatusOrderListFragment", "position: " + status);
        ordersRef.keepSynced(true);

        statusOrderListAdaptor = new StatusOrderListAdaptor(orders, getContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(statusOrderListAdaptor);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orders.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    orders.put(dataSnapshot.getKey(), dataSnapshot.getValue(Order.class));
                }

                switch (status) {
                    case 0:
                        orders.entrySet().removeIf(entry -> entry.getValue().getReadyTimestamp() != null || entry.getValue().getCompletedTimestamp() != null);
                        break;
                    case 1:
                        orders.entrySet().removeIf(entry -> entry.getValue().getReadyTimestamp() == null || entry.getValue().getCompletedTimestamp() != null);
                        break;
                    case 2:
                        orders.entrySet().removeIf(entry -> entry.getValue().getCompletedTimestamp() == null);
                        break;
                    default:
                        break;
                }

                statusOrderListAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
