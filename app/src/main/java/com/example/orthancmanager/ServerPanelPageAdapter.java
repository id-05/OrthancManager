package com.example.orthancmanager;

//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ServerPanelPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    ServerPanelPageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return null;
    }


    @Override
    public int getCount() {
        return numOfTabs;
    }
}