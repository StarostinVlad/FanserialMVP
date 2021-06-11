package com.starostinvlad.fan.SerialPageScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.starostinvlad.fan.Adapters.ReleaseDateListAdapter;
import com.starostinvlad.fan.Adapters.SerialSeasonListAdapter;
import com.starostinvlad.fan.Adapters.SimpleTextListAdapter;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BlurTransformation;
import com.starostinvlad.fan.BlurTransformationForBackground;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer;
import com.starostinvlad.fan.VideoScreen.VideoActivity;

public class SerialPageScreenActivity extends AppCompatActivity implements SerialPageScreenContract {

    private News episode;
    private SerialPageScreenPresenter serialPageScreenPresenter;
    private ProgressBar progressBar;
    private TextView description, titleReleaseDate;
    private Toolbar toolbar;
    private Button openSerial;
    private ImageView imageView, backgroundImage;
    private SimpleTextListAdapter serialPageInfoAdapter;
    private ReleaseDateListAdapter serialPageReleaseDatesAdapter;
    private SerialSeasonListAdapter serialSeasonsAdapter;
    private boolean subscribed;
    private final String TAG = getClass().getSimpleName();


    public static void start(Activity context, News news) {
        Intent intent = new Intent(context, SerialPageScreenActivity.class);
        if (news != null)
            intent.putExtra(context.getString(R.string.episode_extra), news);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (serialPageScreenPresenter != null)
            serialPageScreenPresenter.detach();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_page);
        serialPageInfoAdapter = new SimpleTextListAdapter(this);
        serialPageReleaseDatesAdapter = new ReleaseDateListAdapter(this);

        serialSeasonsAdapter = new SerialSeasonListAdapter(this);

        toolbar = findViewById(R.id.toolbarSerialScreen);
        description = findViewById(R.id.serialPageDescription);
        titleReleaseDate = findViewById(R.id.titleReleaseDate);
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
        backgroundImage = findViewById(R.id.expandedImage);
        progressBar = findViewById(R.id.serialPageProgressBar);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.episode_extra)))
            episode = (News) getIntent().getSerializableExtra(getString(R.string.episode_extra));

        serialPageScreenPresenter = new SerialPageScreenPresenter(this);

        serialPageScreenPresenter.loadData(episode.getHref());


    }

    @Override
    public void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void fillPage(SerialPlayer serialPlayer) {

        toolbar.setTitle(serialPlayer.getTitle());
        setSupportActionBar(toolbar);

        Picasso.with(this)
                .load(episode.getImage())
                .transform(new BlurTransformation(this))
                .placeholder(R.drawable.banner)
                .into(imageView);
        Picasso.with(this)
                .load(episode.getImage())
                .transform(new BlurTransformationForBackground(this))
                .placeholder(R.drawable.banner)
                .into(backgroundImage);
        description.setText(serialPlayer.getDescription());
        serialPageInfoAdapter.setItemList(serialPlayer.getInfoList());
        if (!serialPlayer.getReleaseDates().isEmpty()) {
            titleReleaseDate.setVisibility(View.VISIBLE);
            serialPageReleaseDatesAdapter.setItemList(serialPlayer.getReleaseDates());
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_video:
                Log.i(TAG, "id " + item.getItemId());
                share();
                return true;
            case R.id.subscribe_checker:
                item.setChecked(!item.isChecked());
                serialPageScreenPresenter.putToSubscribe(episode.getSiteId(), item.isChecked());
                if (item.isChecked()) {
                    item.setIcon(R.drawable.ic_favorite_checked);
                    FirebaseMessaging.getInstance().subscribeToTopic(episode.getSiteId());
                } else {
                    item.setIcon(R.drawable.ic_favorite_unchecked);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(episode.getSiteId());
                }
                Log.d(TAG, String.format("item id: %d = %b", item.getItemId(), item.isChecked()));
                return false;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.serial_screen, menu);
        MenuItem subscribe_item = menu.findItem(R.id.subscribe_checker);
        if (App.getInstance().getLoginSubject().getValue().isEmpty()) {
            subscribe_item.setEnabled(false);
            subscribe_item.setIcon(R.drawable.ic_favorite_disable);
        } else {
            subscribe_item.setChecked(subscribed);
            subscribe_item.setIcon(
                    subscribed
                            ? R.drawable.ic_favorite_checked
                            : R.drawable.ic_favorite_unchecked
            );
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void fillSeasonsList(Serial serial) {
        serialSeasonsAdapter.setItemList(serial.getSeasonList());
        serialSeasonsAdapter.setItemClickListener((view, position) -> {
            serial.setCurrentSeasonIndex(position);
            VideoActivity.start(this, serial);
        });
    }

    @Override
    public void checkViewed(boolean viewed) {
//        this.viewed = viewed;
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
        String url = episode.getHref().contains(App.getInstance().getDomain()) ? episode.getHref() : App.getInstance().getDomain() + episode.getHref();
        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("Смотри сериал %s по ссылке %s", toolbar.getTitle().toString(), url));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    public void openActivityWithSerial(Serial serial) {
        VideoActivity.start(this, serial);
    }
}
