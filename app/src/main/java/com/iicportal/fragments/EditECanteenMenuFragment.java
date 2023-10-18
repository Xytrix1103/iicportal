package com.iicportal.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iicportal.R;
import com.iicportal.adaptor.CategoryAdaptor;
import com.iicportal.models.Category;

public class EditECanteenMenuFragment extends Fragment implements CategoryAdaptor.OnCategoryClickListener {

    private CategoryAdaptor categoryAdaptor;

    FrameLayout menuFragmentContainer;
    String category;
    Context context;

    AdminDashboardFragment.OpenDrawerInterface openDrawerInterface;
    FragmentManager fragmentManager;

    public EditECanteenMenuFragment() {
        super(R.layout.edit_ecanteen_menu_fragment);
        openDrawerInterface = null;
        fragmentManager = null;
    }

    public EditECanteenMenuFragment(AdminDashboardFragment.OpenDrawerInterface openDrawerInterface, FragmentManager fragmentManager) {
        super(R.layout.edit_ecanteen_menu_fragment);
        this.openDrawerInterface = openDrawerInterface;
        this.fragmentManager = fragmentManager;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_ecanteen_menu_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();

        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        ImageView menuBtn = view.findViewById(R.id.menuIcon);
        ImageView stopEditBtn = view.findViewById(R.id.stopEditBtn);
        ImageView addBtn = view.findViewById(R.id.addCategoryBtn);
        menuFragmentContainer = view.findViewById(R.id.menu_fragment_container);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoriesRef = database.getReference("ecanteen/categories");

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(categoriesRef, Category.class).build();
        categoryAdaptor = new CategoryAdaptor(options, getContext(), this, getChildFragmentManager(), true);
        categoryRecyclerView.setAdapter(categoryAdaptor);

        if (savedInstanceState != null) {
            onCategoryClick(savedInstanceState.getString("category"));
        }

        String role = requireActivity().getSharedPreferences("com.iicportal", 0).getString("role", "Student");

        if (!role.equals("Admin") && !role.equals("Vendor")) {
            stopEditBtn.setVisibility(View.GONE);
        } else {
            stopEditBtn.setVisibility(View.VISIBLE);
            stopEditBtn.setOnClickListener(v -> {
                ECanteenMenuFragment eCanteenMenuFragment = new ECanteenMenuFragment(openDrawerInterface, fragmentManager);
                fragmentManager.beginTransaction().replace(R.id.ecanteen_fragment_container, eCanteenMenuFragment).commit();
            });
        }

        menuBtn.setOnClickListener(v -> {
            if (openDrawerInterface != null) {
                openDrawerInterface.openDrawer();
            }
        });

        addBtn.setOnClickListener(v -> {
            AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
            addCategoryDialogFragment.show(getChildFragmentManager(), "AddCategoryDialogFragment");
        });
    }

    @Override
    public void onCategoryClick(String category) {
        this.category = category;
        this.context = requireContext();
        MenuFragment menuFragment = new MenuFragment(category, context, getChildFragmentManager(), true);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container, menuFragment).commit();
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("category", category);
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

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (category != null) {
            MenuFragment menuFragment = new MenuFragment(category, context, getChildFragmentManager(), true);
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.menu_fragment_container, menuFragment).commit();
        }
    }
}
