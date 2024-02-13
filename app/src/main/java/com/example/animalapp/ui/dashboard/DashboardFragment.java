package com.example.animalapp.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.animalapp.Adapters.ItemAdapter;
import com.example.animalapp.Model.Animals;
import com.example.animalapp.R;
import com.example.animalapp.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardFragment extends Fragment {
    DatabaseReference reference;
    ArrayList<Animals> list = new ArrayList<>();
    ItemAdapter adapter;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        reference = FirebaseDatabase.getInstance().getReference().child("Animals");
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setHasFixedSize(true);

        new Handler().postDelayed(() -> getAnimals(), 300);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getAnimals() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<String> animalTypes = getAnimalTypes(); // Load animal types from resources

        for (String animalType : animalTypes) {
            reference.child(animalType).orderByChild("owner").equalTo(currentUserUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot animalSnapshot : snapshot.getChildren()) {
                                    Animals animal = animalSnapshot.getValue(Animals.class);
                                    if (animal != null) {
                                        list.add(animal);
                                    }
                                }
                            }
                            checkAndDisplayList();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Error: " +
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private List<String> getAnimalTypes() {
        return Arrays.asList(getResources().getStringArray(R.array.animal_types));
    }

    private void checkAndDisplayList() {
        binding.progressBar.setVisibility(View.GONE);

        if (!list.isEmpty()) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
            binding.recyclerView.setLayoutManager(gridLayoutManager);

            adapter = new ItemAdapter(requireContext(), list);
            binding.recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(requireContext(), "You have no uploaded animals!", Toast.LENGTH_SHORT).show();
        }
    }
}
