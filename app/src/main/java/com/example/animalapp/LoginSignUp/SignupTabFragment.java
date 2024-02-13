package com.example.animalapp.LoginSignUp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.animalapp.Model.UserModel;
import com.example.animalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupTabFragment extends Fragment {

    private EditText name, email, password, mobile;
    private Button register;
    private FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        name = root.findViewById(R.id.Reg_name);
        email = root.findViewById(R.id.Reg_email);
        password = root.findViewById(R.id.Reg_password);
        mobile = root.findViewById(R.id.Reg_mobile);
        register = root.findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                String phone = mobile.getText().toString();
                String Username = name.getText().toString();

                if (emailStr.isEmpty() || passwordStr.isEmpty() || phone.isEmpty() || Username.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        UserModel model = new UserModel();
                                        model.setEmil(emailStr);
                                        model.setPassword(passwordStr);
                                        model.setPhone(phone);
                                        model.setUid(user.getUid());
                                        model.setUsername(Username);

                                        reference.child(user.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        name.setTranslationX(800);
        email.setTranslationX(800);
        password.setTranslationX(800);
        mobile.setTranslationX(800);
        register.setTranslationX(800);

        name.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(600).start();
        mobile.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        register.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(800).start();

        return root;
    }
}