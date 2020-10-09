package com.starostinvlad.rxeducation.NewsScreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.adapters.NewsListAdapter;
import com.starostinvlad.rxeducation.pojos.Datum;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewsFragment extends Fragment implements NewsFragmentContract {

    NewsPresenter newsPresenter;
    String TAG = getClass().getSimpleName();
    private GridView newsGridView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsListAdapter newsGridAdapter;

    public static NewsFragment newInstance() {

        Bundle args = new Bundle();

        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        initViews(view);

        newsPresenter = new NewsPresenter(this);

        newsGridView.setOnScrollChangeListener(
                (view1, i, i1, i2, i3) -> {
//                    Log.d(TAG, "i:" + newsGridView.getLastVisiblePosition() + " size: " + (newsGridView.getCount() - 1));
                    if (newsGridView.getLastVisiblePosition() == newsGridView.getCount() - 1)
                        newsPresenter.addNews();
                }
        );

        swipeRefreshLayout.setOnRefreshListener(newsPresenter::refreshNews);

        newsPresenter.loadNews();

        return view;
    }

    private void initViews(View view) {
        newsGridView = view.findViewById(R.id.news_list_id);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            newsGridView.setNumColumns(1);
        } else {
            newsGridView.setNumColumns(2);
        }
        swipeRefreshLayout = view.findViewById(R.id.news_swipe_refresh_id);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        newsGridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void fillListView(List<Datum> datumList) {
        newsGridAdapter = new NewsListAdapter(datumList);
        newsGridView.setAdapter(newsGridAdapter);
    }

    @Override
    public void addToListView(List<Datum> datumList) {
        newsGridAdapter.addData(datumList);
    }

    @Override
    public void alarm(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.red))
                .show();
    }

    @Override
    public void showLoading(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
    }
}
