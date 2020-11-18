package com.starostinvlad.fan.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.starostinvlad.fan.Adapters.SlidePagerAdapter;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.LoginScreen.LoginFragment;
import com.starostinvlad.fan.NewsScreen.NewsFragment;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SearchScreen.SearchFragment;
import com.starostinvlad.fan.SettingsScreen.SettingsFragment;
import com.starostinvlad.fan.ViewedScreen.ViewedFragment;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MainActivityContract {

    private final String TAG = getClass().getSimpleName();
    private ViewPager pager;
    private BottomNavigationView bottomNavigationView;

    public static void start(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("RxEducation");

        pager = findViewById(R.id.fragment_container_id);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        App.TOKEN_subject.subscribe(token -> {
                    Log.d(TAG, "onCreate: token: " + token);
                    fillFragments(token);
                    activeFragment(bottomNavigationView.getSelectedItemId(), 0);
                },
                Throwable::printStackTrace
        ).isDisposed();

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


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> activeFragment(
                item.getItemId(),
                bottomNavigationView.getSelectedItemId()
        ));
    }

    boolean activeFragment(int id, int selected_id) {
        Log.d(TAG, "selected item: " + id);
        if (id == selected_id)
            return false;
        switch (id) {
            case R.id.navigation_news:
                setActiveFragment(0);
                return true;
            case R.id.navigation_search:
                setActiveFragment(1);
                return true;
            case R.id.navigation_subscribtions:
                setActiveFragment(2);
                return true;
            case R.id.navigation_next:
                setActiveFragment(3);
                return true;
            case R.id.navigation_settings:
                setActiveFragment(4);
                return true;
        }
        return false;
    }

    void fillFragments(String token) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new NewsFragment());

        if (StringUtils.isEmpty(token)) {
            fragments.add(new SearchFragment());
            fragments.add(new LoginFragment());
            fragments.add(new LoginFragment());
        } else {
            NewsFragment newsFragment = new NewsFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(getString(R.string.subscriptions_extra), true);
            newsFragment.setArguments(bundle);

            fragments.add(new SearchFragment());
            fragments.add(newsFragment);
            fragments.add(new ViewedFragment());
        }
        fragments.add(new SettingsFragment());
        fillPagers(fragments);
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
