package com.starostinvlad.rxeducation.VideoScreen;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.adapters.PlayersAdapter;
import com.starostinvlad.rxeducation.pojos.Datum;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class VideoFragment extends Fragment implements VideoFragmentContract {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void hideSystemsElements() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window
                    .setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }

    private void Show() {
        window.getDecorView().setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment, container, false);
        if (getArguments() != null && getArguments().containsKey("SERIA"))
            episode = (Datum) getArguments().get("SERIA");

        window = ((AppCompatActivity) getContext()).getWindow();
        toolbar = view.findViewById(R.id.video_fragment_toolbar);

        setHasOptionsMenu(true);

        progressBar = view.findViewById(R.id.video_progress_id);
        voicesList = view.findViewById(R.id.voice_spiner_id);

        videoContainer = view.findViewById(R.id.video_container);

        Log.d(TAG, "episode: " + episode.getEpisode().getName());

        playerView = view.findViewById(R.id.player_view_id); // creating player view

        primaryLayoutHeight = playerView.getLayoutParams().height;

        playerView.setControllerShowTimeoutMs(2000);
        playerView.setControllerAutoShow(true);

        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

        fullscreenBtn = view.findViewById(R.id.exo_fullscreen);

        fullscreenBtn.setOnClickListener(view1 -> fullscreenChange());


        videoPresenter = new VideoPresenter(this);

        videoPresenter.loadData(episode);

        voicesList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                videoPresenter.getVideo(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                videoPresenter.getVideo();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.video, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search :
//                Log.i("item id ", item.getItemId() + "");
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    private void fullscreenChange() {
        Log.d(TAG, "height: " + primaryLayoutHeight);
        ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
        if (layoutParams.height == MATCH_PARENT) {
            Show();
            ((AppCompatActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            fullscreenBtn.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);

            layoutParams.height = primaryLayoutHeight;
        } else {
            hideSystemsElements();
            ((AppCompatActivity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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
        player = new SimpleExoPlayer.Builder(getContext()).build();

        player.setMediaSource(hlsMediaSource);

        playerView.setPlayer(player);
        // Prepare the player.
        player.prepare();
        player.seekTo(currentPosition);
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
        videoPresenter.getVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
    }
}
