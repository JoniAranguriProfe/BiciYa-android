package com.educacionit.biciya.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RequestDao {
    @Insert
    suspend fun insertRequest(request: RequestEntity)

    @Query("SELECT * FROM requests")
    suspend fun getAllRequests(): List<RequestEntity>

    @Query("SELECT * FROM requests WHERE active = 1 LIMIT 1")
    suspend fun getActiveRequest(): RequestEntity?

    @Query("SELECT * FROM requests WHERE active = 0 ORDER BY expirationDate DESC")
    suspend fun getInactiveRequests(): List<RequestEntity>

    @Query("UPDATE requests SET active = 0 WHERE active = 1")
    suspend fun deactivateActiveRequest()
}