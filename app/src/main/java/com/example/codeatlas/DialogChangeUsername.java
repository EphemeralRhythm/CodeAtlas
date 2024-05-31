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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.L;

public class DialogChangeUsername extends DialogFragment {

    EditText usernameEditText;
    Button cancelBtn, confirmBtn;

    String username;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_username, container);
        getDialog().setTitle("Change Username");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initLayoutComponents(view);
        initButtons();
        initTextChange();
        return view;
    }

    private void initLayoutComponents(View view) {
        usernameEditText = view.findViewById(R.id.newUserNameEditText);
        cancelBtn = view.findViewById(R.id.cancelBtnChangeName);
        confirmBtn = view.findViewById(R.id.confirmBtnChangeName);
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
                if (username == null){
                    Toast.makeText(getActivity(), "Enter Username Before", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveUsername(username);
                }
            }
        });
    }

    private void saveUsername(String username) {
        // save it in firebase and for the current user


        // after that
        // NOTE: check if the name is changed in the layout after saving
        getDialog().dismiss();
    }

    private void initTextChange() {
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Before", usernameEditText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("On", usernameEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("After", usernameEditText.getText().toString());
                username = usernameEditText.getText().toString();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
    }
}
