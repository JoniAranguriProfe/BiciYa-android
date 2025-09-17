package com.educacionit.biciya.login.presenter

import com.educacionit.biciya.login.contracts.LoginContract
import com.educacionit.biciya.login.model.LoginException
import com.educacionit.biciya.login.presenter.domain.UsuarioApp
import com.educacionit.biciya.login.presenter.domain.convertirAUsuarioApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginPresenterImpl(
    private val loginView: LoginContract.View,
    private val loginModel: LoginContract.Model,
    private val presenterScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
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

            try {
                val userServer = loginModel.performLogin(email, password)
                val usuarioApp = userServer.convertirAUsuarioApp()
                saveUserInStorage(usuarioApp)
                loginView.showSuccessMessage("Bienvenido ${usuarioApp.nombre}")
                loginView.goToHomeScreen()
            } catch (e: LoginException) {
                loginView.showErrorMessage("Error en el login!")
                return@launch
            } finally {
                loginView.setLoadingVisibility(false)
            }

        }
    }

    private fun saveUserInStorage(usuarioApp: UsuarioApp) {
        // Guardar en db local
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