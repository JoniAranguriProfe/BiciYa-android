package com.educacionit.biciya.login.contracts

import com.educacionit.biciya.login.model.entities.UsuarioServer

interface LoginContract {

    interface View{
        fun showErrorMessage(message:String)
        fun showSuccessMessage(message:String)
        fun setButtonStatus(enabled:Boolean)
        fun setLoadingVisibility(enabled: Boolean)
        fun goToHomeScreen()
        fun initPresenter()
    }
    interface Presenter{
        suspend fun performLogin(email:String?, password:String?)
        fun validateEmail(email:String): Boolean
        fun validatePassword(password:String):Boolean
    }

    interface Model{
        suspend fun performLogin(email:String, password:String): UsuarioServer
    }
}