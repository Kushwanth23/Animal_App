package com.example.animalapp.Arduino;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityAnimalAddBinding;
import com.example.animalapp.databinding.ActivityArduinoDataBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArduinoDataActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityArduinoDataBinding binding;
    private TextView heartRateTextView;
    private TextView spo2TextView;
    private TextView temperatureTextView;
    private Handler handler;
    private GoogleMap googleMap;
    TextView name, age, type;
    ImageView profile;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArduinoDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        heartRateTextView = findViewById(R.id.heartRateTextView);
        spo2TextView = findViewById(R.id.spo2TextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        name = findViewById(R.id.animal_name);
        age = findViewById(R.id.animal_age);
        type = findViewById(R.id.animal_type);
        profile = findViewById(R.id.animalImage);

        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
        String animalUid = "-NlWM10XIIccGTBPXVxz";
        getAnimalDetails(animalUid);

        handler = new Handler(Looper.getMainLooper());

        new FetchDataTask().execute();
    }

    private void getAnimalDetails(String animalUid) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String[] animalTypes = getResources().getStringArray(R.array.animal_types);

        for (String animalType : animalTypes) {
            DatabaseReference typeReference = reference.child(animalType).child(animalUid);

            typeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Animals animal = snapshot.getValue(Animals.class);
                        if (animal != null) {
                            displayAnimalDetails(animal);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ArduinoDataActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void displayAnimalDetails(Animals animal) {

        if (animal != null) {
            String name = animal.getName();
            String age = (animal.getAge() != null) ? String.valueOf(animal.getAge()) : "N/A";
            String type = animal.getAnimalType();
            String gender = animal.getGender();

            binding.animalName.setText(name);
            binding.animalAge.setText(age);
            binding.animalType.setText(type);

            try{
                Picasso.get().load(animal.getImage()).placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.animalImage);
            }catch (Exception e){
                e.getMessage();
            }

//            binding.genderTextView.setText(gender);

        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(16.4821, 80.6914);
        googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String urlString = "https://api.thingspeak.com/channels/465109/feeds/last.json";

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                // Extract values from the JSON response
                String field1Value = jsonObject.getString("field1");
                String field2Value = jsonObject.getString("field2");
                String field3Value = jsonObject.getString("field3");

                heartRateTextView.setText(field1Value);
                spo2TextView.setText(field2Value);
                temperatureTextView.setText(field3Value);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new FetchDataTask().execute();
                    }
                }, 100);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
