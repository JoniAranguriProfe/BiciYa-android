package com.educacionit.biciya.home.contracts.request.presenter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.educacionit.biciya.R
import com.educacionit.biciya.home.model.RequestRepository
import com.educacionit.biciya.data.database.RequestEntity
import com.educacionit.biciya.home.contracts.request.RequestView
import com.educacionit.biciya.utils.notification.NotificationHelper
import com.educacionit.biciya.utils.service.StationCheckService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RequestPresenterImpl(
    private val view: RequestView,
    private val repository: RequestRepository,
    private val presenterScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val context: Context

) : RequestPresenter {
    override suspend fun saveRequest(expirationDate: String, distance: Int, bikes: Int) {
        view.setLoadingVisibility(true)

        if (expirationDate.isBlank()) {
            view.onRequestError(context.getString(R.string.no_expiration_date_message))
            return
        }

        try {
            withContext(presenterScope.coroutineContext) {
                val activeRequest = repository.getActiveRequest()

                if (activeRequest != null) {
                    repository.setInactiveRequest(activeRequest)
                }

                val newRequest = RequestEntity(
                    expirationDate = expirationDate,
                    distanceRange = distance,
                    bikesRequested = bikes,
                    active = true
                )
                repository.insertRequest(newRequest)
            }
            Log.e("saveRequest" , "saveRequest - pre-startStationCheckWorker")
            val intent = Intent(context, StationCheckService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            view.onRequestSaved()

        } catch (e: Exception) {
            view.onRequestError(context.getString(R.string.request_error, e.message))
        } finally {
            view.setLoadingVisibility(false)
        }
    }

    override suspend fun getInactiveRequests() {
        view.setLoadingVisibility(true)
        try {
            val requests = withContext(presenterScope.coroutineContext) {
                repository.getInactiveRequests()
            }
            if (requests.isEmpty()) {
                view.setInactivesRequestsVisibility(true)
            } else {
                view.setInactivesRequestsVisibility(false)
                view.setNoRequestsVisibility(false)
                view.onInactiveRequestsLoaded(requests)
            }
        } catch (e: Exception) {
            view.onRequestError(context.getString(R.string.request_error, e.message))
        } finally {
            view.setLoadingVisibility(false)
        }
    }

    override suspend fun getActiveRequest() {
        view.setLoadingVisibility(true)
        try {
            val activeRequests = withContext(presenterScope.coroutineContext) {
                repository.getActiveRequest()
            }

            activeRequests?.let {
                Log.d("getActiveRequest()", "Hay una solicitud activa")
                view.setNoActiveRequestsVisibility(false)
                view.setNoRequestsVisibility(false)
                view.onActiveRequestLoaded(activeRequests)
            } ?: run {
                Log.d("getActiveRequest()", "No hay solicitud activa")
                view.setNoActiveRequestsVisibility(true)
            }
        } catch (e: Exception) {
            view.onRequestError(context.getString(R.string.request_error, e.message))
        } finally {
            view.setLoadingVisibility(false)
        }
    }

    override fun notifyRequestSaved(context: Context) {
        NotificationHelper.showRequestSavedNotification(context)
    }
}