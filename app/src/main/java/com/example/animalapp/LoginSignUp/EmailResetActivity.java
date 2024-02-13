package com.example.animalapp.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EmailResetActivity extends AppCompatActivity {

    private Button b1;
    private EditText e1;
    private String email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reset);

        b1 = findViewById(R.id.forget_password_next_btn);
        e1 = findViewById(R.id.email_forgot);

        auth = FirebaseAuth.getInstance();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData(){
        email = e1.getText().toString();
        if(email.isEmpty()){
            e1.setError("Required");
        }
        else{
            forgetPass();
        }
    }

    private void forgetPass(){
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(EmailResetActivity.this, "Check YourEmail", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EmailResetActivity.this, SuccessScreenActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(EmailResetActivity.this, "Error:" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}