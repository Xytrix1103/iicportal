package com.iicportal.adaptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iicportal.R;
import com.iicportal.fragments.EditCategoryDialogFragment;
import com.iicportal.models.Category;

public class CategoryAdaptor extends FirebaseRecyclerAdapter<Category, CategoryAdaptor.CategoryViewHolder> {
    Context context;
    OnCategoryClickListener onCategoryClickListener;
    FragmentManager fragmentManager;
    boolean isEdit = false;


    public CategoryAdaptor(@NonNull FirebaseRecyclerOptions<Category> options, Context context, OnCategoryClickListener onCategoryClickListener, FragmentManager fragmentManager, boolean isEdit) {
        super(options);
        this.context = context;
        this.onCategoryClickListener = onCategoryClickListener;
        this.fragmentManager = fragmentManager;
        this.isEdit = isEdit;
    }

    public void onDataChanged() {
        super.onDataChanged();

        if (super.getItemCount() > 0) {
            onCategoryClickListener.onCategoryClick(getItem(0).getCategory());
        }

        notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
        holder.categoryName.setText(model.getCategory());
        holder.itemView.setSelected(model.getCategory().equals(onCategoryClickListener.getCategory()));

        holder.itemView.setOnClickListener(v -> {
            onCategoryClickListener.onCategoryClick(model.getCategory());
            Log.d("CategoryAdaptor", "Category changed to " + model.getCategory());
            notifyDataSetChanged();
        });

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.iicportal", 0);
        String role = sharedPreferences.getString("role", "Student");

        if (!role.equals("Admin") && !role.equals("Vendor") || !isEdit) {
            holder.editCategory.setVisibility(View.GONE);
        } else {
            holder.editCategory.setVisibility(View.VISIBLE);
            holder.editCategory.setOnClickListener(v -> {
                EditCategoryDialogFragment editCategoryDialogFragment = new EditCategoryDialogFragment(model.getCategory());
                editCategoryDialogFragment.show(fragmentManager, "EditCategoryDialogFragment");
            });
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryViewHolder(view);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView editCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category);
            editCategory = itemView.findViewById(R.id.editBtn);
            context = itemView.getContext();

            SharedPreferences sharedPreferences = context.getSharedPreferences("com.iicportal", 0);
            String role = sharedPreferences.getString("role", "Student");

            if (!role.equals("Admin") && !role.equals("Vendor")) {
                editCategory.setVisibility(View.GONE);
            } else {
                editCategory.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
        String getCategory();
    }
}