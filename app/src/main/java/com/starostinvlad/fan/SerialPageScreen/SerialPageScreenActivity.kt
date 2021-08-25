package com.starostinvlad.fan.SerialPageScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import com.starostinvlad.fan.Adapters.ReleaseDateListAdapter
import com.starostinvlad.fan.Adapters.SerialSeasonListAdapter
import com.starostinvlad.fan.Adapters.SimpleTextListAdapter
import com.starostinvlad.fan.App
import com.starostinvlad.fan.BlurTransformation
import com.starostinvlad.fan.BlurTransformationForBackground
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.R
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer
import com.starostinvlad.fan.VideoScreen.VideoActivity

class SerialPageScreenActivity : AppCompatActivity(), SerialPageScreenContract {
    private var episode: News? = null
    private var presenter: SerialPageScreenPresenter? = null
    private var progressBar: ProgressBar? = null
    private lateinit var description: TextView
    private var titleReleaseDate: TextView? = null
    private var toolbar: Toolbar? = null
    private lateinit var openSerial: Button
    private var imageView: ImageView? = null
    private var backgroundImage: ImageView? = null
    private var serialPageInfoAdapter: SimpleTextListAdapter? = null
    private var serialPageReleaseDatesAdapter: ReleaseDateListAdapter? = null
    private var serialSeasonsAdapter: SerialSeasonListAdapter? = null
    private var subscribed = false
    private val TAG: String = this::class.simpleName!!
    var uiModeManager: UiModeManager? = null
    override fun onDestroy() {
        if (presenter != null) presenter!!.detachView()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager != null && uiModeManager!!.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) setContentView(R.layout.activity_serial_page_tv) else setContentView(R.layout.activity_serial_page)
        serialPageInfoAdapter = SimpleTextListAdapter(this)
        serialPageReleaseDatesAdapter = ReleaseDateListAdapter(this)
        serialSeasonsAdapter = SerialSeasonListAdapter()
        toolbar = findViewById(R.id.toolbarSerialScreen)
        description = findViewById(R.id.serialPageDescription)
        titleReleaseDate = findViewById(R.id.titleReleaseDate)
        openSerial = findViewById(R.id.btnOpenSerial)
        openSerial.setOnClickListener {
            if (!App.instance.isReview)
                presenter!!.openSerialOnClick()
            else
                openWebSite()
        }
        openSerial.onFocusChangeListener = OnFocusChangeListener { view: View, b: Boolean ->
            if (uiModeManager!!.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) if (b) {
                view.animate().scaleY(1.2f).scaleX(1.2f).z(1.2f).start()
            } else {
                view.animate().scaleY(1f).scaleX(1f).z(1f).start()
            }
        }
        val serialPageInfoList = findViewById<RecyclerView>(R.id.serialPageInfoList)
        serialPageInfoList.isNestedScrollingEnabled = false
        serialPageInfoList.setHasFixedSize(true)
        val serialPageReleaseDates = findViewById<RecyclerView>(R.id.serialPageReleaseDates)
        serialPageReleaseDates.setHasFixedSize(true)
        serialPageReleaseDates.isNestedScrollingEnabled = false
        val serialSeasons = findViewById<RecyclerView>(R.id.serialSeasonList)
        serialSeasons.setHasFixedSize(true)
        serialSeasons.isNestedScrollingEnabled = false
        serialSeasons.adapter = serialSeasonsAdapter
        serialPageInfoList.adapter = serialPageInfoAdapter
        serialPageReleaseDates.adapter = serialPageReleaseDatesAdapter
        imageView = findViewById(R.id.serialPoster)
        backgroundImage = findViewById(R.id.expandedImage)
        progressBar = findViewById(R.id.serialPageProgressBar)
        if (intent != null && intent.hasExtra(getString(R.string.episode_extra))) episode = intent.getSerializableExtra(getString(R.string.episode_extra)) as News
        presenter = SerialPageScreenPresenter()
        presenter!!.attachView(this)
        presenter!!.loadData(episode?.href!!)
    }

    private fun openWebSite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://starostinvlad.github.io/"))
        startActivity(intent)
    }

    override fun showLoading(show: Boolean) {
        progressBar!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    override fun fillBtn(currentSeasonIndex: Int, currentEpisodeIndex: Int) {
        openSerial.text = "Открыть $currentSeasonIndex сезон $currentEpisodeIndex серию"
    }

    override fun fillPage(serialPlayer: SerialPlayer?) {
        toolbar?.title = serialPlayer?.title
        setSupportActionBar(toolbar)
        Picasso.get()
                .load(episode?.image)
                .transform(BlurTransformation(this))
                .placeholder(R.drawable.banner)
                .into(imageView)
        Picasso.get()
                .load(episode?.image)
                .placeholder(R.drawable.banner)
                .transform(BlurTransformationForBackground(this))
                .into(backgroundImage)
        description.text = serialPlayer?.description
        serialPageInfoAdapter!!.setItemList(serialPlayer!!.infoList)
        if (serialPlayer.releaseDates.isNotEmpty()) {
            titleReleaseDate!!.visibility = View.VISIBLE
            serialPageReleaseDatesAdapter!!.setItemList(serialPlayer.releaseDates)
        }
        openSerial.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_video -> {
                Log.i(TAG, "id " + item.itemId)
                share()
                return true
            }
            R.id.subscribe_checker -> {
                item.isChecked = !item.isChecked
                presenter!!.putToSubscribe(episode?.siteId, item.isChecked)
                if (item.isChecked) {
                    item.setIcon(R.drawable.ic_favorite_checked)
                    FirebaseMessaging.getInstance().subscribeToTopic(episode?.siteId!!)
                } else {
                    item.setIcon(R.drawable.ic_favorite_unchecked)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(episode?.siteId!!)
                }
                Log.d(TAG, "item id: ${item.itemId} = ${item.isChecked}")
                return false
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.serial_screen, menu)
        val subscribeItem = menu.findItem(R.id.subscribe_checker)
        if (App.instance.loginSubject.value!!.isEmpty()) {
            subscribeItem.isEnabled = false
            subscribeItem.setIcon(R.drawable.ic_favorite_disable)
        } else {
            subscribeItem.isChecked = subscribed
            subscribeItem.setIcon(
                    if (subscribed) R.drawable.ic_favorite_checked else R.drawable.ic_favorite_unchecked
            )
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun fillSeasonsList(serial: Serial) {
        serialSeasonsAdapter!!.setItemList(serial.seasonList)
        if (!App.instance.isReview)
            serialSeasonsAdapter!!.itemClickListener = { position: Int ->
                serial.currentEpisodeIndex = position
                VideoActivity.start(this, serial)
            }
    }

    override fun checkViewed(viewed: Boolean) {
//        this.viewed = viewed;
        supportInvalidateOptionsMenu()
    }

    override fun checkSubscribed(subscribed: Boolean) {
        this.subscribed = subscribed
        supportInvalidateOptionsMenu()
    }

    private fun share() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        val url = if (episode?.href!!.contains(App.instance.domain)) episode?.href else App.instance.domain + episode?.href
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Смотри сериал ${toolbar!!.title} по ссылке $url")
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun openActivityWithSerial(serial: Serial?) {
        VideoActivity.start(this, serial)
    }

    companion object {
        fun start(context: Activity?, news: News?) {
            val intent = Intent(context, SerialPageScreenActivity::class.java)
            if (news != null) intent.putExtra(context!!.getString(R.string.episode_extra), news)
            context!!.startActivity(intent)
        }
    }
}