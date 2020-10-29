package com.starostinvlad.rxeducation.NextEpisodeScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.starostinvlad.rxeducation.Adapters.ViewedListAdapter;
import com.starostinvlad.rxeducation.GsonModels.Viewed;
import com.starostinvlad.rxeducation.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NextEpisodeFragment extends Fragment implements NextEpisodeFragmentContract {

    private ListView listView;
    private ProgressBar progressBar;
    private Button reload_btn;
    private NextEpisodePresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_next_episode, container, false);
        listView = view.findViewById(R.id.next_episode_listview);
        progressBar = view.findViewById(R.id.progress_viewed_list);
        reload_btn = view.findViewById(R.id.reload_viewed_list);

        reload_btn.setOnClickListener(v -> presenter.loadData());

        presenter = new NextEpisodePresenter(this);

        presenter.loadData();

        return view;
    }

    @Override
    public void fillList(List<Viewed> viewedList) {
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
