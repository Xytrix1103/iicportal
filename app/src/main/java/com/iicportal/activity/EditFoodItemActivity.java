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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iicportal.R;
import com.iicportal.models.FoodItem;

public class EditFoodItemActivity extends AppCompatActivity {
    FoodItem foodItem;
    FirebaseDatabase database;
    DatabaseReference menuRef;
    String key;
    Uri imageUri = null;
    String fileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_item);
        Context context = this;
        key = getIntent().getStringExtra("key");
        foodItem = new FoodItem();

        database = MainActivity.database;
        DatabaseReference itemRef = database.getReference("ecanteen/fooditems/").child(key);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        TextInputEditText nameEditText = findViewById(R.id.name);
        TextInputEditText descriptionEditText = findViewById(R.id.desc);
        TextInputEditText priceEditText = findViewById(R.id.price);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        ImageView image = findViewById(R.id.image);
        ImageView editImageBtn = findViewById(R.id.editImageBtn);
        ImageView addImageBtn = findViewById(R.id.addImageBtn);

        ImageView backBtnIcon = findViewById(R.id.backBtnIcon);

        backBtnIcon.setOnClickListener(v -> {
            finish();
        });

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        imageUri = uri;
                        fileName = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
                        MainActivity.loadImage(uri.toString(), image);
                        addImageBtn.setVisibility(View.GONE);
                        editImageBtn.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        itemRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("EditFoodItemDialogFragment", "Error getting data");
            } else {
                Log.d("EditFoodItemDialogFragment", "Data: " + task.getResult().getValue());

                foodItem = task.getResult().getValue(FoodItem.class);

                nameEditText.setText(foodItem.getName());
                descriptionEditText.setText(foodItem.getDescription());
                priceEditText.setText(String.valueOf(foodItem.getPrice()));
                MainActivity.loadImage(foodItem.getImage(), image);
                Log.d("EditFoodItemDialogFragment", "Image: " + foodItem.getImage());
                imageUri = Uri.parse(foodItem.getImage());
                Log.d("EditFoodItemDialogFragment", "Image URI: " + imageUri);
                editImageBtn.setVisibility(View.VISIBLE);
                addImageBtn.setVisibility(View.GONE);
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
                categorySpinner.setSelection(adapter.getPosition(foodItem.getCategory()));
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
        ImageView deleteBtn = findViewById(R.id.deleteBtn);

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
                foodItem.setName(name);
                foodItem.setDescription(description);
                foodItem.setPrice(Double.parseDouble(price));
                foodItem.setCategory(categorySpinner.getSelectedItem().toString());

                if (imageUri != null) {
                    String fileName = imageUri.getPath().substring(imageUri.getPath().lastIndexOf('/') + 1);
                    Log.d("AddFoodItemActivity", "fileName: " + fileName);
                    StorageReference newImageRef = storage.getReference("canteen_food_images/" + fileName);
                    if (foodItem.getImage() != imageUri.toString()) {
                        newImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                foodItem.setImage(uri.toString());
                                itemRef.setValue(foodItem);
                                finish();
                            });
                        });
                    } else {
                        itemRef.setValue(foodItem);
                        finish();
                    }
                } else {
                    itemRef.child("image").setValue(null);
                }
            }
        });

        deleteBtn.setOnClickListener(v -> {
            itemRef.removeValue();
            finish();
        });
    }
}
