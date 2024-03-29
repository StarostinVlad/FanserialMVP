package com.starostinvlad.fan.NewsScreen;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.fan.Adapters.AppodealWrapperAdapter;
import com.starostinvlad.fan.Adapters.NewsWithAdRVAdapter;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NewsFragment extends Fragment implements NewsFragmentContract {

    private final int LANDSCAPE_COUNT = 4;
    private final int PORTRAIT_COUNT = 2;
    private NewsPresenter presenter;
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

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        presenter = new NewsPresenter();
        presenter.attachView(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int viewId = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
                Log.d(TAG, String.format("onCreateView: last: %d", viewId));
                if (viewId >= newsRecyclerViewAdapter.getItemCount() * 0.5f)
                    presenter.addNews();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(presenter::refreshNews);

        presenter.loadNews();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.news_list_id);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(PORTRAIT_COUNT, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        newsRecyclerViewAdapter = new NewsWithAdRVAdapter();
        newsRecyclerViewAdapter.setOnItemClickListener(news -> {
            Log.d(TAG, "click: " + news.getTitle());
            SerialPageScreenActivity.start((Activity) getContext(), news);
        });
//        AppodealWrapperAdapter appodealWrapperAdapter = new AppodealWrapperAdapter(newsRecyclerViewAdapter, 12);
        recyclerView.setAdapter(newsRecyclerViewAdapter);
        UiModeManager uiMode = (UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE);
        if (uiMode != null && uiMode.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            Log.d(TAG, "initViews: is TV!");
            staggeredGridLayoutManager.setSpanCount(5);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
    public void fillListView(List<News> newsList) {
        Log.d(TAG, "fillListView: newsList size: " + newsList.size());
        newsRecyclerViewAdapter.setElements(newsList);
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
