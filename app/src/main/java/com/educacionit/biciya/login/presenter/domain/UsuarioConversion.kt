package com.educacionit.biciya.login.presenter.domain

import com.educacionit.biciya.login.model.entities.UsuarioServer


fun UsuarioServer.convertirAUsuarioApp(): UsuarioApp{
    return UsuarioApp(
        nombre = "$name $lastName" ,
        correo = email,
        tipo = obtenerTipo(type),
    )
}

fun obtenerTipo(type: String) = when (type) {
    "premium" -> TipoUsuario.PREMIUM
    "advanced" -> TipoUsuario.AVANZADO
    else -> TipoUsuario.GRATIS
}
