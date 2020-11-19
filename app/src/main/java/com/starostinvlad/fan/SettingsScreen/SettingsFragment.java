package com.starostinvlad.fan.SettingsScreen;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.starostinvlad.fan.App;
import com.starostinvlad.fan.R;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private Button logout;
    private Button privatePolicy;
    private Button contentPolicy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        logout = view.findViewById(R.id.btn_logout);
        privatePolicy = view.findViewById(R.id.private_policy);
        contentPolicy = view.findViewById(R.id.content_policy);

        privatePolicy.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: click private!");
            PolicyFragmentDialog policyFragmentDialog =
                    PolicyFragmentDialog.newInstance();
            Bundle bundle = new Bundle();
            bundle.putByte("type", (byte) 1);
            policyFragmentDialog.setArguments(bundle);
            policyFragmentDialog.show(getActivity().getSupportFragmentManager(),
                    "add_photo_dialog_fragment");
        });
        contentPolicy.setOnClickListener(v -> {
            Log.d(TAG, "onCreateView: click content!");
            PolicyFragmentDialog policyFragmentDialog =
                    PolicyFragmentDialog.newInstance();
            Bundle bundle = new Bundle();
            bundle.putByte("type", (byte) 2);
            policyFragmentDialog.setArguments(bundle);
            policyFragmentDialog.show(getActivity().getSupportFragmentManager(),
                    "add_photo_dialog_fragment");
        });

        if (App.TOKEN_subject.getValue().isEmpty()) {
            disableLogout();
        } else {
            logout.setOnClickListener(v -> {
                ClearableCookieJar cookieJar =
                        new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));
                cookieJar.clear();
                App.TOKEN_subject.onNext("");
                disableLogout();
            });
        }


        return view;
    }

    private void disableLogout() {
        logout.setEnabled(false);
        logout.setBackgroundColor(Color.DKGRAY);
    }

}
