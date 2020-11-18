package com.starostinvlad.fan.MainActivity;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public interface MainActivityContract {
    void setActiveFragment(int i);
    void fillPagers(ArrayList<Fragment> fragments);
}
