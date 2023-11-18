package com.iicportal.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
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
        setContentView(R.layout.activity_add_food_item);
        String category = getIntent().getStringExtra("category");
        foodItem = new FoodItem();

        database = MainActivity.database;

        FirebaseStorage storage = FirebaseStorage.getInstance();

        TextView headerText = findViewById(R.id.headerText);
        headerText.setText("Add " + category.substring(0, 1).toUpperCase() + category.substring(1));
        TextInputEditText nameEditText = findViewById(R.id.name);
        TextInputEditText descriptionEditText = findViewById(R.id.desc);
        TextInputEditText priceEditText = findViewById(R.id.price);
        ImageView image = findViewById(R.id.image);
        ImageView editImageBtn = findViewById(R.id.editImageBtn);
        ImageView addImageBtn = findViewById(R.id.addImageBtn);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        imageUri = uri;
                        fileName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
                        MainActivity.loadImage(uri.toString(), image, R.drawable.baseline_image_placeholder);
                        addImageBtn.setVisibility(View.GONE);
                        editImageBtn.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
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

        ImageView backBtnIcon = findViewById(R.id.backBtnIcon);

        backBtnIcon.setOnClickListener(v -> {
            finish();
        });

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
                        foodItem.setCategory(category);
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