package com.example.codeatlas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class LessonAdapter extends FragmentStateAdapter {
    private ArrayList<Page> allPages, pages;
    public LessonAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Page page = pages.get(position);

        return new LessonMCQFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public ArrayList<Page> getAllPages() {
        return allPages;
    }

    public void setAllPages(ArrayList<Page> allPages) {
        this.allPages = allPages;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }
}
