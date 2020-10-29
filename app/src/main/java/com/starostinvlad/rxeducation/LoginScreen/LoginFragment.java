package com.starostinvlad.rxeducation.LoginScreen;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.starostinvlad.rxeducation.R;

import org.apache.commons.lang.StringUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements LoginFragmentContract {

    private final String TAG = getClass().getSimpleName();
    private Button btn;
    private EditText email_field, pass_field;
    private ProgressBar progress;
    private LinearLayout loginContainer;
    private LoginPresenter presenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btn = view.findViewById(R.id.btn_login);
        email_field = view.findViewById(R.id.email_field);
        pass_field = view.findViewById(R.id.password_field);
        progress = view.findViewById(R.id.login_progress);
        loginContainer = view.findViewById(R.id.login_container);
        presenter = new LoginPresenter(this);

        email_field.setText("mr.starostinvlad@gmail.com");
        pass_field.setText("bcc34a");

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Загрузка");


        email_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn.setEnabled(isValidPass() && isValidEmail());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pass_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn.setEnabled(isValidPass() && isValidEmail());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn.setOnClickListener(view1 -> presenter.login(email_field.getText().toString(), pass_field.getText().toString()));
        return view;
    }

    @Override
    public void showLoading(boolean load) {
        progress.setVisibility(load ? View.VISIBLE : View.GONE);
        loginContainer.setVisibility(!load ? View.VISIBLE : View.GONE);
    }

    private boolean isValidPass() {
        String pass = pass_field.getText().toString();
        return (pass.length() > 5 && pass.length() < 31);
    }

    private boolean isValidEmail() {
        String email = email_field.getText().toString();
        return StringUtils.isNotEmpty(email);
    }

    @Override
    public void alarm(String message) {
        if (message.contains("User not founded"))
            message = "Пользователь не найден!";
        if (message.contains("Bad email format!"))
            message = "Неверный формат email!";

        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

}
