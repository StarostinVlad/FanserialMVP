package com.starostinvlad.fan.SearchScreen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.fan.Adapters.SearchListAdapter;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.SerialPageScreen.SerialPageScreenActivity;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;

public class SearchFragment extends Fragment implements SearchFragmentContract {
    private final String TAG = getClass().getSimpleName();
    private ProgressBar searchProgressBar;
    private ListView listView;
    private SearchFragmentPresenter searchFragmentPresenter;
    private BehaviorSubject<String> searchQuery = BehaviorSubject.createDefault("");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_search, container, false);
        SearchView searchView = view.findViewById(R.id.searchview_btn);

        searchProgressBar = view.findViewById(R.id.search_progressbar);
        listView = view.findViewById(R.id.search_listview);

        searchFragmentPresenter = new SearchFragmentPresenter(this);

        searchFragmentPresenter.getHistory();

        searchQuery
                .filter(s -> s.length() > 3)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchFragmentPresenter::searchQuery
                        , Throwable::printStackTrace
                )
                .isDisposed();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchQuery.onNext(s);
                return false;
            }
        });

        return view;
    }

    @Override
    public void showLoading(boolean show) {
        searchProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void fillList(List<News> arr) {
        for (News searched : arr) {
            Log.d(TAG, "restore: " + searched.getTitle());
        }
        listView.setAdapter(new SearchListAdapter(arr));
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.d(TAG, arr.get(i).getTitle());
            searchFragmentPresenter.addInHistory(arr.get(i));
//            VideoActivity.start(getActivity(), arr.get(i));
            SerialPageScreenActivity.start(getActivity(), arr.get(i));
        });
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(Objects.requireNonNull(getView()), message, Snackbar.LENGTH_SHORT).show();
    }
}
