package com.starostinvlad.rxeducation.VideoScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.rxeducation.Adapters.MyRecyclerViewAdapter;
import com.starostinvlad.rxeducation.Adapters.PlayersAdapter;
import com.starostinvlad.rxeducation.GsonModels.Episode;
import com.starostinvlad.rxeducation.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class VideoActivity extends AppCompatActivity implements VideoActivityContract {
    private final String TAG = getClass().getSimpleName();
    int[] quality = {2097152, 1048576, 524288};
    private Window window;

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Episode episode;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private VideoPresenter videoPresenter;
    private View videoContainer;
    private long currentPosition = 0;
    private boolean isInPictureInPictureMode;
    private DefaultTrackSelector trackSelector;


    public static void start(Activity activity, Episode episode) {
        Intent intent = new Intent(activity, VideoActivity.class);
        if (episode != null)
            intent.putExtra("SERIA", episode);
        activity.startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);
        if (getIntent() != null && getIntent().hasExtra("SERIA"))
            episode = (Episode) getIntent().getSerializableExtra("SERIA");

        window = this.getWindow();
        hideSystemsElements();
        toolbar = findViewById(R.id.video_fragment_toolbar);

//        setHasOptionsMenu(true);

        progressBar = findViewById(R.id.video_progress_id);

        videoContainer = findViewById(R.id.video_container);

        Log.d(TAG, "episode: " + episode.getName());

        playerView = findViewById(R.id.player_view_id); // creating player view

        trackSelector = new DefaultTrackSelector();
        DefaultTrackSelector.Parameters defaultTrackParam = trackSelector.buildUponParameters().build();
        trackSelector.setParameters(defaultTrackParam);

        playerView.setControllerShowTimeoutMs(2000);
        playerView.setControllerAutoShow(true);
        playerView.hideController();

        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

        Button prevBtn = findViewById(R.id.episode_prev);
        Button nextBtn = findViewById(R.id.episode_next);

        ImageButton voices = findViewById(R.id.voice_btn);

        voices.setOnClickListener(view -> {
            videoPresenter.buildDialog();
        });

        nextBtn.setOnClickListener((view) -> {
            alarm("next");
        });
        prevBtn.setOnClickListener(view -> alarm("prev"));

        FrameLayout right_area = findViewById(R.id.exo_ffwd);
        FrameLayout left_area = findViewById(R.id.exo_rew);
        FrameLayout blockator = findViewById(R.id.blockator);


        blockator.setOnTouchListener(new View.OnTouchListener() {
            boolean doubletap;
            private View view;
            private GestureDetector gestureDetector = new GestureDetector(VideoActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTapEvent(MotionEvent event) {
                    Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    if (event.getRawX() > view.getWidth() / 2.0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            left_area.performContextClick(event.getRawX(), event.getRawY());
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            right_area.performContextClick(event.getRawX(), event.getRawY());
                        }
                    }
                    doubletap = true;
                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Log.d("TEST", "onSingleTapConfirmed event: " + e.getAction());
                    if (playerView.isControllerVisible()) {
                        playerView.hideController();
                        hideSystemsElements();
                    } else
                        playerView.showController();
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    Log.d("TEST", "SingTapUp event: " + e.getAction());
                    return super.onSingleTapUp(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                view = v;
                Log.d("TEST", " event: " + event.getAction() + " double: " + doubletap);
                gestureDetector.onTouchEvent(event);
                if (doubletap) {
                    doubletap = false;
                    return false;
                }
                return true;
            }
        });

        videoPresenter = new VideoPresenter(this);

        videoPresenter.loadData(episode.getUrl());

    }

    @Override
    public void initRecycle(ArrayList<Episode> episodes) {
        Log.d(TAG, "episodes: " + episodes);
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        try {
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(VideoActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, episodes);
            adapter.setClickListener((view, position) -> {
                episode = episodes.get(position);
                videoPresenter.loadData(episode.getUrl());
            });
            recyclerView.setAdapter(adapter);
//            Log.d(TAG, "episodes: " + episodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "fill complete");
    }

    @Override
    public void voiceSelectorDialog(ArrayList<Player> players) {
        PlayersAdapter adapter = new PlayersAdapter(players);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setAdapter(adapter, (dialogInterface, i) -> videoPresenter.getVideo(i));
        alertBuilder.show();
    }

    @Override
    public void qualitySelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.quality))
                .setItems(R.array.quality_array, (dialog, which) -> {
//                        videoPresenter.setQuality(which);
                    DefaultTrackSelector.Parameters
                            parameters = trackSelector.buildUponParameters()
                            .setMaxVideoBitrate(quality[which])
                            .setForceHighestSupportedBitrate(true)
                            .build();
                    trackSelector.setParameters(parameters);
                });
        builder.show();
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
            case R.id.pip:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currentPosition = player.getCurrentPosition();
                    PictureInPictureParams pictureInPictureParams = new PictureInPictureParams.Builder()
                            .setAspectRatio(new Rational(16, 9))
                            .build();
                    enterPictureInPictureMode(pictureInPictureParams);
                    playerView.hideController();
                    player.play();
                }
                Log.i("item id ", item.getItemId() + "");
                return true;
            case R.id.share_video:
                Log.i("item id ", item.getItemId() + "");
                share();
                return true;
            case R.id.quality:
                qualitySelectorDialog();
                Log.i("item id ", item.getItemId() + "");
                return true;
            case R.id.crop_video:
                if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                else
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Log.i("item id ", item.getItemId() + "");
                return true;
        }
        return false;
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Смотри сериал " + episode.getName()
                        + " " + episode.getName()
                        + " по ссылке "
                        + episode.getUrl());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        Log.d(TAG, "OnPip mode");
        this.isInPictureInPictureMode = isInPictureInPictureMode;
        if (player != null)
            player.seekTo(currentPosition);
    }

    @Override
    public void initPlayer(String uri) {
        releasePlayer();
        Log.d(TAG, "uri: " + uri);
        // Create a player instance.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
        // Create a HLS media source pointing to a playlist uri.
        HlsMediaSource hlsMediaSource =
                new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(uri));
        // Set the media source to be played.
        Log.d(TAG, "hlsMediaSource: " + hlsMediaSource.getMediaItem().mediaMetadata.title);
        player = new SimpleExoPlayer.Builder(this).build();

        player.setMediaSource(hlsMediaSource);

        playerView.setPlayer(player);
        // Prepare the player.
        player.prepare();
        player.seekTo(currentPosition);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isInPictureInPictureMode) {
            if (player != null)
                currentPosition = player.getCurrentPosition();
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
}
