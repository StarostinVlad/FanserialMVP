package com.starostinvlad.fan.HDSeria;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.starostinvlad.fan.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HdSeriaActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    public static void start(Context context) {
        Intent intent = new Intent(context, HdSeriaActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hd_seria);
        Observable.fromCallable(this::loadpage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(val -> Log.d(TAG, "onCreate: succsessfull"), Throwable::printStackTrace).isDisposed();
    }

    String loadpage() throws IOException {
        Document document = Jsoup.connect("https://hdseria.tv/novinki-serialov/").get();
        Elements elements = document.select("#dle-content > div > div.short-text > a");
        for (Element element : elements) {
            Log.d(TAG, "loadpage: " + element.text());
        }
        return "";
    }
}
