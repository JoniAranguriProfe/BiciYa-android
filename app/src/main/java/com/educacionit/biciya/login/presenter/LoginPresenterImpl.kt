package com.educacionit.biciya.login.presenter

import com.educacionit.biciya.login.contracts.LoginContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginPresenterImpl(
    val loginView: LoginContract.View,
    val loginModel: LoginContract.Model,
    val presenterScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) :
    LoginContract.Presenter {

    override suspend fun performLogin(email: String?, password: String?) {
        presenterScope.launch {
            loginView.setLoadingVisibility(true)
            if (email.isNullOrEmpty() || !validateEmail(email)) {
                loginView.setLoadingVisibility(false)
                loginView.showErrorMessage("El email ingresado no es válido!")
                return@launch
            }
            if (password.isNullOrEmpty() || !validatePassword(password)) {
                loginView.setLoadingVisibility(false)
                loginView.showErrorMessage("La contraseña no cumple con los requisitos!")
                return@launch
            }

            val loginResult = loginModel.performLogin(email, password)
            loginView.setLoadingVisibility(false)
            if (!loginResult) {
                loginView.showErrorMessage("Error en el login!")
                return@launch
            }

            loginView.showSuccessMessage("Logueado exitosamente!")
            loginView.goToHomeScreen()
        }
    }

    override fun validateEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".com")
    }

    override fun validatePassword(password: String): Boolean {
        return password.length >= MIN_LENGTH_PASSWORD
    }

    companion object {
        const val MIN_LENGTH_PASSWORD = 8
    }
}