package com.iicportal.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iicportal.R;
import com.iicportal.adaptor.CategoryAdaptor;
import com.iicportal.adaptor.MenuItemAdaptor;
import com.iicportal.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class ECanteenMenuActivity extends AppCompatActivity {

    Context context = this;
    RecyclerView menuRecyclerView;
    RecyclerView categoryRecyclerView;

    MenuItemAdaptor menuItemAdaptor;
    CategoryAdaptor categoryAdaptor;

    FirebaseDatabase database;
    DatabaseReference menuRef;

    SharedPreferences sharedPreferences;

    List<String> categories = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecanteen_menu);

        database = FirebaseDatabase.getInstance();
        menuRef = database.getReference("ecanteen/fooditems/");

        sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("category")) {
                menuItemAdaptor.stopListening();
                menuItemAdaptor = new MenuItemAdaptor(new FirebaseRecyclerOptions.Builder<FoodItem>()
                        .setLifecycleOwner(this)
                        .setQuery(menuRef.child(sharedPreferences.getString("category", "Sandwich")), FoodItem.class)
                        .build());
                menuRecyclerView.setAdapter(menuItemAdaptor);
                menuItemAdaptor.startListening();
            }
        });

        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>()
                .setLifecycleOwner(this)
                .setQuery(menuRef.child(sharedPreferences.getString("category", "Sandwich")), FoodItem.class)
                .build();

        menuItemAdaptor = new MenuItemAdaptor(options);

        menuRecyclerView.setAdapter(menuItemAdaptor);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot categorySnapshot: dataSnapshot.getChildren()) {
                    categories.add(categorySnapshot.getKey());
                }

                editor.putString("category", categories.get(0));
                editor.apply();

                categoryAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Error", databaseError.getMessage());
            }
        });

        categoryAdaptor = new CategoryAdaptor(context, categories);

        categoryRecyclerView.setAdapter(categoryAdaptor);
    }

    @Override
    protected void onStart() {
        super.onStart();
        menuItemAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        menuItemAdaptor.stopListening();
    }
}
