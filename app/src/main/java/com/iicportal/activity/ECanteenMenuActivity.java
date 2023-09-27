package com.iicportal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.iicportal.adaptor.CategoryAdaptor;
import com.iicportal.adaptor.MenuItemAdaptor;
import com.iicportal.models.Category;
import com.iicportal.models.FoodItem;

public class ECanteenMenuActivity extends AppCompatActivity {

    Context context = this;
    RecyclerView menuRecyclerView;
    RecyclerView categoryRecyclerView;

    MenuItemAdaptor menuItemAdaptor;
    CategoryAdaptor categoryAdaptor;

    FirebaseDatabase database;
    DatabaseReference menuRef;
    DatabaseReference categoriesRef;
    DatabaseReference cartRef;

    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;

    FrameLayout cartBtn;
    ImageView cartIcon, historyBtnIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecanteen_menu);

        cartBtn = findViewById(R.id.cartBtn);
        cartIcon = findViewById(R.id.cartBtnIcon);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        categoriesRef = database.getReference("ecanteen/categories");
        categoriesRef.keepSynced(true);
        menuRef = database.getReference("ecanteen/fooditems/");
        menuRef.keepSynced(true);
        cartRef = database.getReference("carts/").child(user.getUid());

        sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);

        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("category")) {
                Log.d("ECanteenMenuActivity", "Category changed to " + sharedPreferences.getString("category", ""));
                categoryAdaptor.notifyDataSetChanged();

                menuItemAdaptor.stopListening();
                FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>()
                        .setQuery(menuRef.orderByChild("category").equalTo(sharedPreferences.getString("category", "")), FoodItem.class)
                        .build();
                menuItemAdaptor = new MenuItemAdaptor(options, context);
                menuRecyclerView.setAdapter(menuItemAdaptor);
                menuItemAdaptor.startListening();

                menuItemAdaptor.notifyDataSetChanged();
            }
        });

        FirebaseRecyclerOptions<Category> categoryOptions = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categoriesRef, Category.class)
                .build();

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdaptor = new CategoryAdaptor(categoryOptions, context);
        categoryRecyclerView.setAdapter(categoryAdaptor);

        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>()
                .setQuery(menuRef.orderByChild("category").equalTo(sharedPreferences.getString("category", "")), FoodItem.class)
                .build();

        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuItemAdaptor = new MenuItemAdaptor(options, context);
        menuRecyclerView.setAdapter(menuItemAdaptor);

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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        historyBtnIcon = findViewById(R.id.historyBtnIcon);
        historyBtnIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderHistoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences.edit().remove("category").apply();
        menuItemAdaptor.startListening();
        categoryAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().remove("category").apply();
        menuItemAdaptor.stopListening();
        categoryAdaptor.stopListening();
    }
}
