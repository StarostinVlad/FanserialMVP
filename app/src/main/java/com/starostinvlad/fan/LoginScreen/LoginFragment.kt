package com.starostinvlad.fan.LoginScreen

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.starostinvlad.fan.App
import com.starostinvlad.fan.R

class LoginFragment : Fragment(), LoginFragmentContract {
    private val TAG: String = this::class.simpleName!!
    private var regBtn: Button? = null
    private var loginBtn: Button? = null
    private var emailField: EditText? = null
    private var passField: EditText? = null
    private var nameField: EditText? = null
    private var progress: ProgressBar? = null
    private var loginContainer: LinearLayout? = null
    private var presenter: LoginPresenter? = null
    private val isRegistry = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        if (presenter != null) {
            presenter!!.detachView()
        }
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        loginBtn = view.findViewById(R.id.btn_login)
        regBtn = view.findViewById(R.id.btn_registry)
        emailField = view.findViewById(R.id.email_field)
        passField = view.findViewById(R.id.password_field)
        nameField = view.findViewById(R.id.name_field)
        progress = view.findViewById(R.id.login_progress)
        loginContainer = view.findViewById(R.id.login_container)
        presenter = LoginPresenter()
        presenter!!.attachView(this)
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Загрузка")
        regBtn?.setOnClickListener(View.OnClickListener { v: View? -> openRegistry() })
        nameField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                loginBtn?.setEnabled(isValidPass && isValidEmail && isValidName)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        emailField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!isRegistry) loginBtn?.setEnabled(isValidPass && isValidEmail) else loginBtn?.setEnabled(isValidPass && isValidEmail && isValidName)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        passField?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!isRegistry) loginBtn?.setEnabled(isValidPass && isValidEmail) else loginBtn?.setEnabled(isValidPass && isValidEmail && isValidName)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        loginBtn?.setOnClickListener(View.OnClickListener { view1: View? ->
            if (isRegistry) presenter!!.registryApi(emailField?.getText().toString(),
                    passField?.getText().toString(),
                    nameField?.getText().toString()
            ) else presenter!!.loginApi(emailField?.getText().toString(),
                    passField?.getText().toString())
        })
        return view
    }

    private fun openRegistry() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("${App.instance.domain}/?do=register"))
        startActivity(browserIntent)
    }

    override fun showLoading(load: Boolean) {
        progress!!.visibility = if (load) View.VISIBLE else View.GONE
        loginContainer!!.visibility = if (!load) View.VISIBLE else View.GONE
    }

    private val isValidName: Boolean
        get() {
            val name = nameField!!.text.toString()
            return name.length > 4 && name.length < 31
        }
    private val isValidPass: Boolean
        get() {
            val pass = passField!!.text.toString()
            return if (!isRegistry) pass.length > 5 && pass.length < 31 else pass.length > 8 && pass.length < 31
        }
    private val isValidEmail: Boolean
        get() {
            val email = emailField!!.text.toString()
            return !email.isEmpty()
        }

    override fun alarm(message: String) {
        if (context != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}