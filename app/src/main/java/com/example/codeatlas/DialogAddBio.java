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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogAddBio extends DialogFragment {

    EditText descriptionEditText;
    Button cancelBtn, confirmBtn;
    String description;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_bio, container);
        getDialog().setTitle("Add Bio");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initLayoutComponents(view);
        initButtons();
        initTextChange();
        return view;
    }

    private void initLayoutComponents(View view) {
        descriptionEditText = view.findViewById(R.id.descriptionEditTextAddBio);
        cancelBtn = view.findViewById(R.id.cancelBtnAddBio);
        confirmBtn = view.findViewById(R.id.confirmBtnAddBio);
    }

    private void initButtons(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(description == null){
                    description = "+ Add bio";
                }
                saveItem(description);
            }
        });
    }

    private void saveItem(String description){
        SaveDescription activity = (SaveDescription) getActivity();
        activity.saveBio(description);
        getDialog().dismiss();
    }

    public interface SaveDescription{
        void saveBio(String description);
    }

    private void initTextChange(){
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
                description = descriptionEditText.getText().toString();
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
