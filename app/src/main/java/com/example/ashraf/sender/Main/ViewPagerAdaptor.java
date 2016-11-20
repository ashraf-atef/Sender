package com.example.ashraf.sender.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashraf Atef on 6/14/2016.
 */
public class ViewPagerAdaptor extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdaptor(android.support.v4.app.FragmentManager manager) {
        super(manager);
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}

