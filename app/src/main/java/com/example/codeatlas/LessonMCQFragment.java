package com.example.codeatlas;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Map;

import io.noties.markwon.Markwon;

public class LessonMCQFragment extends Fragment {
    private ViewPager2 viewPager;
    Context context;
    private Page page;
    ImageButton button;
    TextView contentView, buttonText;
    RadioGroup options;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    View marker;
    int correctAnswers, totalAnswers;
    FragmentManager fm;
    int hearts;
    boolean answered = false;

    public LessonMCQFragment(ViewPager2 viewPager, Context context, Page page, FragmentManager fragmentManager){
        this.context = context;
        this.page = page;
        this.viewPager = viewPager;
        this.fm = fragmentManager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mcq_quiz, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initLayoutComponents(view);
        initUI();
        initLogic(view);
        return view;
    }

    public void initLayoutComponents(View view){
        button = view.findViewById(R.id.submitButton);
        contentView = view.findViewById(R.id.questionView);
        options = view.findViewById(R.id.optionsRadioGroup);
        marker = view.findViewById(R.id.marker);
        buttonText = view.findViewById(R.id.submitText);
    }

    public void initUI(){
        Markwon markwon = Markwon.builder(context).build();
        markwon.setMarkdown(contentView, page.getContent().replace("\\n", "\n"));

        Collections.shuffle(page.choices);

        for(int i = 0; i < options.getChildCount(); i++){
            RadioButton radioButton = (RadioButton) options.getChildAt(i);
            radioButton.setText(page.choices.get(i));
        }
    }

    public void initLogic(View view){
        button.setEnabled(false);
        int colorDisabled = getResources().getColor(R.color.disabled);
        button.setColorFilter(colorDisabled);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LessonAdapter adapter = (LessonAdapter) viewPager.getAdapter();
                adapter.flipPage();
            }
        });


        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                button.setEnabled(true);
                button.clearColorFilter();

                int colorCyan = getResources().getColor(R.color.cyan);
                ColorStateList stateList = ColorStateList.valueOf(colorCyan);
                ViewCompat.setBackgroundTintList(button, stateList);

                for(int i = 0; i < options.getChildCount(); i++){
                    RadioButton radioButton = (RadioButton) options.getChildAt(i);

                    int defaultColor = getResources().getColor(R.color.quiz_button_default);
                    ColorStateList colorStateList = ColorStateList.valueOf(defaultColor);
                    ViewCompat.setBackgroundTintList(radioButton, colorStateList);
                }

                RadioButton selected = view.findViewById(checkedId);

                int defaultColor = getResources().getColor(R.color.quiz_button_selected);
                ColorStateList colorStateList = ColorStateList.valueOf(defaultColor);
                ViewCompat.setBackgroundTintList(selected, colorStateList);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton checked = view.findViewById(options.getCheckedRadioButtonId());
                String answer = checked.getText().toString();
                marker.setVisibility(View.VISIBLE);

                LessonActivity activity = (LessonActivity) viewPager.getTag();

                if(!answered)
                    activity.totalAnswers++;

                buttonText.setText("Continue");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LessonAdapter adapter = (LessonAdapter) viewPager.getAdapter();
                        adapter.flipPage();
                    }
                });


                if(!answer.equals(page.getCorrectAnswer())){
                    int defaultColor = getResources().getColor(R.color.quiz_button_wrong);
                    ColorStateList colorStateList = ColorStateList.valueOf(defaultColor);
                    marker.setBackgroundTintList(colorStateList);
                    ViewCompat.setBackgroundTintList(checked, colorStateList);

                    if(!answered){
                        DocumentReference ref = db.collection("users").document(mAuth.getCurrentUser().getUid());
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Map<String, Object> mp = task.getResult().getData();
                                int hearts = Math.toIntExact((Long) mp.get("hearts"));
                                hearts--;


                                TextView heartsView = activity.heartTextView;
                                heartsView.setText(String.valueOf(hearts));

                                if(hearts == 0){
                                    DialogLosingLives dialog = new DialogLosingLives();
                                    dialog.show(fm, "Ran Out of Lives");
                                }

                                mp.put("hearts", hearts);
                                ref.update(mp);
                            }
                        });

                    }
                    answered = true;
                }
                else {
                    // answeredCorrectly = true;
                    answered = true;
                    activity.correctAnswers++;

                    int greenColor = getResources().getColor(R.color.quiz_button_correct);
                    ColorStateList colorStateList = ColorStateList.valueOf(greenColor);
                    marker.setBackgroundTintList(colorStateList);
                }

                for(int i = 0; i < 4; i++){
                    RadioButton button = (RadioButton) options.getChildAt(i);
                    if(page.getCorrectAnswer().equals(button.getText().toString())){
                        int greenColor = getResources().getColor(R.color.quiz_button_correct);
                        ColorStateList colorStateList = ColorStateList.valueOf(greenColor);
                        ViewCompat.setBackgroundTintList(button, colorStateList);
                    }
                }
            }
        });
    }
}
