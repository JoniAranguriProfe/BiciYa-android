package com.educacionit.biciya.home.contracts.request

import android.view.View

interface RequestView {

    fun initView(view: View)
    fun initPresenter()
    fun setLoadingVisibility(isVisible: Boolean)
    fun setListenerViews()
}