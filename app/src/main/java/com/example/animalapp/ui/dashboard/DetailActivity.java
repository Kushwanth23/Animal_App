package com.example.animalapp.ui.dashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import static com.example.animalapp.Adapters.ItemAdapter.animalsList;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    TextView name, gender, type, age;
    ImageView animalImage;
    Animals animals;
    DatabaseReference reference;
    FirebaseUser user;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        position = getIntent().getIntExtra("pos", 0);
//
        animals = animalsList.get(position);

        if (animals != null) {
            binding.animalName.setText(animals.getName());
            binding.animalAge.setText(String.valueOf(animals.getAge()));
            binding.animalGender.setText(animals.getGender());

            try {
                Picasso.get().load(animals.getImage()).placeholder(R.drawable.animals1)
                        .into(binding.animalImage);
            } catch (Exception e) {
                e.getMessage();
            }
        }
        binding.animalType.setText(animals.getAnimalType());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_memu_animal_data_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.editMenuItem) {
            showAlertDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");

        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    showEditConfirmationDialog();
                    break;
                case 1:
                    showDeleteConfirmationDialog();
                    break;
            }
        });

        builder.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder confirmationBuilder = new AlertDialog.Builder(this);
        confirmationBuilder.setTitle("Confirmation");
        confirmationBuilder.setMessage("Are you sure you want to delete?");
        confirmationBuilder.setPositiveButton("Yes", (dialog, which) -> {
            // Implement the logic to delete the item
            // You can call the method to delete the item or perform any necessary operations
            deleteAnimal();
        });

        confirmationBuilder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, do nothing or handle accordingly
        });

        confirmationBuilder.show();
    }

    private void showEditConfirmationDialog() {
        AlertDialog.Builder confirmationBuilder = new AlertDialog.Builder(this);
        confirmationBuilder.setTitle("Confirmation");
        confirmationBuilder.setMessage("Are you sure you want to edit?");

        confirmationBuilder.setPositiveButton("Yes", (dialog, which) -> {
            // User confirmed to edit, open EditAnimalDataActivity
            openEditAnimalDataActivity();
        });

        confirmationBuilder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, do nothing or handle accordingly
        });

        confirmationBuilder.show();
    }
    private void openEditAnimalDataActivity() {
        // Implement the logic to open EditAnimalDataActivity
        // You may want to pass necessary data to the EditAnimalDataActivity using Intent
        // For example, you can use animals.getId() to get the ID of the animal being edited
        // and pass it to the EditAnimalDataActivity
        Intent intent = new Intent(this, EditAnimalDataActivity.class);
        intent.putExtra("animalId", animals.getId());
        intent.putExtra("animalType", animals.getAnimalType());
        startActivity(intent);
    }

    private void deleteAnimal() {
        String animalIdToDelete = animals.getId();
        String animalType = animals.getAnimalType();
        if (animalIdToDelete != null) {
            reference.child("Animals").child(animalType).child(animalIdToDelete).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        removeFromAnimalsList();
                        showDeleteSuccessDialog();
                    })
                    .addOnFailureListener(e -> {
                        // Deletion failed
                        showDeleteFailureDialog();
                    });
        }
    }

    private void removeFromAnimalsList() {
        // Remove the corresponding item from the local list
        if (position >= 0 && position < animalsList.size()) {
            animalsList.remove(position);
        }
    }

    private void showDeleteSuccessDialog() {
        AlertDialog.Builder successBuilder = new AlertDialog.Builder(this);
        successBuilder.setTitle("Success");
        successBuilder.setMessage("Animal deleted successfully");

        successBuilder.setPositiveButton("OK", (dialog, which) -> {
            // Close DetailActivity
            finish();
        });

        successBuilder.show();
    }

    private void showDeleteFailureDialog() {
        AlertDialog.Builder failureBuilder = new AlertDialog.Builder(this);
        failureBuilder.setTitle("Error");
        failureBuilder.setMessage("Failed to delete animal");

        failureBuilder.setPositiveButton("OK", (dialog, which) -> {
            // Close DetailActivity
            finish();
        });

        failureBuilder.show();
    }

}