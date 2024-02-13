package com.example.animalapp.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityAnimalDetailsBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class AnimalDetailsActivity extends AppCompatActivity {
    ActivityAnimalDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimalDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Animals animal = getIntent().getParcelableExtra("animal");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> navigateToHomeFragment());

        if (animal != null) {
            ImageView animalImage = findViewById(R.id.animalImage);
            TextView animalName = findViewById(R.id.animalName);
            TextView animalAge = findViewById(R.id.animalAge);
            TextView animalType = findViewById(R.id.animalType);
            TextView animalGender = findViewById(R.id.animalgender);
            ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);
            Button btnSaveQRCode = findViewById(R.id.btnSaveQRCode);

            Picasso.get().load(animal.getImage()).into(animalImage);
            animalName.setText(animal.getName());
            animalAge.setText(animal.getAge());
            animalType.setText(animal.getAnimalType() != null ? animal.getAnimalType() : "N/A");
            animalGender.setText(animal.getGender());
            generateAndDisplayQRCode(animal.getId(), qrCodeImageView);

            btnSaveQRCode.setOnClickListener(view -> {
                saveQRCodeToGallery(animal.getId(), qrCodeImageView, AnimalDetailsActivity.this);
            });

        } else {
            Toast.makeText(this, "Animal data not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateToHomeFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void navigateToHomeFragment() {
        getSupportFragmentManager().popBackStack();
        finish();
    }
    private void generateAndDisplayQRCode(String text, ImageView imageView) {
        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            imageView.setImageBitmap(mBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    private void saveQRCodeToGallery(String fileName, ImageView imageView, Context context) {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, fileName + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Toast.makeText(context, "QR Code saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}
