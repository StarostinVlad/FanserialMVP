package com.starostinvlad.fan.NewsScreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.fan.Adapters.NewsWithAdRVAdapter;
import com.starostinvlad.fan.GsonModels.Datum;
import com.starostinvlad.fan.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewsFragment extends Fragment implements NewsFragmentContract {

    final int LANDSCAPE_COUNT = 4;
    final int PORTRAIT_COUNT = 2;
    private NewsPresenter newsPresenter;
    private String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsWithAdRVAdapter newsRecyclerViewAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initViews(view);

        if (getArguments() != null) {
            boolean isSubscriptions = getArguments().getBoolean(getString(R.string.subscriptions_extra));
            newsPresenter = new NewsPresenter(this, isSubscriptions);
        } else
            newsPresenter = new NewsPresenter(this);

        recyclerView.setOnScrollChangeListener((view1, i, i1, i2, i3) -> {
                    int viewId = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
                    Log.d(TAG, String.format("onCreateView: last: %d", viewId));
                    if (viewId >= newsRecyclerViewAdapter.getItemCount() * 0.75f)
                        newsPresenter.addNews();
                }
        );

        swipeRefreshLayout.setOnRefreshListener(newsPresenter::refreshNews);

        newsPresenter.loadNews();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.news_list_id);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(PORTRAIT_COUNT, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        newsRecyclerViewAdapter = new NewsWithAdRVAdapter();
        recyclerView.setAdapter(newsRecyclerViewAdapter);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            staggeredGridLayoutManager.setSpanCount(LANDSCAPE_COUNT);
        }
        swipeRefreshLayout = view.findViewById(R.id.news_swipe_refresh_id);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        staggeredGridLayoutManager.setSpanCount(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? LANDSCAPE_COUNT : PORTRAIT_COUNT);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void fillListView(List<Datum> datumList) {
        newsRecyclerViewAdapter.setElements(datumList);
    }

    @Override
    public void refreshListView() {
        newsRecyclerViewAdapter.notifyDataSetChanged();
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
