package com.example.animalapp.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.animalapp.LoginSignUp.LoginActivity;
import com.example.animalapp.Model.UserModel;
import com.example.animalapp.R;
import com.example.animalapp.Utilities.PreferenceManager;
import com.example.animalapp.databinding.FragmentProfileBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    TextView name, username;
    ImageView profile;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.logout.setOnClickListener(v -> showLogoutDialouge());

        name = root.findViewById(R.id.fullname_field);
        username = root.findViewById(R.id.username_field);

        profile = root.findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        fetchUserData();

        binding.account.setOnClickListener(view -> startActivity(new Intent(requireContext(), MyAccountActivity.class)));
        binding.address.setOnClickListener(view -> startActivity(new Intent(requireContext(), MyAddressActivity.class)));

        binding.aboutapp.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("About");
            builder.setMessage("App Name: "+ getResources().getString(R.string.app_name)+"\n"+"App Version: "+
                    BuildConfig.VERSION_NAME);
            builder.setCancelable(false);
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        binding.contactUs.setOnClickListener(view -> startActivity(new Intent(requireContext(), ContactActivity.class)));

        return root;
    }
    private void fetchUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);

                    if (userModel != null) {
                        if (userModel.getImage() != null && !userModel.getImage().isEmpty()) {
                            Picasso.get().load(userModel.getImage()).into(profile);
                        }
                        name.setText(userModel.getUsername());
                        username.setText(userModel.getEmil());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showLogoutDialouge(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Do You Want To Logout ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            if (getActivity() != null){
                                getActivity().finish();
                            }
                            if (preferenceManager != null) {
                                preferenceManager.clear();
                            }
                        }else{
                            Toast.makeText(requireContext(), "Failed: "+
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
}