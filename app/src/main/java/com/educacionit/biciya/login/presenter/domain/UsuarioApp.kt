package com.educacionit.biciya.login.presenter.domain

enum class TipoUsuario{
    GRATIS,PREMIUM, AVANZADO
}

data class UsuarioApp(val nombre:String, val correo: String, val tipo: TipoUsuario)
