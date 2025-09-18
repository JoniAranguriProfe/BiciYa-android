package com.educacionit.biciya.login.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.educacionit.biciya.R
import com.educacionit.biciya.home.view.HomeActivity
import com.educacionit.biciya.login.contracts.LoginContract
import com.educacionit.biciya.login.model.LoginModelImpl
import com.educacionit.biciya.login.presenter.LoginPresenterImpl
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), LoginContract.View {
    private lateinit var loginButton: MaterialButton
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var loginPresenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initPresenter()
        initViews()
    }

    private fun initViews() {
        loginButton = findViewById(R.id.login_button)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loadingView = findViewById(R.id.loading_view)

        loginButton.setOnClickListener {
            lifecycleScope.launch {
                loginPresenter.performLogin(
                    email = emailInput.text.toString(),
                    password = passwordInput.text.toString(),
                )
            }
        }
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        println("Ocurrio el error $message")
    }

    override fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun setButtonStatus(enabled: Boolean) {
        loginButton.isEnabled = enabled
    }

    override fun setLoadingVisibility(enabled: Boolean) {
        loadingView.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    override fun goToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun initPresenter() {
        val loginModel: LoginContract.Model = LoginModelImpl()
        loginPresenter = LoginPresenterImpl(
            loginView = this,
            loginModel = loginModel,
        )
    }
}