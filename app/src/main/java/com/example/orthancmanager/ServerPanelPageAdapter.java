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

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SeachFragment();
            case 1:
                return new PatientsFragment();
            case 2:
                return new StudyFragment();
            case 3:
                return new ImagesFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return numOfTabs;
    }
}