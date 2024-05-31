package com.example.codeatlas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class LessonMCQFragment extends Fragment {
    private ViewPager2 viewPager;
    Context context;
    private Page page;
    ImageButton button;
    TextView contentView;
    RadioGroup options;
    public LessonMCQFragment(ViewPager2 viewPager, Context context, Page page){
        this.context = context;
        this.page = page;
        this.viewPager = viewPager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mcq_quiz, container, false);

        initLayoutComponents(view);
        initUI();
        return view;
    }

    public void initLayoutComponents(View view){
        button = view.findViewById(R.id.submitButton);
        contentView = view.findViewById(R.id.contentView);
        options = view.findViewById(R.id.optionsRadioGroup);
    }

    public void initUI(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LessonAdapter adapter = (LessonAdapter) viewPager.getAdapter();
                adapter.flipPage();
            }
        });
    }
}
