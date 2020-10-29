package com.starostinvlad.rxeducation.SearchScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.rxeducation.Adapters.SearchListAdapter;
import com.starostinvlad.rxeducation.GsonModels.Searched;
import com.starostinvlad.rxeducation.R;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment implements SearchFragmentContract {
    private ProgressBar searchProgressBar;
    private ListView listView;
    private SearchFragmentPresenter searchFragmentPresenter;

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


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFragmentPresenter.searchQuery(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
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
    public void fillList(List<Searched> arr) {
        listView.setAdapter(new SearchListAdapter(arr));
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(Objects.requireNonNull(getView()), message, Snackbar.LENGTH_SHORT).show();
    }
}
