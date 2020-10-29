package com.starostinvlad.rxeducation.MainActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.Utils;
import com.starostinvlad.rxeducation.Adapters.SlidePagerAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MainActivityContract {

    private final String TAG = getClass().getSimpleName();
    private MainActivityPresenter mainActivityPresenter;
    private ViewPager pager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityPresenter = new MainActivityPresenter(this);

        pager = findViewById(R.id.fragment_container_id);

        mainActivityPresenter.fillFragments();

        Utils.AUTH_subject.subscribe(auth -> {
            mainActivityPresenter.fillFragments();
            mainActivityPresenter.activeFragment(bottomNavigationView.getSelectedItemId());
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


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> mainActivityPresenter.activeFragment(
                item.getItemId(),
                bottomNavigationView.getSelectedItemId()
        ));
    }

    @Override
    public void fillPagers(ArrayList<Fragment> fragments) {
        SlidePagerAdapter pagerAdapter = new SlidePagerAdapter(
                getSupportFragmentManager(),
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
