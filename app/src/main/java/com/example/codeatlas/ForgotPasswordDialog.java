package com.example.codeatlas;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog extends DialogFragment {
    EditText email;
    TextView hint;
    ImageView icon;
    ImageButton button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.forgot_password_dialog, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        button = view.findViewById(R.id.send);
        email = view.findViewById(R.id.emailEditText);
        hint = view.findViewById(R.id.hint);
        icon = view.findViewById(R.id.icon);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hint.setVisibility(View.INVISIBLE);
                icon.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = String.valueOf(email.getText());

                if(TextUtils.isEmpty(emailAddress)){
                    hint.setText("Enter your email.");
                    hint.setVisibility(View.VISIBLE);
                    icon.setVisibility(View.VISIBLE);
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Email Sent to " + emailAddress, Toast.LENGTH_SHORT).show();
                                    getDialog().dismiss();
                                }
                                else {
                                    hint.setText("User not found.");
                                    hint.setVisibility(View.VISIBLE);
                                    icon.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });

        return view;
    }
}
