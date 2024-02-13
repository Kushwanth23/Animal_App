package com.example.animalapp.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.animalapp.Model.UserModel;
import com.example.animalapp.R;
import com.example.animalapp.databinding.ActivityAboutAppBinding;
import com.example.animalapp.databinding.ActivityMyAccountBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.Calendar;

public class MyAccountActivity extends AppCompatActivity {
    ActivityMyAccountBinding binding;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseUser user;
    private Uri imageUri;
    private boolean editMode = false;
    private MenuItem editMenuItem;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText phone2EditText = findViewById(R.id.acc_phone2);
        EditText emailEditText = findViewById(R.id.acc_email);
        EditText dobEditText = findViewById(R.id.acc_dob);
        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);
        RadioButton maleRadioButton = findViewById(R.id.radio_male);
        RadioButton femaleRadioButton = findViewById(R.id.radio_female);
        ImageView profile = findViewById(R.id.profile);

        setEditableFields(false);

        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel != null) {
                    nameEditText.setText(userModel.getUsername());
                    phoneEditText.setText(userModel.getPhone());
                    phone2EditText.setText(userModel.getPhone2());
                    emailEditText.setText(userModel.getEmil());
                    dobEditText.setText(userModel.getDob());

                    String gender = userModel.getGender();
                    if ("Male".equals(gender)) {
                        maleRadioButton.setChecked(true);
                    } else if ("Female".equals(gender)) {
                        femaleRadioButton.setChecked(true);
                    }
                    Picasso.get().load(userModel.getImage()).into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyAccountActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(dobEditText);
            }
        });

        profile.setOnClickListener(v -> {
            if (editMode) {
                openImagePicker();
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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);

                ShapeableImageView profile = findViewById(R.id.profile);
                profile.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                Log.e("MyAccountActivity", "Error loading image: " + e.getMessage());
            }
        }
    }

    private void showDatePickerDialog(final EditText dobEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(selectedDate);
                    }
                },
                year,
                month,
                day);

        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        editMenuItem = menu.findItem(R.id.editMenuItem);
        updateMenuIcons();
        return true;
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.editMenuItem).setVisible(!editMode);
        menu.findItem(R.id.submitMenuItem).setVisible(editMode); // Add this line
        return super.onPrepareOptionsMenu(menu);
    }
    private void setEditableFields(boolean editable) {
        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText phone2EditText = findViewById(R.id.acc_phone2);
        EditText emailEditText = findViewById(R.id.acc_email);
        EditText dobEditText = findViewById(R.id.acc_dob);
        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);

        nameEditText.setEnabled(editable);
        phoneEditText.setEnabled(editable);
        phone2EditText.setEnabled(editable);
        emailEditText.setEnabled(editable);
        dobEditText.setEnabled(editable);

        for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
            genderRadioGroup.getChildAt(i).setEnabled(editable);
        }
    }

    private void updateFirebaseData() {
        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText phone2EditText = findViewById(R.id.acc_phone2);
        EditText emailEditText = findViewById(R.id.acc_email);
        EditText dobEditText = findViewById(R.id.acc_dob);

        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);

        DatabaseReference userRef = reference.child(user.getUid());
        userRef.child("username").setValue(nameEditText.getText().toString());
        userRef.child("phone").setValue(phoneEditText.getText().toString());
        userRef.child("phone2").setValue(phone2EditText.getText().toString());
        userRef.child("emil").setValue(emailEditText.getText().toString());
        userRef.child("dob").setValue(dobEditText.getText().toString());
        userRef.child("gender").setValue(selectedRadioButton.getText().toString());

        if (imageUri != null) {
            uploadImageToFirebase();
        } else {
            Toast.makeText(MyAccountActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImageToFirebase() {
        StorageReference imageRef = storageReference.child(user.getUid() + "/profile.jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        DatabaseReference userRef = reference.child(user.getUid());

                        userRef.child("image").setValue(uri.toString());
                        userRef.child("imageUri").setValue(imageUri.toString());

                        Toast.makeText(MyAccountActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e("MyAccountActivity", "Error uploading image: " + e.getMessage());
                    Toast.makeText(MyAccountActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateMenuIcons() {
        if (editMenuItem != null) {
            editMenuItem.setVisible(!editMode);
        }
    }
}

/*
package com.example.animalapp.ui.profile;

        import androidx.appcompat.app.AppCompatActivity;

        import android.app.DatePickerDialog;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.Toast;

        import com.example.animalapp.Model.UserModel;
        import com.example.animalapp.R;
        import com.example.animalapp.databinding.ActivityAboutAppBinding;
        import com.example.animalapp.databinding.ActivityMyAccountBinding;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.squareup.picasso.Picasso;

        import java.util.Calendar;

public class MyAccountActivity extends AppCompatActivity {
    ActivityMyAccountBinding binding;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseUser user;
    private Uri imageUri;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText phone2EditText = findViewById(R.id.acc_phone2);
        EditText emailEditText = findViewById(R.id.acc_email);
        EditText dobEditText = findViewById(R.id.acc_dob);
        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);
        RadioButton maleRadioButton = findViewById(R.id.radio_male);
        RadioButton femaleRadioButton = findViewById(R.id.radio_female);
        ImageView profile = findViewById(R.id.profile);

        setEditableFields(false);

        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel != null) {
                    nameEditText.setText(userModel.getUsername());
                    phoneEditText.setText(userModel.getPhone());
                    phone2EditText.setText(userModel.getPhone2());
                    emailEditText.setText(userModel.getEmil());
                    dobEditText.setText(userModel.getDob());

                    String gender = userModel.getGender();
                    if ("Male".equals(gender)) {
                        maleRadioButton.setChecked(true);
                    } else if ("Female".equals(gender)) {
                        femaleRadioButton.setChecked(true);
                    }
                    Picasso.get().load(userModel.getImage()).into(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyAccountActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(dobEditText);
            }
        });
    }

    private void showDatePickerDialog(final EditText dobEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(selectedDate);
                    }
                },
                year,
                month,
                day);

        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.editMenuItem) {
            editMode = !editMode;
            setEditableFields(editMode);
            invalidateOptionsMenu();
            return true;
        } else if (itemId == R.id.submitMenuItem) {
            if (editMode) {
                updateFirebaseData();
                editMode = false;
                setEditableFields(false);
                invalidateOptionsMenu();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.editMenuItem).setVisible(!editMode);
        menu.findItem(R.id.submitMenuItem).setVisible(editMode);
        return super.onPrepareOptionsMenu(menu);
    }
    private void setEditableFields(boolean editable) {
        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText phone2EditText = findViewById(R.id.acc_phone2);
        EditText emailEditText = findViewById(R.id.acc_email);
        EditText dobEditText = findViewById(R.id.acc_dob);
        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);

        nameEditText.setEnabled(editable);
        phoneEditText.setEnabled(editable);
        phone2EditText.setEnabled(editable);
        emailEditText.setEnabled(editable);
        dobEditText.setEnabled(editable);

        for (int i = 0; i < genderRadioGroup.getChildCount(); i++) {
            genderRadioGroup.getChildAt(i).setEnabled(editable);
        }
    }

    private void updateFirebaseData() {
        EditText nameEditText = findViewById(R.id.acc_name);
        EditText phoneEditText = findViewById(R.id.acc_phone);
        EditText phone2EditText = findViewById(R.id.acc_phone2);
        EditText emailEditText = findViewById(R.id.acc_email);
        EditText dobEditText = findViewById(R.id.acc_dob);

        RadioGroup genderRadioGroup = findViewById(R.id.radio_gender);
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);

        DatabaseReference userRef = reference.child(user.getUid());
        userRef.child("username").setValue(nameEditText.getText().toString());
        userRef.child("phone").setValue(phoneEditText.getText().toString());
        userRef.child("phone2").setValue(phone2EditText.getText().toString());
        userRef.child("emil").setValue(emailEditText.getText().toString());
        userRef.child("dob").setValue(dobEditText.getText().toString());
        userRef.child("gender").setValue(selectedRadioButton.getText().toString());

        Toast.makeText(MyAccountActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
    }
}

*/