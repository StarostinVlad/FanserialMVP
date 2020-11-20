package com.starostinvlad.fan.SplashScreen;

import android.os.Bundle;

import com.starostinvlad.fan.MainActivity.MainActivity;
import com.starostinvlad.fan.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity implements SplashScreenContract {

    private SplashScreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        presenter = new SplashScreenPresenter(this);
        presenter.loadSettings();
    }

    @Override
    public void startNextActivity() {
        MainActivity.start(this);
        finish();
    }
}
