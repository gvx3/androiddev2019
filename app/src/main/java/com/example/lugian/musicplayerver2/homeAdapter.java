package com.example.lugian.musicplayerver2;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.lugian.musicplayerver2.chartRelated.topSongFragment;
import com.example.lugian.musicplayerver2.searchRelated.OnlineSearchFragment;

public class homeAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 2;
    private String titles[] = {"Online Songs", "Top Songs"};
    private SparseArray<Fragment> myFragments = new SparseArray<>();

    public homeAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int page) {
        // return an instance of fragment coressponding to different tab
        switch (page) {
            case 0: return new OnlineSearchFragment();
            case 1: return new topSongFragment();

        }
        return new Fragment();
    }

    public Fragment getMyFragments(int position){
        return myFragments.get(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int page){
        //return a page title
        return titles[page];
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        myFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        myFragments.remove(position);
        super.destroyItem(container, position, object);
    }


}
