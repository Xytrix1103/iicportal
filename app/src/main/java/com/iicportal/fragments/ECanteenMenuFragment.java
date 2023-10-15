package com.iicportal.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.activity.CartActivity;
import com.iicportal.activity.OrderHistoryActivity;
import com.iicportal.adaptor.CategoryAdaptor;
import com.iicportal.models.Category;

public class ECanteenMenuFragment extends Fragment implements CategoryAdaptor.OnCategoryClickListener {

    private Context context;
    private RecyclerView categoryRecyclerView;

    private CategoryAdaptor categoryAdaptor;

    private FirebaseDatabase database;
    private DatabaseReference categoriesRef;
    private DatabaseReference cartRef;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private SharedPreferences sharedPreferences;

    private FrameLayout cartBtn;
    private ImageView cartIcon, historyBtnIcon, menuBtn, editBtn;
    FrameLayout menuFragmentContainer;
    String category;

    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FragmentManager fragmentManager;

    public ECanteenMenuFragment() {
        super(R.layout.ecanteen_menu_fragment);
        openDrawerInterface = null;
        fragmentManager = null;
    }

    public ECanteenMenuFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface, FragmentManager fragmentManager) {
        super(R.layout.ecanteen_menu_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ecanteen_menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();

        cartBtn = view.findViewById(R.id.cartBtn);
        cartIcon = view.findViewById(R.id.cartBtnIcon);
        menuFragmentContainer = view.findViewById(R.id.menu_fragment_container);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        menuBtn = view.findViewById(R.id.menuIcon);
        editBtn = view.findViewById(R.id.editBtn);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");

        if (!role.equals("Admin") && !role.equals("Vendor")) {
            editBtn.setVisibility(View.GONE);
        } else {
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(v -> {
                EditECanteenMenuFragment editECanteenMenuFragment = new EditECanteenMenuFragment(openDrawerInterface, fragmentManager);
                fragmentManager.beginTransaction().replace(R.id.ecanteen_fragment_container, editECanteenMenuFragment).commit();
            });
        }

        sharedPreferences = requireActivity().getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("role", "").equals("Admin") || sharedPreferences.getString("role", "").equals("Vendor")) {
            menuBtn.setVisibility(View.VISIBLE);
            menuBtn.setOnClickListener(v -> {
                if (openDrawerInterface != null) {
                    openDrawerInterface.openDrawer();
                }
            });
        } else {
            menuBtn.setVisibility(View.GONE);
        }

        database = FirebaseDatabase.getInstance();
        categoriesRef = database.getReference("ecanteen/categories");
        categoriesRef.keepSynced(true);
        cartRef = database.getReference("carts/" + user.getUid());

        FirebaseRecyclerOptions<Category> categoryOptions = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categoriesRef, Category.class)
                .build();

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        categoryAdaptor = new CategoryAdaptor(categoryOptions, context, this, getChildFragmentManager(), false);
        categoryRecyclerView.setAdapter(categoryAdaptor);

        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, CartActivity.class);
            startActivity(intent);
        });

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    count += 1;
                }

                BadgeDrawable badgeDrawable = BadgeDrawable.create(context);
                badgeDrawable.setVerticalOffset(15);
                badgeDrawable.setHorizontalOffset(20);
                badgeDrawable.setBadgeGravity(BadgeDrawable.TOP_END);
                badgeDrawable.setNumber(count);

                if (count == 0) {
                    cartBtn.setForeground(null);
                    badgeDrawable.setVisible(false);
                } else {
                    cartBtn.setForeground(badgeDrawable);
                    cartBtn.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                        badgeDrawable.updateBadgeCoordinates(cartIcon, cartBtn);
                    });
                    badgeDrawable.setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        historyBtnIcon = view.findViewById(R.id.historyBtnIcon);
        historyBtnIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onCategoryClick(String category) {
        this.category = category;
        this.context = requireContext();
        Log.d("context", context.toString());
        MenuFragment menuFragment = new MenuFragment(category, context, getChildFragmentManager(), false);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container, menuFragment).commit();
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void onStart() {
        super.onStart();
        categoryAdaptor.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryAdaptor.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdaptor.stopListening();
    }
}
