package com.starostinvlad.fan.VideoScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PictureInPictureParams
import android.app.UiModeManager
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.util.Rational
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appodeal.ads.Appodeal
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.starostinvlad.fan.Adapters.DialogRVAdapter
import com.starostinvlad.fan.Adapters.OnItemSelectListener
import com.starostinvlad.fan.Adapters.SeasonRecyclerViewAdapter
import com.starostinvlad.fan.App
import com.starostinvlad.fan.R
import com.starostinvlad.fan.VideoScreen.PlayerModel.Episode
import com.starostinvlad.fan.VideoScreen.PlayerModel.Hls
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial
import com.starostinvlad.fan.VideoScreen.PlayerModel.Translation

class VideoActivity : AppCompatActivity(), VideoActivityContract {
    private val TAG: String = this::class.simpleName!!
    var quality = mapOf(
            "360" to 414000,
            "480" to 714000,
            "720" to 1064000,
            "1080" to 5055521
    )
    lateinit var playerView: PlayerView
    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var videoContainer: View? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var videoPresenter: VideoPresenter? = null
    private var currentPosition: Long = 0
    private var isInPIPMode = false
    private var trackSelector: DefaultTrackSelector? = null
    private var uiModeManager: UiModeManager? = null
    private var exoRew: Button? = null
    private var exoFfwd: Button? = null
    private var subtitlesBtn: ImageButton? = null
    private var doubleTap = false
    private var doubleClickArea: View? = null
    private var mDetector: GestureDetectorCompat? = null


    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_video)
        playerView = findViewById(R.id.player_view_id)
        toolbar = findViewById(R.id.video_fragment_toolbar)
        progressBar = findViewById(R.id.video_progress_id)
        videoContainer = findViewById(R.id.video_container)
        mDetector = GestureDetectorCompat(this, MyGestureListener())

//        if (getIntent() != null && getIntent().hasExtra(getString(R.string.episode_extra)))
//            episode = (News) getIntent().getSerializableExtra(getString(R.string.episode_extra));
//        else if (getIntent() != null && getIntent().hasExtra("NAME")) {
//            episode = new News();
//            episode.setTitle(getIntent().getStringExtra("NAME"));
//            episode.setHref(getIntent().getStringExtra("HREF"));
//        } else {
//            showDialog(getString(R.string.video_error));
//        }
        uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        hideSystemsElements()
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }


//        Log.d(TAG, "episode: " + episode.getSubTitle());
        trackSelector = DefaultTrackSelector(this)
        val defaultTrackParam = trackSelector!!.buildUponParameters().build()
        trackSelector!!.parameters = defaultTrackParam
        with(playerView) {
            controllerShowTimeoutMs = 2000
            controllerAutoShow = true
            hideController()
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            simpleExoPlayer = SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector!!).build()
            player = simpleExoPlayer
        }


        exoFfwd = findViewById(R.id.exo_ffwd)
        exoRew = findViewById(R.id.exo_rew)
        val voices = findViewById<ImageButton>(R.id.voice_btn)
        subtitlesBtn = findViewById(R.id.btn_subtitle)
        subtitlesBtn?.setOnClickListener { subtitleSelectorDialog() }
        voices.setOnClickListener { videoPresenter!!.onBuildDialog() }
        doubleClickArea = findViewById(R.id.doubleClickArea)
        videoPresenter = VideoPresenter()
        videoPresenter!!.attachView(this)
        if (intent != null && intent.hasExtra("SERIAL")) {
            videoPresenter!!.onStartWithSerial(intent.getSerializableExtra("SERIAL") as Serial)
        } else if (App.instance.isReview) {
            playerView.hideController()
            showDialog(getString(R.string.on_review))
        } else {
            Appodeal.show(this, Appodeal.INTERSTITIAL)
            //            videoPresenter.loadData(episode.getHref());
        }
    }

    override fun showDialog(msg: String) {
        AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .setOnDismissListener { finish() }.show()
    }

    override fun openTrailer(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun changeDescription(title: String, subTitle: String) {
        toolbar!!.title = title
        toolbar!!.subtitle = subTitle
        setSupportActionBar(toolbar)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (!playerView.isControllerVisible) playerView.showController()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun initRecycle(episodes: List<Episode>) {
        val recyclerView = findViewById<RecyclerView>(R.id.episodeListRV)
        try {
            val horizontalLayoutManager = LinearLayoutManager(this@VideoActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = horizontalLayoutManager
            val adapter = SeasonRecyclerViewAdapter()
            adapter.items = episodes.toMutableList()
            adapter.mClickListener = { it ->
                videoPresenter!!.onChangeEpisode(it)
                Appodeal.show(this, Appodeal.INTERSTITIAL)
            }
            recyclerView.adapter = adapter
            Log.d(TAG, "episodes: $episodes")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "fill complete")
    }

    override fun translationSelectorDialog(translations: List<Translation>) {
        val dialogFragment = DialogFragment(items = translations.map { translation -> translation.title }, callback = object : DialogCallback {
            override fun onSelect(position: Int) {
                videoPresenter!!.onChangeTranslation(position)
            }
        })
        dialogFragment.show(supportFragmentManager, "Озвучка")
    }

    override fun qualitySelectorDialog() {
        val dialogFragment =
                DialogFragment(items = quality.keys.toList(),
                        callback = object : DialogCallback {
                            override fun onSelect(position: Int) {
                                val parameters = trackSelector!!.buildUponParameters()
                                        .setMaxVideoBitrate(quality.getValue(quality.keys.toList()[position]))
                                        .setForceHighestSupportedBitrate(true)
                                        .build()
                                trackSelector!!.parameters = parameters
                            }
                        })
        dialogFragment.show(supportFragmentManager, "Качество")
    }

    private fun subtitleSelectorDialog() {
        val subs = simpleExoPlayer!!
                .currentMediaItem!!
                .playbackProperties!!
                .subtitles
                .map { it.language!! }

        val dialogFragment = DialogFragment(items = subs, callback = object : DialogCallback {
            override fun onSelect(position: Int) {
                val parameters = trackSelector!!.buildUponParameters()
                        .setPreferredTextLanguage(
                                subs[position]
                        )
                        .build()
                trackSelector!!.parameters = parameters
            }
        })
        dialogFragment.show(supportFragmentManager, "Субтитры")
    }

    private fun hideSystemsElements() {
        with(window) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
            setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.video, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.pip -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && uiModeManager!!.currentModeType != Configuration.UI_MODE_TYPE_TELEVISION) {
                    currentPosition = simpleExoPlayer!!.currentPosition
                    val pictureInPictureParams = PictureInPictureParams.Builder()
                            .setAspectRatio(Rational(16, 9))
                            .build()
                    enterPictureInPictureMode(pictureInPictureParams)
                    playerView.hideController()
                    simpleExoPlayer!!.play()
                }
                Log.i(TAG, "id " + item.itemId)
                return true
            }
            R.id.quality -> {
                qualitySelectorDialog()
                Log.i(TAG, "id " + item.itemId)
                return true
            }
            R.id.crop_video -> {
                if (playerView.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT) playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM else playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                Log.i(TAG, "id " + item.itemId)
                return true
            }
        }
        return false
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        Log.d(TAG, "OnPip mode")
        isInPIPMode = isInPictureInPictureMode
        //        if (player != null)
//            player.seekTo(currentPosition);
    }

    override fun initPlayer(hls: Hls) {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector!!).build()
            playerView.player = simpleExoPlayer
        }
        Log.d(TAG, "uri: " + hls.src)
        val subtitles: MutableList<MediaItem.Subtitle> = ArrayList()
        if (hls.enSub.isNotEmpty() && hls.enSub != "false") {
            val sub = Uri.parse(hls.enSub)
            val subtitle = MediaItem.Subtitle(sub, MimeTypes.TEXT_VTT, "en", Format.NO_VALUE)
            subtitles.add(subtitle)
        }
        if (hls.ruSub.isNotEmpty() && hls.ruSub != "false") {
            val sub = Uri.parse(hls.ruSub)
            val subtitle = MediaItem.Subtitle(sub, MimeTypes.TEXT_VTT, "ru", Format.NO_VALUE)
            subtitles.add(subtitle)
        }
        if (subtitles.isEmpty()) {
            subtitlesBtn!!.visibility = View.GONE
        } else {
            subtitlesBtn!!.visibility = View.VISIBLE
        }
        val mediaItem = MediaItem.Builder()
                .setUri(hls.src)
                .setSubtitles(subtitles)
                .build()
        Log.d(TAG, "media item: " + mediaItem.playbackProperties!!.uri)
        if (simpleExoPlayer != null) {
            with(simpleExoPlayer!!) {
                setMediaItem(mediaItem)
                setForegroundMode(true)
                prepare()
                seekTo(currentPosition)
            }
        }
    }

    override fun alarm(message: String) {
        Snackbar.make(videoContainer!!, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.RED)
                .show()
    }

    private fun releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer!!.release()
            simpleExoPlayer = null
        }
    }

    public override fun onStart() {
        super.onStart()
        videoPresenter!!.onStart()
        val mediaSession = MediaSessionCompat(this, packageName)
        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(simpleExoPlayer)
        mediaSession.isActive = true
    }

    public override fun onPause() {
        super.onPause()
        if (!isInPIPMode) {
            if (simpleExoPlayer != null) {
                currentPosition = simpleExoPlayer!!.currentPosition
            }
            if (Util.SDK_INT <= 23) {
                releasePlayer()
            }
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPresenter!!.onDetach()
    }

    override fun showLoading(show: Boolean) {
        videoContainer!!.isVisible = !show
        toolbar!!.isVisible = !show
        progressBar!!.isVisible = show
    }

    override fun fillToolbar(title: String) {
        toolbar!!.title = title
        setSupportActionBar(toolbar)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "dispatchTouchEvent: dispatch touch")
        if (uiModeManager!!.currentModeType != Configuration.UI_MODE_TYPE_TELEVISION) mDetector!!.onTouchEvent(event)
        Log.d(TAG, "dispatchTouchEvent: doubleTap 1:$doubleTap")
        if (!doubleTap) {
            hideSystemsElements()
            return playerView.dispatchTouchEvent(event)
        }
        doubleTap = false
        //        else
//            return mDetector.onTouchEvent(event);
//            return true;
        return super.dispatchTouchEvent(event)
    }

    internal inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            Log.d(TAG, "onDown: $event")
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            doubleTap = true
            return super.onDoubleTapEvent(e)
        }

        override fun onDoubleTap(event: MotionEvent): Boolean {
            Log.d(TAG, "onDoubleTapEvent: tap " + event.rawX + " : " + playerView.width / 2.0)
            doubleTap = false
            doubleClickArea!!.performClick()
            if (event.rawX > playerView.width / 2.0) {
                exoFfwd!!.performClick()
            } else {
                exoRew!!.performClick()
            }
            return super.onDoubleTap(event)
        }
    }

    companion object {
        fun start(activity: Activity, serial: Serial?) {
            val intent = Intent(activity, VideoActivity::class.java)
            if (serial != null) intent.putExtra("SERIAL", serial)
            activity.startActivity(intent)
        }
    }

    class DialogFragment(private val callback: DialogCallback, var items: List<String>) : BottomSheetDialogFragment() {

        private lateinit var recycleView: RecyclerView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

            dialog.setOnShowListener {
                val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(bottomSheet)
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            return dialog
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.bottom_sheet_dialog, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            recycleView = view.findViewById(R.id.bottomSheetRV)
            val translationAdapter = DialogRVAdapter(object : OnItemSelectListener {
                override fun onSelect(position: Int) {
                    callback.onSelect(position)
                    dismiss()
                }
            })
            translationAdapter.items = items
            recycleView.adapter = translationAdapter

        }
    }

    interface DialogCallback {
        fun onSelect(position: Int)
    }
}