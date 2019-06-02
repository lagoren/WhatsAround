package com.example.galgoren.finalproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.galgoren.finalproject.fragments.FragmentFavorite;
import com.example.galgoren.finalproject.fragments.FragmentMainSearch;

import java.util.ArrayList;

/**
 * Created by galgoren on 9/16/17.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {


    // Initializing fragment array list
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();


    // fragment manager constructor, adding the fragments
    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new FragmentFavorite());
        fragments.add(new FragmentMainSearch());
    }


    //----------------------------------------------------------------------------------------------


    // get the fragment by position method
    @Override
    public Fragment getItem(int position) {


        switch (position){

            case 0:
                return fragments.get(0);

            case 1:
                return fragments.get(1);
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------


    // how many fragments method
    @Override
    public int getCount() {
        return 2;
    }


    //----------------------------------------------------------------------------------------------


    // tabs title method
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){

            case 0:
                return "MY FAVORITE";
            case 1:
                return "ARROUND ME";
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------

}
