package com.example.codeatlas;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import io.noties.markwon.Markwon;

public class QuizActivity extends AppCompatActivity implements LevelInterface{
    TextView questionView;
    RadioGroup options;
    String track, topic;
    int level, pageNumber;
    ImageButton nextButton, submitButton;
    String correctAnswer;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView heartsView;
    View marker;
    boolean answeredCorrectly = false;
    int hearts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcq_quiz);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initLayoutComponents();
        initPageInfo();
        MapLevelHelper.initTopBar(db, QuizActivity.this);
        MapLevelHelper.fetchLevelData(db, track, topic, level, pageNumber, QuizActivity.this);
        initUILogic();
        initNextButton();
    }

    public void initPageInfo(){
        track = getIntent().getExtras().getString("track");
        topic = getIntent().getExtras().getString("topic");
        level = getIntent().getExtras().getInt("level");
        pageNumber = getIntent().getExtras().getInt("page");

        String uid = mAuth.getUid();
        DocumentReference ref = db.collection("users").document(uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                hearts = Math.toIntExact(task.getResult().getLong("hearts"));
                heartsView.setText(String.valueOf(hearts));
            }
        });
    }
    public void initLayoutComponents(){
        questionView = findViewById(R.id.questionView);
        options = findViewById(R.id.optionsRadioGroup);
        nextButton = findViewById(R.id.nextButton);
        submitButton = findViewById(R.id.submitButton);
        heartsView = findViewById(R.id.livesText);
    }
    public void initUILogic(){
        nextButton.setEnabled(false);
        submitButton.setEnabled(false);

        int colorDisabled = getResources().getColor(R.color.disabled);
        submitButton.setColorFilter(colorDisabled);
        nextButton.setColorFilter(colorDisabled);


        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                submitButton.setEnabled(true);
                submitButton.clearColorFilter();

                int colorCyan = getResources().getColor(R.color.cyan);
                ColorStateList stateList = ColorStateList.valueOf(colorCyan);
                ViewCompat.setBackgroundTintList(submitButton, stateList);

                for(int i = 0; i < options.getChildCount(); i++){
                    RadioButton radioButton = (RadioButton) options.getChildAt(i);

                    int defaultColor = getResources().getColor(R.color.quiz_button_default);
                    ColorStateList colorStateList = ColorStateList.valueOf(defaultColor);
                    ViewCompat.setBackgroundTintList(radioButton, colorStateList);
                }
                RadioButton selected = findViewById(checkedId);

                int defaultColor = getResources().getColor(R.color.quiz_button_selected);
                ColorStateList colorStateList = ColorStateList.valueOf(defaultColor);
                ViewCompat.setBackgroundTintList(selected, colorStateList);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setEnabled(true);
                nextButton.clearColorFilter();
                ViewCompat.setBackgroundTintList(nextButton, null);
                RadioButton checked = findViewById(options.getCheckedRadioButtonId());
                String answer = checked.getText().toString();
                if(!answer.equals(correctAnswer)){
                    int defaultColor = getResources().getColor(R.color.quiz_button_wrong);
                    ColorStateList colorStateList = ColorStateList.valueOf(defaultColor);
                    ViewCompat.setBackgroundTintList(checked, colorStateList);

                    hearts--;
                    DocumentReference ref = db.collection("users").document(mAuth.getCurrentUser().getUid());
                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Map<String, Object> mp = task.getResult().getData();
                            mp.put("hearts", hearts);
                            ref.update(mp);
                        }
                    });

                    heartsView.setText(String.valueOf(hearts));

                    if(hearts == 0){
                        DialogLosingLives dialog = new DialogLosingLives();
                        FragmentManager fm = getSupportFragmentManager();
                        dialog.show(fm, "Ran Out of Lives");
                    }
                }
                else {
                    answeredCorrectly = true;
                }

                for(int i = 0; i < 4; i++){
                    RadioButton button = (RadioButton) options.getChildAt(i);
                    if(correctAnswer.equals(button.getText().toString())){
                        int greenColor = getResources().getColor(R.color.quiz_button_correct);
                        ColorStateList colorStateList = ColorStateList.valueOf(greenColor);
                        ViewCompat.setBackgroundTintList(button, colorStateList);
                    }
                }
            }
        });
    }
    public void updateUI(Page page){
        Markwon markwon = Markwon.builder(this).build();
        markwon.setMarkdown(questionView, page.getContent().replace("\\n", "\n"));
        Collections.shuffle(page.choices);

        for(int i = 0; i < options.getChildCount(); i++){
            RadioButton radioButton = (RadioButton) options.getChildAt(i);
            radioButton.setText(page.choices.get(i));
        }

        correctAnswer = page.getCorrectAnswer();
    }


    public void initNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapLevelHelper.nextPage(db, track, topic, level,
                        pageNumber + 1, QuizActivity.this, answeredCorrectly, getSupportFragmentManager());
            }
        });
    }
}