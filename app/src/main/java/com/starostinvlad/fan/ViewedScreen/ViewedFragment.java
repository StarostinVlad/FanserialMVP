package com.starostinvlad.fan.ViewedScreen;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.starostinvlad.fan.Adapters.ViewedListAdapter;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.GsonModels.Viewed;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ViewedFragment extends Fragment implements ViewedFragmentContract {

    private static final int PORTRAIT_COUNT = 1;
    private static final int LANDSCAPE_COUNT = 2;
    private ProgressBar progressBar;
    private Button reload_btn;
    private ViewedPresenter presenter;
    private ViewedListAdapter viewedListAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.detach();
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation)
            staggeredGridLayoutManager.setSpanCount(2);
        else
            staggeredGridLayoutManager.setSpanCount(1);
        super.onConfigurationChanged(newConfig);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_viewed, container, false);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(PORTRAIT_COUNT, StaggeredGridLayoutManager.VERTICAL);
        RecyclerView nextEpisodeRV = view.findViewById(R.id.nextEpisodeRV);
        nextEpisodeRV.setLayoutManager(staggeredGridLayoutManager);
        viewedListAdapter = new ViewedListAdapter();
        nextEpisodeRV.setAdapter(viewedListAdapter);

        progressBar = view.findViewById(R.id.progress_viewed_list);
        reload_btn = view.findViewById(R.id.reload_viewed_list);
        reload_btn.setOnClickListener(v -> presenter.loadData());

        UiModeManager uiMode = (UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE);
        if (uiMode != null && uiMode.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            staggeredGridLayoutManager.setSpanCount(3);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            staggeredGridLayoutManager.setSpanCount(LANDSCAPE_COUNT);
        }


        viewedListAdapter.setOnItemClickListener(news -> {
            SerialPageScreenActivity.start((AppCompatActivity) getContext(), news);
        });

        presenter = new ViewedPresenter(this);
        presenter.loadData();

    }

    @Override
    public void fillList(List<News> viewedList) {
        viewedListAdapter.setViewedList(viewedList);
    }

    @Override
    public void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showButton(boolean show) {
        reload_btn.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
