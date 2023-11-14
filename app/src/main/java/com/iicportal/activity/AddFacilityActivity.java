package com.iicportal.activity;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iicportal.R;
import com.iicportal.models.Facilities;

public class AddFacilityActivity extends AppCompatActivity {
    Facilities facility ;
    FirebaseDatabase database;
    DatabaseReference menuRef;
    Uri imageUri;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facility);
        Context context = this;
        facility = new Facilities();

        database = MainActivity.database;

        FirebaseStorage storage = FirebaseStorage.getInstance();

        TextInputEditText nameEditText = findViewById(R.id.name);
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

        database.getReference("facilities/facility/").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String[] facilities = new String[(int) task1.getResult().getChildrenCount()];
                int i = 0;
                for (DataSnapshot snapshot : task1.getResult().getChildren()) {
                    facilities[i] = snapshot.child("name").getValue(String.class);
                    i++;
                }
            } else {
                Log.e("EditFacilityDialogFragment", "Error getting data", task1.getException());
            }
        });

        PopupMenu popupMenu = new PopupMenu(context, editImageBtn);
        popupMenu.getMenuInflater().inflate(R.menu.facility_image_menu, popupMenu.getMenu());

        editImageBtn.setOnClickListener(v -> {
            popupMenu.show();
        });

        addImageBtn.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());

        });

        popupMenu.setOnMenuItemClickListener(item ->{
        final int edit = R.id.edit_facility_image;
        final int delete = R.id.delete_facility_image;

        if (item.getItemId() == edit){
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            editImageBtn.setVisibility(View.VISIBLE);
            addImageBtn.setVisibility(View.GONE);
        } else if (item.getItemId() == delete){
            imageUri = null;
            fileName = null;
            image.setImageResource(R.color.grey_200);
            addImageBtn.setVisibility(View.VISIBLE);
            editImageBtn.setVisibility(View.GONE);
        } else {
            Log.d("EditFacilityDialogFragment", "Error getting facility");
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
            String price = priceEditText.getText().toString();

            if (name.isEmpty()) {
                nameEditText.setError("Name is required");
                nameEditText.requestFocus();
                return;
            } else if (price.isEmpty()) {
                priceEditText.setError("Price is required");
                priceEditText.requestFocus();
                return;
            } else if (imageUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Please select an image");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                String fileName = imageUri.getPath().substring(imageUri.getPath().lastIndexOf('/') + 1);
                Log.d("AddFacilityActivity", "File name: " + fileName);
                StorageReference newImageRef = storage.getReference().child("facilities/" + fileName);
                newImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        facility.setName(name);
                        facility.setPrice(Double.parseDouble(price));
                        facility.setImage(uri.toString());
                        menuRef = database.getReference("facilities/facility/");
                        menuRef.push().setValue(facility);
                        finish();
                    });
                });
            }
        });
    }
}
