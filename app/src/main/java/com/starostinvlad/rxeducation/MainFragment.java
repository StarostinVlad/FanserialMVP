package com.starostinvlad.rxeducation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.starostinvlad.rxeducation.adapters.SlidePagerAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class MainFragment extends Fragment implements MainFragmentContract {

    private final String TAG = getClass().getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private MainFragmentPresenter mainFragmentPresenter;
    private ViewPager pager;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mainFragmentPresenter = new MainFragmentPresenter(this);

        pager = view.findViewById(R.id.fragment_container_id);

        mainFragmentPresenter.fillFragments();

        Utils.AUTH_subject.subscribe(auth -> {
            mainFragmentPresenter.fillFragments();
            mainFragmentPresenter.activeFragment(bottomNavigationView.getSelectedItemId());
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                bottomNavigationView
                        .setSelectedItemId(
                                bottomNavigationView
                                        .getMenu()
                                        .getItem(i)
                                        .getItemId()
                        );
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> mainFragmentPresenter.activeFragment(
                item.getItemId(),
                bottomNavigationView.getSelectedItemId()
        ));

        return view;
    }

    @Override
    public void fillPagers(ArrayList<Fragment> fragments) {
        SlidePagerAdapter pagerAdapter = new SlidePagerAdapter(
                getActivity().getSupportFragmentManager(),
                fragments.size(),
                fragments
        );
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public void setActiveFragment(int i) {
        pager.setCurrentItem(i, true);
    }
}
