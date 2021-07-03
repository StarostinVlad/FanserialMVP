package com.starostinvlad.fan.SerialPageScreen;

import android.util.Log;

import com.starostinvlad.fan.App;
import com.starostinvlad.fan.BaseMVP.BasePresenter;
import com.starostinvlad.fan.GsonModels.CurrentSerial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.Serial;
import com.starostinvlad.fan.VideoScreen.PlayerModel.SerialPlayer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SerialPageScreenPresenter extends BasePresenter {

    private final SerialPageScreenContract view;
    private final SerialPlayer serialPlayer;
    private Serial serial;
    private final String TAG = getClass().getSimpleName();

    SerialPageScreenPresenter(SerialPageScreenContract serialPageScreenContract) {
        this.view = serialPageScreenContract;
        serialPlayer = new SerialPlayer(App.getInstance().getDomain(), App.getInstance().getOkHttpClient());
    }

    void putToSubscribe(String id, boolean checked) {
        disposables.add(
                serialPlayer.subscribeRequest(id, checked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                val -> Log.d(TAG, "answer: " + val),
                                throwable -> {
//                                    sendErrorMsg(throwable.getMessage());
                                    throwable.printStackTrace();
                                }
                        )
        );
    }

    void loadData(String url) {
        view.showLoading(true);

        disposables.add(
                serialPlayer
                        .getSerial(url)
                        .flatMap(
                                val -> App.getInstance()
                                        .getDatabase()
                                        .currentSerialDao()
                                        .getById(val.getId())
                                        .defaultIfEmpty(new CurrentSerial(
                                                val.getId(),
                                                val.getCurrentSeasonIndex(),
                                                val.getCurrentEpisodeIndex(),
                                                val.getCurrentTranslationIndex()
                                        )),
                                ((serial1, currentSerial) -> {
                                    if (currentSerial != null) {
                                        serial1.setCurrentSeasonIndex(currentSerial.getCurrentSeasonIndex());
                                        serial1.setCurrentEpisodeIndex(currentSerial.getCurrentEpisodeIndex());
                                        serial1.setCurrentTranslationIndex(currentSerial.getCurrentTranslationIndex());
                                    }
                                    return serial1;
                                })
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(serial -> {
                            this.serial = serial;

                            view.fillBtn(
                                    serial.getCurrentSeasonIndex() + 1,
                                    serial.getCurrentEpisodeIndex() + 1
                            );

                            view.fillPage(serialPlayer);
                            view.fillSeasonsList(serial);

                            view.checkSubscribed(serialPlayer.isSubscribed());

                            Log.d(TAG, "loadData: serial: " + serial);
                            view.showLoading(false);
                        }, Throwable::printStackTrace)
        );
    }

    void openSerialOnClick() {
        view.openActivityWithSerial(serial);
    }
}
