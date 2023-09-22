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

import com.iicportal.R;

import java.util.List;

public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.CategoryViewHolder> {

    Context context;
    List<String> categories;

    SharedPreferences sharedPreferences;

    public CategoryAdaptor(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
        sharedPreferences = context.getSharedPreferences("com.iicportal", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category, parent, false);
        return new CategoryAdaptor.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.category.setText(categories.get(position));
        holder.itemView.setSelected(sharedPreferences.getString("category", "Sandwich").equals(categories.get(position)));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView category;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);

            itemView.setOnClickListener(v -> {
                Log.d("CategoryAdaptor", "Category changed to " + category.getText().toString());
                sharedPreferences.edit().putString("category", category.getText().toString()).apply();
                notifyDataSetChanged();
                notifyItemChanged(categories.indexOf(sharedPreferences.getString("category", "Sandwich")));
            });
        }
    }
}