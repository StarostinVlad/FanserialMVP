package com.starostinvlad.rxeducation;

import android.util.Log;

import com.starostinvlad.rxeducation.LoginScreen.LoginFragment;
import com.starostinvlad.rxeducation.NewsScreen.NewsFragment;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class MainFragmentPresenter {

    private final String TAG = getClass().getSimpleName();
    MainFragmentContract view;

    public MainFragmentPresenter(MainFragmentContract view) {
        this.view = view;
    }

    public boolean activeFragment(int selected_id) {
        return activeFragment(selected_id, 0);
    }

    public boolean activeFragment(int id, int selected_id) {
        Log.d(TAG, "selected item: " + id);
        if (id == selected_id)
            return false;
        switch (id) {
            case R.id.navigation_news:
                view.setActiveFragment(0);
                return true;
            case R.id.navigation_subscribtions:
                view.setActiveFragment(1);
                return true;
            case R.id.navigation_next:
                view.setActiveFragment(2);
                return true;
        }
        return false;
    }

    void fillFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new NewsFragment());

        if (!Utils.AUTH) {
            fragments.add(new LoginFragment());
            fragments.add(new LoginFragment());
        } else {
            fragments.add(new NewsFragment());
            fragments.add(new NewsFragment());
        }

        view.fillPagers(fragments);
    }
}
