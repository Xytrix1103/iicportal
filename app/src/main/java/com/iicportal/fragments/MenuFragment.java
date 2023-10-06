package com.iicportal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.MenuItemAdaptor;
import com.iicportal.models.FoodItem;

public class MenuFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference menuRef;
    Context context;
    String category;
    RecyclerView menuRecyclerView;
    MenuItemAdaptor menuItemAdaptor;

    public MenuFragment(String category) {
        super(R.layout.menu_fragment);
        this.category = category;
        this.context = getContext();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        database = FirebaseDatabase.getInstance();
        menuRef = database.getReference("ecanteen/fooditems/");
        menuRef.keepSynced(true);

        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>()
                .setQuery(menuRef.orderByChild("category").equalTo(category), FoodItem.class)
                .build();

        menuRecyclerView = view.findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        //pass fragment manager to adaptor
        menuItemAdaptor = new MenuItemAdaptor(options, context, getChildFragmentManager());
        menuRecyclerView.setAdapter(menuItemAdaptor);

        return view;
    }

    public void onStart() {
        super.onStart();
        menuItemAdaptor.startListening();
    }
}
