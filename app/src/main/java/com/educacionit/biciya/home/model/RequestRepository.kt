package com.educacionit.biciya.home.model

import com.educacionit.biciya.data.database.RequestDao
import com.educacionit.biciya.data.database.RequestEntity

class RequestRepository(private val dao: RequestDao) {

    suspend fun insertRequest(request: RequestEntity) {
        dao.insertRequest(request)
    }

    suspend fun getAllRequests(): List<RequestEntity> {
        return dao.getAllRequests()
    }

    suspend fun getActiveRequest(): RequestEntity? {
        return dao.getActiveRequest()
    }

    suspend fun getInactiveRequests(): List<RequestEntity> {
        return dao.getInactiveRequests()
    }

    suspend fun setInactiveRequest(request: RequestEntity) {
        dao.deactivateActiveRequest()
    }
}