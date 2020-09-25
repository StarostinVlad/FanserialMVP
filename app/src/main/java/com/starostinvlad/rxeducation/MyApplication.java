package com.starostinvlad.rxeducation;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Utils.init(this);
        } catch (IllegalStateException e) {
            //TODO
        }
    }
}