package com.example.codeatlas;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class DialogChangePassword extends DialogFragment {

    EditText oldPassword, newPassword, confirmPassword;
    Button cancelBtn, confirmBtn;
    TextView forgetPassword;

    String oldPass, newPass, confirmPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_password, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initLayoutComponents(view);
        initButtons();
        initTextChange();
        return view;
    }
    private void initLayoutComponents(View view) {
        oldPassword = view.findViewById(R.id.oldPassEditText);
        newPassword = view.findViewById(R.id.newPassEditText);
        confirmPassword = view.findViewById(R.id.confirmPassEditText);
        cancelBtn = view.findViewById(R.id.cancelBtnChangePass);
        confirmBtn = view.findViewById(R.id.confirmBtnChangePass);
        forgetPassword = view.findViewById(R.id.forgotPasswordText);
    }

    private void initButtons() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPass == null || newPass == null || confirmPass == null){
                    Toast.makeText(getActivity(), "Enter all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (checkOldPassword()){
                        saveNewPassword();
                    }
                    else {
                        Toast.makeText(getActivity(), "Incorrect Old Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                ForgotPasswordDialog dialog = new ForgotPasswordDialog();
                dialog.show(fm, "Forgot Password");
            }
        });
    }

    private boolean checkOldPassword(){
        // check if old password is true
        return true;
    }

    private void saveNewPassword(){
        if (!newPass.equals(confirmPass)){
            Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else {
            // change password in firebase, change it for user

            getDialog().dismiss();
        }
    }

    private void initTextChange(){
        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Before", oldPassword.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("On", oldPassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("After", oldPassword.getText().toString());
                oldPass = oldPassword.getText().toString();
            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Before", newPassword.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("On", newPassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("After", newPassword.getText().toString());
                newPass = newPassword.getText().toString();
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Before", confirmPassword.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("On", confirmPassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("After", confirmPassword.getText().toString());
                confirmPass = confirmPassword.getText().toString();
            }
        });
    }

}
