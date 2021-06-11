package com.starostinvlad.fan.ViewedScreen;

import android.os.Bundle;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ViewedFragment extends Fragment implements ViewedFragmentContract {

    private ListView listView;
    private ProgressBar progressBar;
    private Button reload_btn;
    private ViewedPresenter presenter;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_viewed, container, false);

        listView = view.findViewById(R.id.next_episode_listview);
        listView.setItemsCanFocus(true);
        progressBar = view.findViewById(R.id.progress_viewed_list);
        reload_btn = view.findViewById(R.id.reload_viewed_list);

        reload_btn.setOnClickListener(v -> presenter.loadData());

        presenter = new ViewedPresenter(this);

//        presenter.updateSubcribtions();

        presenter.loadData();

        return view;
    }

    @Override
    public void fillList(List<News> viewedList) {
        ViewedListAdapter viewedListAdapter = new ViewedListAdapter(viewedList);
        listView.setAdapter(viewedListAdapter);
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
