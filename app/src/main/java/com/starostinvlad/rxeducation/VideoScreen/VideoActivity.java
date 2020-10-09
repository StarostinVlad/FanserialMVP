package com.starostinvlad.rxeducation.VideoScreen;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.adapters.MyRecyclerViewAdapter;
import com.starostinvlad.rxeducation.adapters.PlayersAdapter;
import com.starostinvlad.rxeducation.pojos.Datum;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class VideoActivity extends AppCompatActivity implements VideoActivityContract {
    private final String TAG = getClass().getSimpleName();
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Datum episode;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private Spinner voicesList;
    private VideoPresenter videoPresenter;
    private View videoContainer;
    private long currentPosition = 0;
    private Button fullscreenBtn;
    private int primaryLayoutHeight;
    private Window window;
    private boolean fullscreen;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_fragment);
        if (getIntent() != null && getIntent().hasExtra("SERIA"))
            episode = (Datum) getIntent().getSerializableExtra("SERIA");

        window = this.getWindow();
        hideSystemsElements();
        toolbar = findViewById(R.id.video_fragment_toolbar);

//        setHasOptionsMenu(true);

        progressBar = findViewById(R.id.video_progress_id);
        voicesList = findViewById(R.id.voice_spiner_id);

        videoContainer = findViewById(R.id.video_container);

        Log.d(TAG, "episode: " + episode.getEpisode().getName());

        playerView = findViewById(R.id.player_view_id); // creating player view

        primaryLayoutHeight = playerView.getLayoutParams().height;

        playerView.setControllerShowTimeoutMs(2000);
        playerView.setControllerAutoShow(true);

        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

//        fullscreenBtn = findViewById(R.id.exo_fullscreen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        fullscreenBtn.setOnClickListener(view1 -> fullscreenChange());
//        fullscreenBtn.setOnClickListener(view1 -> {
//            if (fullscreen) {
//                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
//                exitFullscreen();
//            } else {
//                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//                enterFullscreen();
//            }
//        });

        Button prevBtn = findViewById(R.id.episode_prev);
        Button nextBtn = findViewById(R.id.episode_next);

        nextBtn.setOnClickListener((view) -> {
            alarm("next");
        });
        prevBtn.setOnClickListener(view -> alarm("prev"));

//        View right_area = findViewById(R.id.right_area);
//        View left_area = findViewById(R.id.left_area);

        playerView.setOnTouchListener(new View.OnTouchListener() {
            private View view;
            private GestureDetector gestureDetector = new GestureDetector(VideoActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent event) {
                    Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    if (view != null && player != null) {
                        if (event.getRawX() >= (view.getWidth() / 2.0)) {
                            player.seekTo(player.getContentPosition() + 10000);
                            // TODO: 08.10.2020 Добавить анимацию
                            Log.d("TEST", "onDoubleTap right");
                        } else {
                            player.seekTo(player.getContentPosition() + 10000);
                            // TODO: 08.10.2020 Добавить анимацию
                            Log.d("TEST", "onDoubleTap left");
                        }
                    }
                    return super.onDoubleTap(event);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                view = v;
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });


        videoPresenter = new VideoPresenter(this);

        videoPresenter.loadData(episode);

        initRecycle();

    }

    void initRecycle() {
        ArrayList<Integer> viewColors = new ArrayList<>();
        viewColors.add(Color.BLUE);
        viewColors.add(Color.YELLOW);
        viewColors.add(Color.MAGENTA);
        viewColors.add(Color.RED);
        viewColors.add(Color.BLACK);

        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(VideoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, viewColors, animalNames);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "orient: " + newConfig.orientation);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            enterFullscreen();
        else
            exitFullscreen();
        super.onConfigurationChanged(newConfig);
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

    private void Show() {
        window.getDecorView().setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // TODO: 08.10.2020 реализовать настройки видео
//        switch (item.getItemId()) {
//            case R.id.action_search :
//                Log.i("item id ", item.getItemId() + "");
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    void enterFullscreen() {
        fullscreen = true;
        Log.d(TAG, "enter full");
        ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
//        fullscreenBtn.setBackgroundResource(R.drawable.exo_controls_fullscreen_exit);
        layoutParams.height = MATCH_PARENT;
    }

    void exitFullscreen() {
        fullscreen = false;
        Log.d(TAG, "exit full");
        ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
//        fullscreenBtn.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);
        layoutParams.height = primaryLayoutHeight;
    }


    private void fullscreenChange(boolean fromBtn) {
        Log.d(TAG, "height: " + primaryLayoutHeight);
        ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
        if (layoutParams.height == MATCH_PARENT) {
//            Show();
            if (fromBtn)
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            else
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            fullscreenBtn.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);
            layoutParams.height = primaryLayoutHeight;
            fullscreen = false;
        } else {
            fullscreen = true;
            if (fromBtn)
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            fullscreenBtn.setBackgroundResource(R.drawable.exo_controls_fullscreen_exit);
            layoutParams.height = MATCH_PARENT;
        }
        playerView.setLayoutParams(layoutParams);
    }


    @Override
    public void initPlayer(String uri) {
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
    public void initSpinnerClickListener() {
        voicesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                videoPresenter.getVideo(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                videoPresenter.onStart();
            }
        });
    }

    @Override
    public void alarm(String message) {
        Snackbar.make(videoContainer, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.red))
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
        if (player != null)
            currentPosition = player.getCurrentPosition();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
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
    public void fillSpiner(ArrayList<Player> players) {
        PlayersAdapter adapter = new PlayersAdapter(players);
        voicesList.setAdapter(adapter);
    }

    @Override
    public void fillToolbar(String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }
}
