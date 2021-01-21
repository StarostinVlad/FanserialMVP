package com.starostinvlad.fan.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.Episode;
import com.starostinvlad.fan.GsonModels.Images;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;


public class SerialRecyclerViewAdapter extends RecyclerView.Adapter<SerialRecyclerViewAdapter.ViewHolder> {

    private final List<String> list;
    private final String TAG = getClass().getSimpleName();
    private List<List<Episode>> seasonEpisodes;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public SerialRecyclerViewAdapter(Context context, List<String> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.seasonEpisodes = new ArrayList<>();
        Log.d(TAG, "initialize");
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.season_list_item, parent, false);
        Log.d(TAG, "createViewHolder");
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");

        holder.titleView.setText("Сезон " + (position + 1));

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.recyclerView.setLayoutManager(layoutManager);
        if (position < seasonEpisodes.size())
            holder.recyclerView.setAdapter(getAdapter(holder.recyclerView.getContext(), seasonEpisodes.get(position)));
        else
            Observable.fromCallable(() -> getseasonEpisodeList(list.get(position)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(val -> {
                                Log.d(TAG, "val length: " + val.size());
                                seasonEpisodes.add(val);
                                holder.recyclerView.setAdapter(getAdapter(holder.recyclerView.getContext(), val));
                            },
                            Throwable::printStackTrace);

    }

    private EpisodeRecyclerViewAdapter getAdapter(Context context, List<Episode> episodes) {
        EpisodeRecyclerViewAdapter myRecyclerViewAdapter = new EpisodeRecyclerViewAdapter(context, episodes);
//        myRecyclerViewAdapter.setClickListener(
//                (view1, position) -> VideoActivity.start((Activity) context, episodes.get(position))
//        );
        return myRecyclerViewAdapter;
    }

    private ArrayList<Episode> getseasonEpisodeList(String url) {
        Log.d(TAG, "url: " + url);
        Request getSeriaPage = new Request.Builder().url(url).get().build();
        try {
            Response response = App.getInstance().getOkHttpClient().newCall(getSeriaPage).execute();
            if (response.code() == 200 & response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                Elements elements = doc.select("#episode_list > li > div > div");
                //#episode_list > div:nth-child(1) > ul > li:nth-child(1) > div > div > div.serial-top > div.field-img
                ArrayList<Episode> episodes = new ArrayList<>();
                for (Element element : elements) {
                    Episode episode = new Episode();
                    Element sup = element.select(".serial-bottom > div.field-description > a").first();
                    episode.setName(sup.text());
                    episode.setUrl(sup.attr("href"));
                    sup = element.select(".serial-top > div.field-img").first();
                    String src = sup.attr("style");
                    Images images = new Images();
                    images.setSmall(src.substring(23, src.length() - 3));
                    episode.setImages(images);
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

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }

    // convenience method for getting data at click position
    public List<Episode> getItem(int id) {
        return seasonEpisodes.get(id);
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        RecyclerView recyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.season_body);
            titleView = itemView.findViewById(R.id.season_title);
        }
    }
}