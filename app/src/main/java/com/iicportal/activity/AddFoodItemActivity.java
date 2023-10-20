package com.iicportal.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iicportal.R;
import com.iicportal.models.FoodItem;

public class AddFoodItemActivity extends AppCompatActivity {
    FoodItem foodItem;
    FirebaseDatabase database;
    DatabaseReference menuRef;
    Uri imageUri;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_item);
        Context context = this;
        foodItem = new FoodItem();

        database = MainActivity.database;

        FirebaseStorage storage = FirebaseStorage.getInstance();

        TextInputEditText nameEditText = findViewById(R.id.name);
        TextInputEditText descriptionEditText = findViewById(R.id.desc);
        TextInputEditText priceEditText = findViewById(R.id.price);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        ImageView image = findViewById(R.id.image);
        ImageView editImageBtn = findViewById(R.id.editImageBtn);
        ImageView addImageBtn = findViewById(R.id.addImageBtn);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        imageUri = uri;
                        fileName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
                        Glide.with(context).load(uri).into(image);
                        addImageBtn.setVisibility(View.GONE);
                        editImageBtn.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        database.getReference("ecanteen/categories/").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String[] categories = new String[(int) task1.getResult().getChildrenCount()];
                int i = 0;
                for (DataSnapshot snapshot : task1.getResult().getChildren()) {
                    categories[i] = snapshot.child("category").getValue(String.class);
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                categorySpinner.setSelection(0);
            } else {
                Log.d("EditFoodItemDialogFragment", "Error getting data");
            }
        });

        PopupMenu popupMenu = new PopupMenu(this, editImageBtn);
        popupMenu.getMenuInflater().inflate(R.menu.food_item_image_menu, popupMenu.getMenu());

        editImageBtn.setOnClickListener(v -> {
            popupMenu.show();
        });

        addImageBtn.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        popupMenu.setOnMenuItemClickListener(item -> {
            final int edit = R.id.edit_food_item_image;
            final int remove = R.id.remove_food_item_image;

            if (item.getItemId() == edit) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
                editImageBtn.setVisibility(View.VISIBLE);
                addImageBtn.setVisibility(View.GONE);
            } else if (item.getItemId() == remove) {
                imageUri = null;
                fileName = null;
                image.setImageResource(R.color.grey_300);
                editImageBtn.setVisibility(View.GONE);
                addImageBtn.setVisibility(View.VISIBLE);
            } else {
                Log.d("EditFoodItemDialogFragment", "Error getting menu item");
            }

            return true;
        });

        TextView cancelBtn = findViewById(R.id.cancelBtn);
        TextView saveBtn = findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        saveBtn.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String price = priceEditText.getText().toString();

            if (name.isEmpty()) {
                nameEditText.setError("Name cannot be empty");
                nameEditText.requestFocus();
            } else if (description.isEmpty()) {
                descriptionEditText.setError("Description cannot be empty");
                descriptionEditText.requestFocus();
            } else if (price.isEmpty()) {
                priceEditText.setError("Price cannot be empty");
                priceEditText.requestFocus();
            } else if (imageUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Please select an image");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                String fileName = imageUri.getPath().substring(imageUri.getPath().lastIndexOf('/') + 1);
                Log.d("AddFoodItemActivity", "fileName: " + fileName);
                StorageReference newImageRef = storage.getReference("canteen_food_images/" + fileName);
                newImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        foodItem.setName(name);
                        foodItem.setDescription(description);
                        foodItem.setPrice(Double.parseDouble(price));
                        foodItem.setCategory(categorySpinner.getSelectedItem().toString());
                        foodItem.setImage(uri.toString());
                        menuRef = database.getReference("ecanteen/fooditems/");
                        menuRef.push().setValue(foodItem);
                        finish();
                    });
                });
            }
        });
    }
}