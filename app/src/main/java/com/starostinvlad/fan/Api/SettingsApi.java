package com.starostinvlad.fan.Api;

import com.starostinvlad.fan.GsonModels.Settings;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface SettingsApi {
    @GET("settings.json")
    Observable<Settings> getSettings();
}
