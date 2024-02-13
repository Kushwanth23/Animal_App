//package com.example.animalapp.ui.home;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;
//
//import com.example.animalapp.Model.Animals;
//import com.example.animalapp.R;
//import com.example.animalapp.databinding.ActivityAnimalAddBinding;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//public class AnimalAddActivity extends AppCompatActivity {
//
//    ActivityAnimalAddBinding binding;
//    RadioGroup animal_type;
//    StorageReference storageReference;
//    DatabaseReference reference;
//    FirebaseUser user;
//
//    private Uri imageUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityAnimalAddBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
//        storageReference = FirebaseStorage.getInstance().getReference().child("Animals Images");
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        animal_type.clearCheck();
//
//        animal_type.setOnCheckedChangeListener(
//                new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                        RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
//                    }
//                }
//        );
//
//        binding.productImage.setOnClickListener(view -> {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, 99);
//        });
//
//        binding.submit.setOnClickListener(view ->  {
//            String aName = binding.animalName.getText().toString();
//            String aAgeText = binding.animalAge.getText().toString();
//            int aAge = aAgeText.isEmpty() ? 0 : Integer.parseInt(aAgeText);
//
//            int selectedId = animal_type.getCheckedRadioButtonId();
//            String animalType = getAnimalType(selectedId);
//
//                if(aName.isEmpty()||aAgeText.isEmpty())
//
//            {
//                Toast.makeText(AnimalAddActivity.this, "All Fields are Required!", Toast.LENGTH_SHORT).show();
//            } else if(imageUri ==null)
//
//            {
//                Toast.makeText(AnimalAddActivity.this, "Please select image!", Toast.LENGTH_SHORT).show();
//            } else if(animalType ==null)
//
//            {
//                Toast.makeText(AnimalAddActivity.this, "Select the Animal Type!", Toast.LENGTH_SHORT).show();
//            } else
//            {
//                uploadImage(aName, aAge, animalType);
//            }
////                int selectedId = animal_type.getCheckedRadioButtonId();
////                if (selectedId == -1) {
////                    Toast.makeText(AnimalAddActivity.this, "Select the Animal Type", Toast.LENGTH_SHORT).show();
////                } else {
////                    RadioButton radioButton = animal_type.findViewById(selectedId);
////
////                }
//        });
//    }
//
//    private void uploadImage(String aName, Integer aAge, String animalType) {
//        ProgressDialog progressDialog = new ProgressDialog(AnimalAddActivity.this);
//        progressDialog.setMessage("Uploading......");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + ".jpg");
//
//        storageReference1.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
//                storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String imageUrl = uri.toString();
//                    String id = reference.push().getKey();
//
//                    Animals animals = new Animals();
//                    animals.setImage(imageUrl);
//                    animals.setName(aName);
//                    animals.setAge(aAge);
//                    animals.setId(id);
//
//                    if (animalType != null) {
//                        switch (animalType) {
//                            case "Cows":
//                                animals.setB1(true);
//                                break;
//                            case "Calf":
//                                animals.setB2(true);
//                                break;
//                            case "Buffalo":
//                                animals.setB3(true);
//                                break;
//                            case "Dogs":
//                                animals.setB4(true);
//                                break;
//                            case "Sheep":
//                                animals.setB5(true);
//                                break;
//                            case "Goat":
//                                animals.setB6(true);
//                                break;
//                        }
//                    }
//
//
//                    reference.child(id).setValue(animals).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            progressDialog.dismiss();
//                            Toast.makeText(AnimalAddActivity.this, "Product uploaded!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            progressDialog.dismiss();
//                            Toast.makeText(AnimalAddActivity.this, "Failed: " +
//                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                }).addOnFailureListener(e -> {
//                    progressDialog.dismiss();
//                    Toast.makeText(AnimalAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }));
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 99 && resultCode == RESULT_OK && data != null) {
//            imageUri = data.getData();
//            binding.productImage.setImageURI(imageUri);
//        } else {
//            Toast.makeText(AnimalAddActivity.this, "Please select image.....", Toast.LENGTH_SHORT).show();
//        }
//    }
//}


package com.example.animalapp.ui.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.animalapp.LoginSignUp.SuccessScreenActivity;
import com.example.animalapp.Model.Animals;
import com.example.animalapp.QRScan.QRGeneratorActivity;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityAnimalAddBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AnimalAddActivity extends AppCompatActivity {

    ActivityAnimalAddBinding binding;
//    RadioGroup animal_type;
    Spinner spinnerAnimal;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseUser user;
    RadioGroup radioGroup;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
        storageReference = FirebaseStorage.getInstance().getReference().child("Animals");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        spinnerAnimal = findViewById(R.id.spinner_animal);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.animal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnimal.setAdapter(adapter);

        radioGroup = findViewById(R.id.radio_gender);

        binding.productImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 99);
        });

        Button clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(view -> {
            binding.productImage.setImageResource(R.drawable.add);
            imageUri = null;
            binding.animalName.getText().clear();
            binding.animalAge.getText().clear();
            Spinner spinnerAnimal = findViewById(R.id.spinner_animal);
            spinnerAnimal.setSelection(0);
            radioGroup.clearCheck();
        });

        binding.submit.setOnClickListener(view -> {
            String aName = binding.animalName.getText().toString();
            String aAgeText = binding.animalAge.getText().toString();
            String gender = getGender();
            String animalType = getAnimalType();

            if (aName.isEmpty() || aAgeText.isEmpty()) {
                Toast.makeText(AnimalAddActivity.this, "All Fields are Required!", Toast.LENGTH_SHORT).show();
            } else if (imageUri == null) {
                Toast.makeText(AnimalAddActivity.this, "Please select an image!", Toast.LENGTH_SHORT).show();
            } else if (animalType == null) {
                Toast.makeText(AnimalAddActivity.this, "Select the Animal Type", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage(aName, aAgeText, animalType, gender);
            }

        });
    }

    private void uploadImage(String aName, String aAgeText, String animalType, String gender) {
        ProgressDialog progressDialog = new ProgressDialog(AnimalAddActivity.this);
        progressDialog.setMessage("Uploading......");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + ".jpg");

        storageReference1.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String id = reference.push().getKey();

                    Animals animals = new Animals();
                    animals.setImage(imageUrl);
                    animals.setName(aName);
                    animals.setAge(aAgeText);
                    animals.setId(id);
                    animals.setOwner(user.getUid());
                    animals.setAnimalType(animalType);
                    animals.setGender(gender);

                    DatabaseReference animalTypeRef = reference.child(animalType);
                    //animalTypeRef.child(id).setValue(animals);

//                    setAnimalType(animals, animalType);

                    animalTypeRef.child(id).setValue(animals).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(AnimalAddActivity.this, "Animal Data uploaded!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AnimalAddActivity.this, AnimalDetailsActivity.class);
                            intent.putExtra("animal", animals);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AnimalAddActivity.this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AnimalAddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private String getAnimalType() {
        Spinner spinnerAnimal = findViewById(R.id.spinner_animal);
        return spinnerAnimal.getSelectedItem().toString();
    }

//    private void setAnimalType(Animals animals, String animalType) {
//        if (animalType != null) {
//            switch (animalType) {
//                case "Cows":
//                    animals.setB1(binding.cows);
//                    break;
//                case "Calf":
//                    animals.setB2(binding.calf);
//                    break;
//                case "Buffalo":
//                    animals.setB3(binding.buffalo);
//                    break;
//                case "Dogs":
//                    animals.setB4(binding.dogs);
//                    break;
//                case "Sheep":
//                    animals.setB5(binding.sheep);
//                    break;
//                case "Goat":
//                    animals.setB6(binding.goat);
//                    break;
//            }
//        }
//    }

    private String getGender(){
        int selectedGenderId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedGenderId);
        return radioButton != null ? radioButton.getText().toString() : "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.productImage.setImageURI(imageUri);
        } else {
            Toast.makeText(AnimalAddActivity.this, "Please select an image.....", Toast.LENGTH_SHORT).show();
        }
    }
}
