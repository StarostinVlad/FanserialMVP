package com.starostinvlad.fan.LoginScreen;


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
import com.starostinvlad.fan.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements LoginFragmentContract {

    private final String TAG = getClass().getSimpleName();
    private Button regBtn, loginBtn;
    private EditText emailField, passField, nameField;
    private ProgressBar progress;
    private LinearLayout loginContainer;
    private LoginPresenter presenter;
    private boolean isRegistry = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn = view.findViewById(R.id.btn_login);
        regBtn = view.findViewById(R.id.btn_registry);
        emailField = view.findViewById(R.id.email_field);
        passField = view.findViewById(R.id.password_field);
        nameField = view.findViewById(R.id.name_field);
        progress = view.findViewById(R.id.login_progress);
        loginContainer = view.findViewById(R.id.login_container);
        presenter = new LoginPresenter(this);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Загрузка");

        regBtn.setOnClickListener(v -> {
            isRegistry = !isRegistry;
            if (isRegistry) {
                nameField.setVisibility(View.VISIBLE);
                loginBtn.setText("Регистрация");
                regBtn.setText("Войти");
            } else {
                nameField.setVisibility(View.GONE);
                regBtn.setText("Регистрация");
                loginBtn.setText("Войти");
            }
        });

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loginBtn.setEnabled(isValidPass() && isValidEmail() && isValidName());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isRegistry)
                    loginBtn.setEnabled(isValidPass() && isValidEmail());
                else
                    loginBtn.setEnabled(isValidPass() && isValidEmail() && isValidName());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isRegistry)
                    loginBtn.setEnabled(isValidPass() && isValidEmail());
                else
                    loginBtn.setEnabled(isValidPass() && isValidEmail() && isValidName());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginBtn.setOnClickListener(view1 -> {
            if (isRegistry)
                presenter.registryApi(emailField.getText().toString(),
                        passField.getText().toString(),
                        nameField.getText().toString()
                );
            else
                presenter.loginApi(emailField.getText().toString(),
                        passField.getText().toString());
        });
        return view;
    }

    @Override
    public void showLoading(boolean load) {
        progress.setVisibility(load ? View.VISIBLE : View.GONE);
        loginContainer.setVisibility(!load ? View.VISIBLE : View.GONE);
    }

    private boolean isValidName() {
        String name = nameField.getText().toString();
        return (name.length() > 4 && name.length() < 31);
    }


    private boolean isValidPass() {
        String pass = passField.getText().toString();
        if (!isRegistry)
            return (pass.length() > 5 && pass.length() < 31);
        else
            return (pass.length() > 8 && pass.length() < 31);
    }

    private boolean isValidEmail() {
        String email = emailField.getText().toString();
        return !email.isEmpty();
    }

    @Override
    public void alarm(String message) {
        if (message.contains("уже используется"))
            message = "Данный E-mail уже используется!";
        if (message.contains("Email адрес указан неверно"))
            message = "Email адрес указан неверно!";
        if (message.contains("User not founded"))
            message = "Пользователь не найден!";
        if (message.contains("Bad email format!"))
            message = "Неверный формат email!";
        if (getView() != null)
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

}
