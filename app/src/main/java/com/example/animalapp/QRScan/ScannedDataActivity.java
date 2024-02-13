package com.example.animalapp.QRScan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animalapp.Arduino.ArduinoDataActivity;
import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityArduinoDataBinding;
import com.example.animalapp.databinding.ActivityScannedDataBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ScannedDataActivity extends AppCompatActivity {
    TextView t1,t2;
    private DatabaseReference reference;
    private ActivityScannedDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannedDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

//        t2 = findViewById(R.id.text);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("scannedData")) {
            String scannedData = extras.getString("scannedData");
//            t2.setText(scannedData);
        } else {
//            t2.setText("No scanned data available");
        }

//        t1 = findViewById(R.id.text);
        String animalUid = getIntent().getStringExtra("scannedData");

        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
        getAnimalDetails(animalUid);
    }
    private void getAnimalDetails(String animalUid) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String[] animalTypes = getResources().getStringArray(R.array.animal_types);

        for (String animalType : animalTypes) {
            DatabaseReference typeReference = reference.child(animalType).child(animalUid);

            typeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Animals animal = snapshot.getValue(Animals.class);
                        if (animal != null) {
                            displayAnimalDetails(animal);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ScannedDataActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void displayAnimalDetails(Animals animal) {

        if (animal != null) {
            String name = animal.getName();
            String type = animal.getAnimalType();
            String gender = animal.getGender();
            String age = animal.getAge();

            binding.animalName.setText(name);
            binding.animalAge.setText(age);
            binding.animalType.setText(type);
            binding.animalGender.setText(gender);


            try{
                Picasso.get().load(animal.getImage()).placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.animalImage);
            }catch (Exception e){
                e.getMessage();
            }

//            binding.genderTextView.setText(gender);
        }
    }
}