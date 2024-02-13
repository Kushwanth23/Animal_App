package com.example.animalapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.animalapp.Arduino.*;
import com.example.animalapp.LoginSignUp.EmailResetActivity;
import com.example.animalapp.QRScan.ScannerActivity;
import com.example.animalapp.QRScan.*;
import com.example.animalapp.R;
import com.example.animalapp.databinding.FragmentHomeBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button btnQRCode = root.findViewById(R.id.btn_qr_code);
        Button btnAnimalData = root.findViewById(R.id.btn_add_animal);
        Button arduino = root.findViewById(R.id.ardunio);

        btnAnimalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AnimalAddActivity.class);
                startActivity(intent);
            }
        });

//        btnQRCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ScannerActivity.class);
//                startActivity(intent);
//            }
//        });

        btnQRCode.setOnClickListener(view -> {
            // Start QR code scanning
            initiateQRCodeScan();
        });

        arduino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ArduinoDataActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void initiateQRCodeScan() {
        IntentIntegrator.forSupportFragment(this)
                .setPrompt("Scan a barcode or QR Code")
                .setOrientationLocked(true)
                .initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                // Handle the case where scanning was canceled
                Toast.makeText(getActivity(), "Scanning canceled", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the scanned data (intentResult.getContents())
                // Start a new activity to display information based on the scanned data
                displayScannedData(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void displayScannedData(String scannedData) {
        // Start a new activity to display information based on the scanned data
        Intent intent = new Intent(getActivity(), ScannedDataActivity.class);
        intent.putExtra("scannedData", scannedData);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}