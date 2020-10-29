package com.starostinvlad.rxeducation.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.starostinvlad.rxeducation.GsonModels.Episode;
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.VideoScreen.VideoActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.starostinvlad.rxeducation.Utils.CLIENT;

public class SeasonAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();
    private final List<String> list;
    private List<List<Episode>> seasonEpisodes;

    public SeasonAdapter(List<String> seasons) {
        this.list = seasons;
        seasonEpisodes = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.season_list_item, viewGroup, false);
        RecyclerView recyclerView = view.findViewById(R.id.season_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(viewGroup.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        if (i < seasonEpisodes.size())
            recyclerView.setAdapter(getAdapter(viewGroup.getContext(), seasonEpisodes.get(i)));
        else
            Observable.fromCallable(() -> getseasonEpisodeList(list.get(i)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(val -> {
                                Log.d(TAG, "val length: " + val.size());
                                seasonEpisodes.add(val);
                                recyclerView.setAdapter(getAdapter(viewGroup.getContext(), val));
                            },
                            Throwable::printStackTrace);

        return view;
    }

    MyRecyclerViewAdapter getAdapter(Context context, List<Episode> episodes) {
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(context, episodes);
        myRecyclerViewAdapter.setClickListener(
                (view1, position) -> VideoActivity.start((Activity) context, episodes.get(position))
        );
        return myRecyclerViewAdapter;
    }

    private ArrayList<Episode> getseasonEpisodeList(String url) {
        Log.d(TAG, "url: " + url);
        Request getSeriaPage = new Request.Builder().url(url).get().build();
        try {
            Response response = CLIENT.newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                Elements elements = doc.select("#episode_list > li > div > div > div.serial-bottom > div.field-description > a");
                ArrayList<Episode> episodes = new ArrayList<>();
                for (Element element : elements) {
                    Episode episode = new Episode();
                    episode.setName(element.text());
                    episode.setUrl(element.attr("href"));
                    episodes.add(episode);
                    Log.d(TAG, "episode: " + element.text());
                }
                Collections.reverse(episodes);
                return episodes;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.getMessage());
        }
        return null;
    }
}
