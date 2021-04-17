package com.starostinvlad.fan.VideoScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

class VideoPresenter {
    private static final String TAG = VideoPresenter.class.getSimpleName();
    private final VideoActivityContract view;
    private Serial serial = null;
    private CompositeDisposable disposables = new CompositeDisposable();
    private SerialPlayer serialPlayer;

    VideoPresenter(VideoActivityContract view) {
        this.view = view;
        serialPlayer = new SerialPlayer(App.getInstance().getDomain(), App.getInstance().getOkHttpClient());
    }

    void onDetach() {
        disposables.dispose();
    }


    void loadData(String url) {
        view.showLoading(true);

        disposables.add(
                serialPlayer
                        .getSerial(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(serial -> {
                            this.serial = serial;
                            view.translationSelectorDialog(serial.getTranslations());

//                            onStart();

                            view.fillToolbar(serialPlayer.getTitle());
                            view.checkSubscribed(serialPlayer.isSubscribed());

                            Log.d(TAG, "loadData: serial: " + serial);
                            view.showLoading(false);
                        }, Throwable::printStackTrace)
        );

//        Observable.fromCallable(() -> loadPage(url))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(this::getPageAttrs)
//                .observeOn(Schedulers.io())
//                .map(this::getSerial)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(serial ->
//                        {
//                            this.serial = serial;
//                            view.translationSelectorDialog(serial.getTranslations());
//                            view.showLoading(false);
//                            onStart();
//                        }
//                        ,
//                        (exception) -> {
//                            exception.printStackTrace();
//                            sendErrorMsg(exception.getMessage());
//                        }).isDisposed();
    }

    private void changeVideoSource(int index) {
        Log.d(TAG, "getVideo: " + serial.getCurrentEpisode());
        String url = serial.getCurrentTranslation()
                .getEpisodes()
                .get(index)
                .getUrl();
        if (url.contains("youtube")) {
            url = url.startsWith("http") ? url : "http://" + url;
            view.openTrailer(url);
//        } else if (url.contains("/dew/")) {
//            Log.e(TAG, "getSerial: url " + url);
//            url = url.startsWith("https") ? url : "http:" + url;
//            disposables.add(
//                    serialModel
//                            .getSerialFromSecondPlayer(url)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(serial -> {
//                                        this.serial = serial;
//
//                                        view.translationSelectorDialog(serial.getTranslations());
//
////                                        onStart();
//
//                                        view.fillToolbar(serial.getTitle());
//                                        view.checkSubscribed(serial.isSubscibed());
//
//                                        Log.d(TAG, "loadData: serial: " + serial);
//                                        view.showLoading(false);
//                                    },
//                                    Throwable::printStackTrace)
//            );
        } else {
            startVideo(url);
        }
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
            if (!serial.getCurrentTranslation().getEpisodes().get(serial.getCurrentEpisode()).getType().equals(2))
                changeVideoSource(serial.getCurrentEpisode());
        }
    }

    void buildDialog() {
        view.translationSelectorDialog(serial.getTranslations());
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


    void putToSubscribe(String id, boolean checked) {
        disposables.add(
                serialPlayer.subscribeRequest(id, checked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                val -> Log.d(TAG, "answer: " + val),
                                throwable -> {
                                    sendErrorMsg(throwable.getMessage());
                                    throwable.printStackTrace();
                                }
                        )
        );
    }

    void setEpisode(int position) {
        if (position >= serial.getCurrentTranslation().getEpisodes().size())
            position = serial.getCurrentTranslation().getEpisodes().size() - 1;
        serial.setCurrentEpisode(position);
        Log.d(TAG, "getEpisode: " + serial.getCurrentEpisode());
        changeDescription();
        changeVideoSource(position);
    }

    void setTranslation(int i) {
        serial.setCurrentTranslationIndex(i);
        view.initRecycle(serial.getTranslations().get(i));
        setEpisode(serial.getCurrentEpisode());
    }

    private void changeDescription() {
        view.changeDescription(
                serial.getCurrentTranslation().getTitle(),
                serial.getCurrentTranslation()
                        .getEpisodes()
                        .get(serial.getCurrentEpisode())
                        .getTitle()
        );
    }

}
