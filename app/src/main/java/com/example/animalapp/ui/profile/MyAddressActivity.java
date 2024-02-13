package com.example.animalapp.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animalapp.Model.UserModel;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityMyAddressBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAddressActivity extends AppCompatActivity {

    ActivityMyAddressBinding binding;
    DatabaseReference reference;
    FirebaseUser user;
    private boolean editMode = false;
    private MenuItem editMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText addressEditText = findViewById(R.id.acc_add1);
        EditText addressEditText2 = findViewById(R.id.acc_add2);
        EditText stateEditText = findViewById(R.id.acc_state);
        EditText pinEditText = findViewById(R.id.acc_pin);

        setEditableFields(false);

        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel != null){
                    nameEditText.setText(userModel.getUsername());
                    phoneEditText.setText(userModel.getPhone());
                    addressEditText.setText(userModel.getAddress1());
                    addressEditText2.setText(userModel.getAddress2());
                    stateEditText.setText(userModel.getState());
                    pinEditText.setText(userModel.getPinNumber());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAddressActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        Button submitButton = findViewById(R.id.register);
        submitButton.setOnClickListener(v -> {
            if (editMode) {
                updateFirebaseData();
                editMode = false;
                setEditableFields(false);
                updateMenuIcons();
            }
        });

        updateMenuIcons();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        editMenuItem = menu.findItem(R.id.editMenuItem);
        updateMenuIcons();
        return true;
    }

    private void updateMenuIcons() {
        if (editMenuItem != null) {
            editMenuItem.setVisible(!editMode);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.editMenuItem).setVisible(!editMode);
        menu.findItem(R.id.submitMenuItem).setVisible(editMode); // Add this line
        return super.onPrepareOptionsMenu(menu);
    }

    private void setEditableFields(boolean editable) {
        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText addressEditText = findViewById(R.id.acc_add1);
        EditText addressEditText2 = findViewById(R.id.acc_add2);
        EditText stateEditText = findViewById(R.id.acc_state);
        EditText pinEditText = findViewById(R.id.acc_pin);

        nameEditText.setEnabled(editable);
        phoneEditText.setEnabled(editable);
        addressEditText.setEnabled(editable);
        addressEditText2.setEnabled(editable);
        stateEditText.setEnabled(editable);
        pinEditText.setEnabled(editable);
    }
    private void updateFirebaseData() {
        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText addressEditText = findViewById(R.id.acc_add1);
        EditText addressEditText2 = findViewById(R.id.acc_add2);
        EditText stateEditText = findViewById(R.id.acc_state);
        EditText pinEditText = findViewById(R.id.acc_pin);

        DatabaseReference userRef = reference.child(user.getUid());
        userRef.child("username").setValue(nameEditText.getText().toString());
        userRef.child("phone").setValue(phoneEditText.getText().toString());
        userRef.child("address").setValue(addressEditText.getText().toString());
        userRef.child("address2").setValue(addressEditText2.getText().toString());
        userRef.child("state").setValue(stateEditText.getText().toString());
        userRef.child("pincode").setValue(pinEditText.getText().toString());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.editMenuItem) {
            editMode = true;
            setEditableFields(editMode);
            updateMenuIcons();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}