package com.starostinvlad.fan.Adapters;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SlidePagerAdapter extends FragmentStatePagerAdapter {

    public SlidePagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Fragment> fragmentArrayList) {
        super(fm, behavior);
        this.fragmentArrayList = fragmentArrayList;
    }

    private ArrayList<Fragment> fragmentArrayList;

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}
