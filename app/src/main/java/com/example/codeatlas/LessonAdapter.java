package com.example.codeatlas;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class LessonAdapter extends FragmentStateAdapter {
    private ArrayList<Page> allPages, pages;
    public Context context;
    public FragmentManager fragmentManager;
    public ViewPager2 viewPager;

    public LessonAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        pages = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Page page = pages.get(position);

        if(page.getType() == Page.QUIZ)
            return new LessonMCQFragment(viewPager, context, page, fragmentManager);
        else
            return new LessonInfoFragment(viewPager, context, page);
    }

    @Override
    public int getItemCount() {
        return pages.size();
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

    public void flipPage(){
        int cur = viewPager.getCurrentItem();

        if(cur  + 1 < getItemCount()){
            viewPager.setCurrentItem(cur + 1);
            return;
        }

        if(allPages.size() > pages.size()){
            pages.add(allPages.get(pages.size()));
            notifyDataSetChanged();
            viewPager.setCurrentItem(cur + 1);
            return;
        }

        // win condition
    }
}
