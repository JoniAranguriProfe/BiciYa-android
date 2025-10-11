package com.educacionit.biciya.home.contracts.request

import android.view.View
import com.educacionit.biciya.data.database.RequestEntity

interface RequestView {

    fun initView(view: View)
    fun initPresenter()
    fun setLoadingVisibility(isVisible: Boolean)
    fun setListenerViews()
    fun onRequestSaved()
    fun onRequestError(msg: String)
    fun onInactiveRequestsLoaded(requests: List<RequestEntity>)
    fun onActiveRequestLoaded(request: RequestEntity)
    fun setNoActiveRequestsVisibility(isVisible: Boolean)
    fun setInactivesRequestsVisibility(isVisible: Boolean)
}