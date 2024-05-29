package com.example.codeatlas;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LevelCompletedDialog extends DialogFragment {
    TextView diamondsView;
    TextView starsView;
    int correct;
    int total;
    public LevelCompletedDialog(int correct, int total){
        this.correct = correct;
        this.total = total;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_level_completed, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        diamondsView = view.findViewById(R.id.diamonds_amount);
        starsView = view.findViewById(R.id.stars_amount);

        diamondsView.setText("+" + String.valueOf(correct * 10));
        starsView.setText("+" + String.valueOf(correct));
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        Intent intent = new Intent(getActivity(), RoadmapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }
}
