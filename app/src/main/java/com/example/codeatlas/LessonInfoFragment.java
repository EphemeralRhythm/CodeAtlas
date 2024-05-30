package com.example.codeatlas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import io.noties.markwon.Markwon;

public class LessonInfoFragment extends Fragment {
    private ViewPager2 viewPager;
    private Page page;
    TextView contentView;
    Context context;
    ImageButton nextButton;

    public LessonInfoFragment(ViewPager2 viewPager, Context context, Page page){
        this.viewPager = viewPager;
        this.page = page;
        this.context = context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.information_page, container, false);

        initLayoutComponents(view);
        initUI();
        initNextButton();
        return view;
    }

    public void initLayoutComponents(View view){
        contentView = view.findViewById(R.id.contentView);
        nextButton = view.findViewById(R.id.nextButton);
    }

    public void initUI(){
        Markwon markwon = Markwon.builder(context).build();
        markwon.setMarkdown(contentView, page.getContent().replace("\\n", "\n"));
    }

    public void initNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LessonAdapter adapter = (LessonAdapter) viewPager.getAdapter();

                adapter.flipPage();
            }
        });
    }
}
