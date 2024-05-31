package com.example.codeatlas;

import com.example.codeatlas.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends BaseActivity implements DialogAddBio.SaveDescription{
    TextView usernameView, bioView, emailView, codeforcesView, starsView;
    CircleImageView pfpView;

    ImageButton settingsImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Navbar.initNavBar(Profile.this);
        ButtonsAlpha.resetButtonsAlpha(this);
        ButtonsAlpha.profile.setAlpha(1f);
        setupBackButton();

        initLayoutComponents();
        initUser();
        initPfpView();

        initButtons();
    }
    public void initLayoutComponents(){
        usernameView = findViewById(R.id.usernameTextView);
        bioView = findViewById(R.id.addBioTextView);
        emailView = findViewById(R.id.emailTextView);
        codeforcesView = findViewById(R.id.codeforcesTextView);
        starsView = findViewById(R.id.starsTextView);
        pfpView = findViewById(R.id.profileImage);
        settingsImgBtn = findViewById(R.id.settingsButton);
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
                Toast.makeText(Profile.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show();
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

    private void initButtons(){
        bioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogAddBio dialog = new DialogAddBio();
                dialog.show(fm, "Add Bio");
            }
        });

        settingsImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsMenu(v);
            }
        });
    }

    private void showSettingsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return handleMenuItemClick(item);
            }
        });
        popup.show();
    }

    public boolean handleMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_log_out){
            Toast.makeText(this, "Log out Succeeded", Toast.LENGTH_SHORT).show();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();

            Intent intent = new Intent(Profile.this, LoginActivity.class);
            startActivity(intent);
        }

        else if (itemId == R.id.action_change_password){
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();

            FragmentManager fm = getSupportFragmentManager();
            DialogChangePassword dialog = new DialogChangePassword();
            dialog.show(fm, "Change Username");
        }

        else if (itemId == R.id.action_change_username){
            Toast.makeText(this, "Chane username", Toast.LENGTH_SHORT).show();
            FragmentManager fm = getSupportFragmentManager();
            DialogChangeUsername dialog = new DialogChangeUsername();
            dialog.show(fm, "Change Username");
        }

        else if (itemId == R.id.about_us){
            Toast.makeText(this, "About us", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void saveProfilePhoto(Bitmap profileImage) {
        pfpView.setImageBitmap(profileImage);
        saveProfileIntoFirebase(profileImage);
    }

    @Override
    public void saveBio(String description) {
        bioView.setText(description);
        saveBioIntoFirebase(description);
    }

    private void saveBioIntoFirebase(String newBio) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("bio", newBio);


        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Profile.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void saveProfileIntoFirebase(Bitmap profileImage) {
        if(profileImage == null)
            return;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference profileImagesRef = storageRef.child("profileImages");
        StorageReference userImageRef = profileImagesRef.child(uid + ".jpg");

        UploadTask uploadTask = userImageRef.putBytes(baos.toByteArray());

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Log.d("Firestore", "updated profile picture");
                    }
                });
            }
        });
    }
}