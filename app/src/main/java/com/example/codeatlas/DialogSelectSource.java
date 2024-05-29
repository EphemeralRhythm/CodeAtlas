package com.example.codeatlas;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;

public class DialogSelectSource extends DialogFragment {

    final int PERMISSION_REQUEST_CAMERA = 103;

    final int PERMISSION_REQUEST_CODE = 100;
    final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 102;

    ImageButton galleryBtn, cameraBtn, closeBtn;
    TextView galleryText, cameraText;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    User currentUser;

    //String sourceSelected = "";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.dialog_select_source, container);
        getDialog().setTitle("Select Source");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initLayoutComponents(view);

        currentUser = new User();

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            float density = DialogSelectSource.this.getResources().getDisplayMetrics().density;
                            int dp = 140;
                            int pixels = (int) ((dp * density) + 0.5);
                            Bitmap scaledPhoto = Bitmap.createScaledBitmap(
                                    photo, pixels, pixels, true);
                            saveItem(scaledPhoto);
                            currentUser.setProfilePicture(scaledPhoto);
                            Toast.makeText(getActivity(), "Photo captured!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            try {
                                saveItem(uriToBitmap(imageUri));
                                currentUser.setProfilePicture(uriToBitmap(imageUri));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            Toast.makeText(getActivity(), "Image selected!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Selection Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initButtons();
        return view;
    }

    protected void initLayoutComponents(View view){
        closeBtn = view.findViewById(R.id.closeBtnSelectSource);
        galleryBtn = view.findViewById(R.id.galleryImgBtn);
        cameraBtn = view.findViewById(R.id.takePhotoImgBtn);
        galleryText = view.findViewById(R.id.getFromGalleryText);
        cameraText = view.findViewById(R.id.takePhotoText);
    }

    protected void initButtons(){
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOpenGallery(v);
                getDialog().dismiss();
            }
        });

        galleryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOpenGallery(v);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOpenCamera(v);
            }
        });

        cameraText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initOpenCamera(v);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }
    private void initOpenCamera(View v) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), android.Manifest.permission.CAMERA)) {
                Snackbar.make(v.findViewById(R.id.dialog_select_source),
                                "The app needs permission to take photo",
                                Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("MainActivity Camera permission", "");
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                            }
                        })
                        .show();
            }
            else {
                Log.d("choosePicture Camera permission", "");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }


    private void initOpenGallery(View v) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            pickPhoto();
        }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                Snackbar.make(v.findViewById(R.id.activity_choose_picture),
                                "The app needs permission to access gallery",
                                Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("choosePicture Gallery permission", "");
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        })
                        .show();
            }
            else {
                Log.d("Permission to access the gallery was denied", "");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void pickPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }


    private Bitmap uriToBitmap(Uri uri) throws Exception {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(inputStream);
    }

    private void saveItem(Bitmap image){
        if (getActivity() instanceof ChoosePicture) {
            ((ChoosePicture) getActivity()).saveProfilePhoto(image);
            getDialog().dismiss();
        }
        if (getActivity() instanceof Profile) {
            ((Profile) getActivity()).saveProfilePhoto(image);
            getDialog().dismiss();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
    }

}