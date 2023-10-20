package com.iicportal.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.activity.AddFoodItemActivity;
import com.iicportal.activity.MainActivity;
import com.iicportal.adaptor.MenuItemAdaptor;
import com.iicportal.models.FoodItem;

public class MenuFragment extends Fragment {
    FirebaseDatabase database;
    DatabaseReference menuRef;
    Context context;
    String category;
    RecyclerView menuRecyclerView;
    MenuItemAdaptor menuItemAdaptor;
    FragmentManager childFragmentManager;
    boolean isEdit;

    public MenuFragment() {
        super(R.layout.menu_fragment);
    }

    public MenuFragment(String category, Context context, FragmentManager childFragmentManager, boolean isEdit) {
        super(R.layout.menu_fragment);
        this.category = category;
        this.context = context;
        this.childFragmentManager = childFragmentManager;
        this.isEdit = isEdit;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = MainActivity.database;
        menuRef = database.getReference("ecanteen/fooditems/");

        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>()
                .setQuery(menuRef.orderByChild("category").equalTo(category), FoodItem.class)
                .build();

        menuRecyclerView = view.findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        menuItemAdaptor = new MenuItemAdaptor(options, context, getChildFragmentManager(), isEdit);
        menuRecyclerView.setAdapter(menuItemAdaptor);

        ImageView addItemBtn = view.findViewById(R.id.addItemBtn);

        if(isEdit) {
            addItemBtn.setVisibility(View.VISIBLE);
        } else {
            addItemBtn.setVisibility(View.GONE);
        }

        addItemBtn.setOnClickListener(v -> {
            startActivity(new Intent(context, AddFoodItemActivity.class).putExtra("category", category));
        });
    }

    public void onStart() {
        super.onStart();
        menuItemAdaptor.startListening();
    }
}
