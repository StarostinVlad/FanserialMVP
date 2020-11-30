package com.starostinvlad.fan.SplashScreen;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.Native;
import com.starostinvlad.fan.BuildConfig;
import com.starostinvlad.fan.MainActivity.MainActivity;
import com.starostinvlad.fan.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity implements SplashScreenContract {

    private final String TAG = getClass().getSimpleName();
    private SplashScreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Referer", "https://fanserial.net/58967-zvyozdnyy-put-diskaveri-3-sezon-7-seriya-obedinenie-iii.html");
        webView.loadUrl("https://abiding.thealloha.club/?kp=977754&token=c15aa40d0799388dc4159306388ee9&episode=7&season=3",
                headers);
        webView.setWebViewClient(new MyWebViewClient());

//        if (BuildConfig.BUILD_TYPE.toLowerCase().equals("debug"))
//            Appodeal.setTesting(true);
//        Appodeal.setRequiredNativeMediaAssetType(Native.MediaAssetType.ICON);
//        Appodeal.initialize(this, "5b840848384e83385753354fd57248b212fbd0a454d85083", Appodeal.INTERSTITIAL | Appodeal.NATIVE);

//        presenter = new SplashScreenPresenter(this);
//        presenter.loadSettings();
    }

    @Override
    public void startNextActivity() {
        MainActivity.start(this);
        finish();
    }

    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl().toString());
            view.loadUrl(request.getUrl().toString(), request.getRequestHeaders());
            return true;
        }

        // Для старых устройств
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + url);
            view.loadUrl(url);
            return true;
        }
    }
}

