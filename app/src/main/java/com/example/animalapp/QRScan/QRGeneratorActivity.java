package com.example.animalapp.QRScan;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.Utilities.PreferenceManager;
import com.example.animalapp.databinding.ActivityQrgeneratorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRGeneratorActivity extends AppCompatActivity {

    ActivityQrgeneratorBinding binding;
    DatabaseReference reference;
    FirebaseUser user;
    PreferenceManager preferenceManager;

    Animals models;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);
        Button btnGenerate = findViewById(R.id.btnGenerate);
        EditText etText = findViewById(R.id.etText);
        ImageView imageCode = findViewById(R.id.imageCode);

        preferenceManager = new PreferenceManager(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
        user = FirebaseAuth.getInstance().getCurrentUser();

        getAnimalData();

        btnGenerate.setOnClickListener(v -> {
            String myText = etText.getText().toString().trim();
            MultiFormatWriter mWriter = new MultiFormatWriter();
            try {
                BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400, 400);
                BarcodeEncoder mEncoder = new BarcodeEncoder();
                Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
                imageCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(etText.getApplicationWindowToken(), 0);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });


    }

    private void getAnimalData() {
        reference.child("Buffalo").child("NhQjGxgXK6mnzhNM0eI").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Animals model = snapshot.getValue(Animals.class);
                    if (model != null) {
                        binding.email.setText(model.getId());
                    } else {
                        Toast.makeText(QRGeneratorActivity.this, "No Exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QRGeneratorActivity.this, "No Exists2", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}