package com.educacionit.biciya.login.model

import com.educacionit.biciya.login.contracts.LoginContract
import com.educacionit.biciya.login.model.entities.UsuarioServer
import kotlinx.coroutines.delay

class LoginException(message: String): RuntimeException(message)

class LoginModelImpl() : LoginContract.Model {
    override suspend fun performLogin(email: String, password: String): UsuarioServer {
        // Simulamos una llamada a un servidor
        delay(4000L)

        if(password == "fake12345"){
            throw LoginException("Usuario inválido!")
        }
        return UsuarioServer(
            name = "Barack",
            lastName = "Obama",
            email = "barack_obama@us.com",
            type = "premium"
        )
    }
}