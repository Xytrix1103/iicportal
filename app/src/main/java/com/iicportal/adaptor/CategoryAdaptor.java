package com.iicportal.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.iicportal.R;
import com.iicportal.models.Category;

public class CategoryAdaptor extends FirebaseRecyclerAdapter<Category, CategoryAdaptor.CategoryViewHolder> {
    Context context;
    OnCategoryClickListener onCategoryClickListener;


    public CategoryAdaptor(@NonNull FirebaseRecyclerOptions<Category> options, Context context, OnCategoryClickListener onCategoryClickListener) {
        super(options);
        this.context = context;
        this.onCategoryClickListener = onCategoryClickListener;
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
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category, parent, false);
        return new CategoryViewHolder(view);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
        String getCategory();
    }
}