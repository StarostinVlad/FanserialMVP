package com.starostinvlad.fan.VideoScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.fan.Adapters.SubtitlesAdapter;
import com.starostinvlad.fan.Adapters.TranslationsAdapter;
import com.starostinvlad.fan.Adapters.SeasonRecyclerViewAdapter;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.GsonModels.News;
import com.starostinvlad.fan.R;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Season;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class VideoActivity extends AppCompatActivity implements VideoActivityContract {
    private final String TAG = getClass().getSimpleName();
    int[] quality = {414000, 714000, 1064000, 5055521};

    PlayerView playerView;
    Toolbar toolbar;
    ProgressBar progressBar;
    View videoContainer;
    private Window window;
    private SimpleExoPlayer player;
    private VideoPresenter videoPresenter;
    private long currentPosition = 0;
    private boolean isInPictureInPictureMode;
    private DefaultTrackSelector trackSelector;
    private UiModeManager uiModeManager;
    private boolean viewed;
    private boolean subscribed;
    private String id = "";
    private Button exo_rew;
    private Button exo_ffwd;
    private ImageButton subtitlesBtn;
    private boolean doubleTap;
    private View doubleClickArea;


    public static void start(Activity activity, Serial serial) {
        Intent intent = new Intent(activity, VideoActivity.class);
        if (serial != null)
            intent.putExtra("SERIAL", serial);
        activity.startActivity(intent);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);
        playerView = findViewById(R.id.player_view_id);
        toolbar = findViewById(R.id.video_fragment_toolbar);
        progressBar = findViewById(R.id.video_progress_id);
        videoContainer = findViewById(R.id.video_container);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

//        if (getIntent() != null && getIntent().hasExtra(getString(R.string.episode_extra)))
//            episode = (News) getIntent().getSerializableExtra(getString(R.string.episode_extra));
//        else if (getIntent() != null && getIntent().hasExtra("NAME")) {
//            episode = new News();
//            episode.setTitle(getIntent().getStringExtra("NAME"));
//            episode.setHref(getIntent().getStringExtra("HREF"));
//        } else {
//            showDialog(getString(R.string.video_error));
//        }

        window = this.getWindow();
        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        hideSystemsElements();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


//        Log.d(TAG, "episode: " + episode.getSubTitle());

        trackSelector = new DefaultTrackSelector();
        DefaultTrackSelector.Parameters defaultTrackParam = trackSelector.buildUponParameters().build();
        trackSelector.setParameters(defaultTrackParam);

        playerView.setControllerShowTimeoutMs(2000);
        playerView.setControllerAutoShow(true);
        playerView.hideController();

        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
        player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        playerView.setPlayer(player);

        exo_ffwd = findViewById(R.id.exo_ffwd);
        exo_rew = findViewById(R.id.exo_rew);

        ImageButton voices = findViewById(R.id.voice_btn);
        subtitlesBtn = findViewById(R.id.btn_subtitle);
        subtitlesBtn.setOnClickListener(view -> subtitleSelectorDialog());

        voices.setOnClickListener(view -> videoPresenter.onBuildDialog());

        doubleClickArea = findViewById(R.id.doubleClickArea);

        videoPresenter = new VideoPresenter();
        videoPresenter.attachView(this);

        if (getIntent() != null && getIntent().hasExtra("SERIAL")) {
            videoPresenter.onStartWithSerial((Serial) getIntent().getSerializableExtra("SERIAL"));
        } else if (App.getInstance().isReview()) {
            playerView.hideController();
            showDialog(getString(R.string.on_review));
        } else {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
//            videoPresenter.loadData(episode.getHref());
        }
    }

    @Override
    public void showDialog(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                .setOnDismissListener(dialogInterface -> finish()).show();
    }

    @Override
    public void openTrailer(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void changeDescription(String title, String subTitle) {
        toolbar.setTitle(title);
        toolbar.setSubtitle(subTitle);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (!playerView.isControllerVisible())
                playerView.showController();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void initRecycle(List<Episode> episodes) {
        RecyclerView recyclerView = findViewById(R.id.episodeListRV);
        try {
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(VideoActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            SeasonRecyclerViewAdapter adapter = new SeasonRecyclerViewAdapter(this, episodes);
            adapter.setClickListener((view, position) -> {
                videoPresenter.onChangeEpisode(position);
                Appodeal.show(this, Appodeal.INTERSTITIAL);
            });
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "episodes: " + episodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "fill complete");
    }

    @Override
    public void translationSelectorDialog(List<Translation> translations) {
        TranslationsAdapter adapter = new TranslationsAdapter(translations);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setAdapter(adapter, (dialogInterface, i) -> videoPresenter.onChangeTranslation(i));
        alertBuilder.show();
    }

    @Override
    public void qualitySelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.quality))
                .setItems(R.array.quality_array, (dialog, which) -> {
                    DefaultTrackSelector.Parameters
                            parameters = trackSelector.buildUponParameters()
                            .setMaxVideoBitrate(quality[which])
                            .setForceHighestSupportedBitrate(true)
                            .build();
                    trackSelector.setParameters(parameters);
                });
        builder.show();
    }

    void subtitleSelectorDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SubtitlesAdapter adapter = new SubtitlesAdapter(player.getCurrentMediaItem().playbackProperties.subtitles);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                    .setAdapter(adapter, (dialogInterface, i) -> {
                        Log.d(TAG, "showSubtitleDialog: lang: " + adapter.getItem(i));
                        DefaultTrackSelector.Parameters
                                parameters = trackSelector.buildUponParameters()
                                .setPreferredTextLanguage(adapter.getItem(i))
                                .build();
                        trackSelector.setParameters(parameters);
                    });
            alertBuilder.show();
        }
    }


    private void hideSystemsElements() {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.pip:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && uiModeManager.getCurrentModeType() != Configuration.UI_MODE_TYPE_TELEVISION) {
                    currentPosition = player.getCurrentPosition();
                    PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                            .setAspectRatio(new Rational(16, 9))
                            .build();
                    enterPictureInPictureMode(pictureInPictureParams);
                    playerView.hideController();
                    player.play();
                }
                Log.i(TAG, "id " + item.getItemId());
                return true;

            case R.id.quality:
                qualitySelectorDialog();
                Log.i(TAG, "id " + item.getItemId());
                return true;
            case R.id.crop_video:
                if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                else
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Log.i(TAG, "id " + item.getItemId());
                return true;
        }
        return false;
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        Log.d(TAG, "OnPip mode");
        this.isInPictureInPictureMode = isInPictureInPictureMode;
//        if (player != null)
//            player.seekTo(currentPosition);
    }

    @Override
    public void initPlayer(Hls hls) {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
            playerView.setPlayer(player);
        }
        Log.d(TAG, "uri: " + hls.src);
        List<MediaItem.Subtitle> subtitles = new ArrayList<>();
        if (!hls.enSub.isEmpty() && !hls.enSub.equals("false")) {
            Uri sub = Uri.parse(hls.enSub);
            MediaItem.Subtitle subtitle = new MediaItem.Subtitle(sub, MimeTypes.TEXT_VTT, "en", Format.NO_VALUE);
            subtitles.add(subtitle);
        }
        if (!hls.ruSub.isEmpty() && !hls.ruSub.equals("false")) {
            Uri sub = Uri.parse(hls.ruSub);
            MediaItem.Subtitle subtitle = new MediaItem.Subtitle(sub, MimeTypes.TEXT_VTT, "ru", Format.NO_VALUE);
            subtitles.add(subtitle);
        }
        if (subtitles.isEmpty()) {
            subtitlesBtn.setVisibility(View.GONE);
        } else {
            subtitlesBtn.setVisibility(View.VISIBLE);
        }
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(hls.src)
                .setSubtitles(subtitles)
                .build();
//        MediaItem mediaItem = MediaItem.fromUri(uri);
        Log.d(TAG, "media item: " + mediaItem.playbackProperties.uri);
//        player = new SimpleExoPlayer.Builder(this).build();
        if (player != null) {
            player.setMediaItem(mediaItem);
            player.setForegroundMode(true);
//        playerView.setPlayer(player);
            player.prepare();
            player.seekTo(currentPosition);
        }
    }

    @Override
    public void alarm(String message) {
        Snackbar.make(videoContainer, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .show();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        videoPresenter.onStart();
        MediaSessionCompat mediaSession = new MediaSessionCompat(this,getPackageName());
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);
        mediaSession.setActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isInPictureInPictureMode) {
            if (player != null) {
                currentPosition = player.getCurrentPosition();
            }
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPresenter.onDetach();
    }

    @Override
    public void showLoading(boolean b) {
        videoContainer.setVisibility(!b ? View.VISIBLE : View.GONE);
        toolbar.setVisibility(!b ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void fillToolbar(String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: dispatch touch");
        if (uiModeManager.getCurrentModeType() != Configuration.UI_MODE_TYPE_TELEVISION)
            this.mDetector.onTouchEvent(event);
        Log.d(TAG, "dispatchTouchEvent: doubleTap 1:" + doubleTap);
        if (!doubleTap) {
            hideSystemsElements();
            return playerView.dispatchTouchEvent(event);
        }
        doubleTap = false;
//        else
//            return mDetector.onTouchEvent(event);
//            return true;

        return super.dispatchTouchEvent(event);
    }

    GestureDetectorCompat mDetector;

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            doubleTap = true;
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTapEvent: tap " + event.getRawX() + " : " + playerView.getWidth() / 2.0);
            doubleTap = false;
            doubleClickArea.performClick();
            if (event.getRawX() > playerView.getWidth() / 2.0) {
                exo_ffwd.performClick();
            } else {
                exo_rew.performClick();
            }
            return super.onDoubleTap(event);
        }
    }
}
