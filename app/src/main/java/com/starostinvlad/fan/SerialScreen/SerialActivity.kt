package com.starostinvlad.fan.SerialScreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.starostinvlad.fan.Adapters.SerialRecyclerViewAdapter
import com.starostinvlad.fan.GsonModels.News
import com.starostinvlad.fan.GsonModels.Searched
import com.starostinvlad.fan.R

class SerialActivity : AppCompatActivity(), SerialActivityContract {
    var presenter: SerialActivityPresenter? = null
    var listView: RecyclerView? = null
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_serial)
        toolbar = findViewById(R.id.serial_toolbar)
        listView = findViewById(R.id.season_listview)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        listView?.layoutManager = layoutManager
        val searched = intent.getSerializableExtra("url") as Searched
        presenter = SerialActivityPresenter(this)
        toolbar?.title = searched.name
        setSupportActionBar(toolbar)
        presenter!!.loadData(searched.url!!)
    }

    override fun fillList(seasons: List<String>) {
        val serialRecyclerViewAdapter = SerialRecyclerViewAdapter(this)
        serialRecyclerViewAdapter.itemList = (seasons as MutableList<String>)
        listView!!.adapter = serialRecyclerViewAdapter
    }

    companion object {
        fun start(context: Activity, url: News?) {
            val intent = Intent(context, SerialActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }
}