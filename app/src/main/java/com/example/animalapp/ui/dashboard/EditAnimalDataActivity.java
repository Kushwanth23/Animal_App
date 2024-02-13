package com.example.animalapp.ui.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityEditAnimalDataBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class EditAnimalDataActivity extends AppCompatActivity {
    ActivityEditAnimalDataBinding binding;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseUser user;
    private Uri imageUri;
    private boolean editMode = false;
    private MenuItem editMenuItem;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedImageBitmap;
    private String animalId, animalType;
    private Animals animalModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAnimalDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
        storageReference = FirebaseStorage.getInstance().getReference().child("Animals");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        EditText nameEditText = binding.animalName;
        EditText ageEditText = binding.animalAge;
        ImageView profile = binding.profile;
        RadioGroup genderRadioGroup = binding.radioGender;
        RadioButton maleRadioButton = binding.radioMale;
        RadioButton femaleRadioButton = binding.radioFemale;
        Spinner spinnerAnimal = binding.spinnerAnimal;
        EditText description = binding.animalDescription;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.animal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        spinnerAnimal.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("animalId")) {
            animalId = intent.getStringExtra("animalId");
            animalType = intent.getStringExtra("animalType");


            reference.child(animalType).child(animalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    animalModel = dataSnapshot.getValue(Animals.class);
                    if (animalModel != null) {
                        nameEditText.setText(animalModel.getName());
                        ageEditText.setText(animalModel.getAge());
                        description.setText(animalModel.getDescription());

                        String gender = animalModel.getGender();
                        if ("Male".equals(gender)) {
                            maleRadioButton.setChecked(true);
                        } else if ("Female".equals(gender)) {
                            femaleRadioButton.setChecked(true);
                        }

                        // Set the selected animal in the spinner
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerAnimal.getAdapter();
                        if (adapter != null) {
                            int position = adapter.getPosition(animalModel.getAnimalType());
                            spinnerAnimal.setSelection(position);
                        }

                        // Load image using Picasso or your preferred image loading library
                        // For example, if using Picasso:
                        Picasso.get().load(animalModel.getImage()).into(profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditAnimalDataActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            if (editMode) {
                updateFirebaseData();
                editMode = false;
                setEditableFields(false);
                updateMenuIcons();
            }
        });

        profile.setOnClickListener(v -> {
            if (editMode) {
                openGallery();
            }
        });

        setEditableFields(false);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                binding.profile.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_memu_animal_data_display, menu);
        editMenuItem = menu.findItem(R.id.editMenuItem);
        updateMenuIcons();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.editMenuItem) {
            editMode = true;
            setEditableFields(editMode);
            updateMenuIcons();
            return true;
        } else if (itemId == R.id.submitMenuItem) { // Added condition for submit button
            if (editMode) {
                updateFirebaseData();
                editMode = false;
                setEditableFields(false);
                updateMenuIcons();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setEditableFields(boolean editable) {
        EditText nameEditText = findViewById(R.id.animal_name);
        EditText ageEditText = findViewById(R.id.animal_age);
        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);
        Spinner spinnerAnimal = findViewById(R.id.spinner_animal);
        EditText description = findViewById(R.id.animal_description);

        nameEditText.setEnabled(editable);
        ageEditText.setEnabled(editable);
        description.setEnabled(editable);

        for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
            genderRadioGroup.getChildAt(i).setEnabled(editable);
        }

        spinnerAnimal.setEnabled(false);
    }

    private void updateFirebaseData() {
        EditText nameEditText = findViewById(R.id.animal_name);
        EditText ageEditText = findViewById(R.id.animal_age);
        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        Spinner spinnerAnimal = findViewById(R.id.spinner_animal);
        EditText description = findViewById(R.id.animal_description);

        String updatedAnimalType = spinnerAnimal.getSelectedItem().toString();

        animalModel.setName(nameEditText.getText().toString());
        animalModel.setAge(ageEditText.getText().toString());
        animalModel.setGender(selectedRadioButton.getText().toString());
        animalModel.setDescription(description.getText().toString());
        // Update other fields as needed

        DatabaseReference animalRef = reference.child(animalType).child(animalId);
        animalRef.setValue(animalModel)
                .addOnSuccessListener(aVoid -> {
                    if (imageUri != null) {
                        updateAnimalImage();
                    } else {
                        onUploadSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditAnimalDataActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAnimalImage() {
        // Upload the new image to Firebase Storage
        StorageReference imageRef = storageReference.child(animalType).child(animalId + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the updated download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the image URL in the Firebase Database
                        animalModel.setImage(uri.toString());
                        DatabaseReference animalImageRef = reference.child(animalType).child(animalId).child("image");
                        animalImageRef.setValue(uri.toString());
                        onUploadSuccess();
                    });
                })
                .addOnFailureListener(e -> {
                    // Image upload failed
                    Toast.makeText(EditAnimalDataActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void onUploadSuccess() {
        Toast.makeText(EditAnimalDataActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();

        // Open DashboardFragment
        openDashboardFragment();
    }

    private void updateMenuIcons() {
        if (editMenuItem != null) {
            editMenuItem.setVisible(!editMode);
        }
    }

    private void openDashboardFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openDashboardFragment", true);
        startActivity(intent);
        finish();
    }
}