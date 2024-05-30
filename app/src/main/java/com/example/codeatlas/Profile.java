package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends BaseActivity {
    TextView usernameView, bioView, emailView, codeforcesView, starsView;
    CircleImageView pfpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Navbar.initNavBar(Profile.this);
        setupBackButton();

        initLayoutComponents();
        initUser();
        initPfpView();

        ImageButton settings = findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(Profile.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    public void initLayoutComponents(){
        usernameView = findViewById(R.id.usernameTextView);
        bioView = findViewById(R.id.addBioTextView);
        emailView = findViewById(R.id.emailTextView);
        codeforcesView = findViewById(R.id.codeforcesTextView);
        starsView = findViewById(R.id.starsTextView);
        pfpView = findViewById(R.id.profileImage);
    }
    public void initUser(){
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = fbUser.getUid();
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> mp =  document.getData();

                            String username = (String) mp.get("username");
                            String bio = (String) mp.getOrDefault("bio", "+Add bio");
                            String email = (String) mp.getOrDefault("email", "Email");
                            String cfHandle = (String) mp.getOrDefault("codeforces_handle", "account");
                            Long numStars = (Long) mp.get("stars");
                            loadProfilePicture(uid);

                            usernameView.setText(username);
                            bioView.setText(bio);
                            emailView.setText(email);
                            codeforcesView.setText(cfHandle);
                            starsView.setText(String.valueOf(numStars));
                        }
                    }
                });
    }

    public void loadProfilePicture(String uid){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference().child("profileImages/" + uid +".jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(pfpView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText(Profile.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initPfpView(){
        pfpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogSelectSource dialog = new DialogSelectSource();
                dialog.show(fm, "Select Source");
            }
        });
    }

    public void saveProfilePhoto(Bitmap profileImage) {
        pfpView.setImageBitmap(profileImage);
    }
}