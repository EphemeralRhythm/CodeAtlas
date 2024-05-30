package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(RegisterActivity.this, ChoosePicture.class);
            startActivity(intent);
            finish();
        }
    }

    FirebaseAuth mAuth;
    EditText username, email, password, confirmPassword;
    TextView goToLogin;
    ImageButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initComponents();
        initRegisterButton();
        initFirebase();
        initGoToLogin();
    }

    public void initComponents(){
        username = findViewById(R.id.usernameEditText);
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        confirmPassword = findViewById(R.id.confirmPassEditText);

        registerButton = findViewById(R.id.send);
        goToLogin = findViewById(R.id.haveAccountText);
    }

    public void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
    }

    public void initRegisterButton(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(RegisterActivity.this.email.getText());
                String username = String.valueOf(RegisterActivity.this.username.getText());
                String password = String.valueOf(RegisterActivity.this.password.getText());
                String confirmPassword = String.valueOf(RegisterActivity.this.confirmPassword.getText());


                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Required fields are empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.password.setText("");
                    RegisterActivity.this.confirmPassword.setText("");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build();

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    Map<String, Object> mp = new HashMap<>();

                                    mp.put("username", username);
                                    mp.put("email", user.getEmail());
                                    mp.put("stars", 0);
                                    mp.put("diamonds", 0);
                                    mp.put("hearts", 5);


                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        db.collection("users")
                                                                .document(user.getUid())
                                                                .set(mp)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // User successfully added
                                                                    Log.d("Firestore", "User added with ID: " + user.getUid());
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    // Failed to add user
                                                                    Log.w("Firestore", "Error adding user", e);
                                                                });

                                                        Intent intent = new Intent(RegisterActivity.this, ChoosePicture.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else {
                                                        Toast.makeText(RegisterActivity.this, "Error! Updating Username", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void initGoToLogin(){
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}