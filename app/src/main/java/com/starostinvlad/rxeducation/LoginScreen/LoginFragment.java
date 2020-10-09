package com.starostinvlad.rxeducation.LoginScreen;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.starostinvlad.rxeducation.R;
import com.starostinvlad.rxeducation.Utils;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button btn = view.findViewById(R.id.btn_login);
        btn.setOnClickListener(view1 -> {
            Utils.AUTH = true;
            Utils.AUTH_subject.onNext(true);
        });
        return view;
    }

}
