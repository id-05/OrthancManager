package com.example.orthancmanager;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.orthancmanager.datastorage.OrthancServer;

public class ServerPanelPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private OrthancServer server;

    ServerPanelPageAdapter(OrthancServer server, FragmentManager fm, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
        this.server = server;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putInt("serverid", server.id);
                SeachFragment frag = new SeachFragment();
                frag.setArguments(bundle);
                return frag;
            case 1:
                return new PatientsFragment();
            case 2:
                return new StudyFragment();
            case 3:
                return new SeriesFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return numOfTabs;
    }
}