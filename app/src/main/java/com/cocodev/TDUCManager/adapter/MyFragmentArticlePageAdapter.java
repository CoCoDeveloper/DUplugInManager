package com.cocodev.TDUCManager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cocodev.TDUCManager.articles.SubmittedArticles;

import java.util.List;

/**
 * Created by Sudarshan on 01-06-2017.
 */

public class MyFragmentArticlePageAdapter extends FragmentStatePagerAdapter {

    List<SubmittedArticles> listFragments;

    public MyFragmentArticlePageAdapter(FragmentManager fm, List<SubmittedArticles> listFragments) {
        super(fm);
        this.listFragments=listFragments;

    }


    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {

        return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SubmittedArticles articles = listFragments.get(position);

        return "";
    }

    public void swapListFragments(List<SubmittedArticles> listFragments){
        this.listFragments = listFragments;
    }


}
