package com.starostinvlad.rxeducation;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public interface MainFragmentContract {
    void setActiveFragment(int i);
    void fillPagers(ArrayList<Fragment> fragments);
}
