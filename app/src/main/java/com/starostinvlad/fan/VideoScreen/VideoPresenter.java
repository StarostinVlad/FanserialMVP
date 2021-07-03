package com.starostinvlad.fan.VideoScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BaseMVP.BasePresenter;
import com.starostinvlad.fan.GsonModels.CurrentSerial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Season;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

class VideoPresenter extends BasePresenter<VideoActivityContract> {
    private static final String TAG = VideoPresenter.class.getSimpleName();
    private Serial serial = null;
    private SerialPlayer serialPlayer;

    @Override
    public void attachView(VideoActivityContract mvpView) {
        super.attachView(mvpView);
        serialPlayer = new SerialPlayer(App.getInstance().getDomain(), App.getInstance().getOkHttpClient());
    }

    void onDetach() {
        disposables.dispose();
    }

    void onStartWithSerial(Serial serial) {
        this.serial = serial;
        view.initRecycle(serial.getCurrentSeason().getEpisodes());
        Log.d(TAG, "loadData: serial: " + serial);
        view.showLoading(false);
    }

    private void changeVideoSource() {
//        Log.d(TAG, "getVideo: " + serial.getCurrentEpisodeIndex());
        String url = serial
                .getCurrentSeason()
                .getEpisode(serial.getCurrentEpisodeIndex())
                .getTranslation(serial.getCurrentTranslationIndex())
                .getUrl();
        if (url.contains("youtube")) {
            url = url.startsWith("http") ? url : "http://" + url;
            view.openTrailer(url);
        } else {
            startVideo(url);
        }
        changeDescription();
    }

    private void startVideo(String url) {
        disposables.add(
                serialPlayer.getHlsObject(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::initPlayer, throwable -> {
                            sendErrorMsg(throwable.getMessage());
                            throwable.printStackTrace();
                        })
        );
    }


    void onStart() {
        if (serial != null) {
            changeVideoSource();
        }
    }

    void onBuildDialog() {
        view.translationSelectorDialog(
                serial.getCurrentSeason().getEpisode(serial.getCurrentEpisodeIndex()).getTranslations()
        );
    }

    private void sendErrorMsg(String msg) {
        if (msg != null) {
            if (msg.contains("timeout"))
                msg = "Превышено время ожидания";
            else if (msg.contains("returned null"))
                msg = "Что-то пошло не так";
            else if (msg.contains("review"))
                msg = "Сериал недоступен в вашей стране";
            view.showDialog(msg);
        }
    }

    void onChangeEpisode(int position) {
        if (position >= serial.getCurrentSeason().getEpisodes().size())
            position = serial.getCurrentSeason().getEpisodes().size() - 1;
        int finalPosition = position;
        disposables.add(
                App.getInstance().getDatabase().currentSerialDao()
                        .insert(
                                new CurrentSerial(
                                        serial.getId(),
                                        serial.getCurrentSeasonIndex(),
                                        finalPosition,
                                        serial.getCurrentTranslationIndex()
                                )
                        )
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                val -> Log.d(TAG, "changeVideoSource: " + val),
                                Throwable::printStackTrace
                        )
        );
        serial.setCurrentEpisodeIndex(position);
        Log.d(TAG, "getEpisode: " + serial.getCurrentEpisodeIndex());
        changeVideoSource();
    }

    void onChangeTranslation(int i) {
        serial.setCurrentTranslationIndex(i);
        changeVideoSource();
    }

    private void changeDescription() {
        view.changeDescription(
                serial.getCurrentSeason()
                        .getEpisode(serial.getCurrentEpisodeIndex())
                        .getTranslation(serial.getCurrentTranslationIndex())
                        .getTitle(),
                serial.getCurrentSeason()
                        .getEpisode(serial.getCurrentEpisodeIndex())
                        .getTitle()
        );
    }

}
