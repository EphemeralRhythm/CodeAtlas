package com.example.codeatlas;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChoosePicture extends BaseActivity{
    ImageButton chooseImgBtn;
    Button continueBtn;
    EditText descriptionEditText;
    CircleImageView profilePicture;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);
        initLayoutComponents();
        setupBackButton();
        initButtons();
        initTextChangedEvents();
        // it must not be a nwe one, we must get it from the user in dialog
        // I have an idea for getting the id of the user using method saveProfilePhoto by parameters
        // but I don't know how since I don't know how to bring the id.
        currentUser = new User();
    }

    protected void initLayoutComponents(){
        chooseImgBtn = findViewById(R.id.addProfileBtn);
        continueBtn = findViewById(R.id.continueBtn);
        descriptionEditText = findViewById(R.id.editTextDescription);
        profilePicture = findViewById(R.id.profilePicture);
    }

    protected void initButtons(){
        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                DialogSelectSource dialog = new DialogSelectSource();
                dialog.show(fm, "Select Source");
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.getProfilePicture() == null){
                    profilePicture.setImageBitmap(getBitmapFromDrawable(R.drawable.default_profile));
                    chooseImgBtn.setVisibility(View.INVISIBLE);
                    currentUser.setProfilePicture(getBitmapFromDrawable(R.drawable.default_profile));
                    profilePicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fm = getSupportFragmentManager();
                            DialogSelectSource dialog = new DialogSelectSource();
                            dialog.show(fm, "Select Source");
                        }
                    });
                }

                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                // put profile in firebase
                if(currentUser.getProfilePicture() != null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    currentUser.getProfilePicture().compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference profileImagesRef = storageRef.child("profileImages");
                    StorageReference userImageRef = profileImagesRef.child(fbUser.getUid() + ".jpg");

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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Firestore", "Failed to get download URL: " + exception.getMessage());
                            Toast.makeText(ChoosePicture.this, "Failed to update!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                String bio = String.valueOf(descriptionEditText.getText());
                if(!TextUtils.isEmpty(bio)){
                    saveBio(bio, fbUser.getUid());
                }
                Intent intent = new Intent(ChoosePicture.this, LocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void saveBio(String bio, String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.update("bio", bio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Updated bio successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error updating bio.", e);
                    }
                });
    }

    private Bitmap getBitmapFromDrawable(@DrawableRes int drawableId) {
        Drawable drawable = getResources().getDrawable(drawableId, null);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void saveProfilePhoto(Bitmap profileImage) {
        chooseImgBtn.setVisibility(View.INVISIBLE);
        profilePicture.setImageBitmap(profileImage);
    }

    private void initTextChangedEvents() {
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Before", descriptionEditText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("On", descriptionEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("After", descriptionEditText.getText().toString());
                currentUser.setBio(descriptionEditText.getText().toString());
            }
        });
    }

}