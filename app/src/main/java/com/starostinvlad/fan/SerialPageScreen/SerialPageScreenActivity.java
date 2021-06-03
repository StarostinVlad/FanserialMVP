package com.starostinvlad.fan.SerialPageScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.Adapters.SerialSeasonListAdapter;
import com.starostinvlad.fan.Adapters.SimpleTextListAdapter;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

public class SerialPageScreenActivity extends AppCompatActivity implements SerialPageScreenContract {

    private News episode;
    private SerialPageScreenPresenter serialPageScreenPresenter;
    private ProgressBar progressBar;
    private TextView description;
    private Toolbar title;
    private Button openSerial;
    private ImageView imageView;
    private SimpleTextListAdapter serialPageInfoAdapter, serialPageReleaseDatesAdapter;
    private SerialSeasonListAdapter serialSeasonsAdapter;
    private boolean viewed;
    private boolean subscribed;


    public static void start(Activity context, News news) {
        Intent intent = new Intent(context, SerialPageScreenActivity.class);
        if (news != null)
            intent.putExtra(context.getString(R.string.episode_extra), news);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_page);
        serialPageInfoAdapter = new SimpleTextListAdapter(this);
        serialPageReleaseDatesAdapter = new SimpleTextListAdapter(this);

        serialSeasonsAdapter = new SerialSeasonListAdapter(this);

        title = findViewById(R.id.toolbar);
        description = findViewById(R.id.serialPageDescription);
        openSerial = findViewById(R.id.btnOpenSerial);
        openSerial.setOnClickListener(view -> {
            serialPageScreenPresenter.openSerialOnClick();
        });

        RecyclerView serialPageInfoList = findViewById(R.id.serialPageInfoList);
        serialPageInfoList.setNestedScrollingEnabled(false);
        serialPageInfoList.setHasFixedSize(true);

        RecyclerView serialPageReleaseDates = findViewById(R.id.serialPageReleaseDates);
        serialPageReleaseDates.setHasFixedSize(true);
        serialPageReleaseDates.setNestedScrollingEnabled(false);

        RecyclerView serialSeasons = findViewById(R.id.serialSeasonList);
        serialSeasons.setHasFixedSize(true);
        serialSeasons.setNestedScrollingEnabled(false);
        serialSeasons.setAdapter(serialSeasonsAdapter);

        serialPageInfoList.setAdapter(serialPageInfoAdapter);
        serialPageReleaseDates.setAdapter(serialPageReleaseDatesAdapter);

        imageView = findViewById(R.id.serialPoster);
        progressBar = findViewById(R.id.serialPageProgressBar);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.episode_extra)))
            episode = (News) getIntent().getSerializableExtra(getString(R.string.episode_extra));

        serialPageScreenPresenter = new SerialPageScreenPresenter(this);

        serialPageScreenPresenter.loadData(episode.getHref());

        //            case R.id.share_video:
//                Log.i(TAG, "id " + item.getItemId());
//                share();
//                return true;
//            case R.id.subscribe_checker:
//                item.setChecked(!item.isChecked());
//                videoPresenter.putToSubscribe(episode.getSiteId(), item.isChecked());
//                if (item.isChecked())
//                    FirebaseMessaging.getInstance().subscribeToTopic(episode.getSiteId());
//                else
//                    FirebaseMessaging.getInstance().unsubscribeFromTopic(episode.getSiteId());
//                Log.d(TAG, String.format("item id: %d = %b", item.getItemId(), item.isChecked()));
//                return false;
    }

    @Override
    public void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void fillPage(SerialPlayer serialPlayer) {
        title.setTitle(serialPlayer.getTitle());
        Picasso.with(this).load(episode.getImage()).placeholder(R.drawable.banner).into(imageView);
        description.setText(serialPlayer.getDescription());
        serialPageInfoAdapter.setItemList(serialPlayer.getInfoList());
        serialPageReleaseDatesAdapter.setItemList(serialPlayer.getReleaseDates());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video, menu);
//        MenuItem subscribe_item = menu.findItem(R.id.subscribe_checker);
//        if (App.getInstance().getLoginSubject().getValue().isEmpty()) {
//            subscribe_item.setEnabled(false);
//        } else {
//            subscribe_item.setChecked(subscribed);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void fillSeasonsList(Serial serial) {
        serialSeasonsAdapter.setItemList(serial.getSeasonList());
        serialSeasonsAdapter.setItemClickListener((view, position) -> {
            serial.setCurrentSeasonIndex(position);
            VideoActivity.start(this,serial);
        });
    }

    @Override
    public void checkViewed(boolean viewed) {
        this.viewed = viewed;
        supportInvalidateOptionsMenu();
    }

    @Override
    public void checkSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
        supportInvalidateOptionsMenu();
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        String url = episode.getHref().contains(App.getInstance().getDomain()) ? episode.getHref() : App.getInstance().getDomain() + episode.getHref();
//        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("Смотри сериал %s по ссылке %s", toolbar.getTitle().toString(), url));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    public void openActivityWithSerial(Serial serial) {
        VideoActivity.start(this, serial);
    }
}
