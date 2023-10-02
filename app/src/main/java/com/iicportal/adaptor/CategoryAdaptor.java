package com.iicportal.adaptor;

import android.content.Context;
import android.content.SharedPreferences;
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

    SharedPreferences sharedPreferences;

    public CategoryAdaptor(@NonNull FirebaseRecyclerOptions<Category> options, Context context) {
        super(options);
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
    }

    public void onDataChanged() {
        super.onDataChanged();
        if (super.getItemCount() > 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("category", super.getItem(0).getCategory());
            editor.apply();
        }

        notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
        holder.categoryName.setText(model.getCategory());
        holder.itemView.setSelected(sharedPreferences.getString("category", "").equals(model.getCategory()));

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("category", model.getCategory());
            editor.apply();

            Log.d("CategoryAdaptor", "Category changed to " + sharedPreferences.getString("category", ""));
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
}