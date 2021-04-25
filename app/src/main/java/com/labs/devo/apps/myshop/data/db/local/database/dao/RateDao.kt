package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*
import com.labs.devo.apps.myshop.data.models.util.RateLimit


@Dao
interface RateDao {

    @Query("SELECT * FROM ratelimit where operationName = :operationName")
    suspend fun getLimit(operationName: String): RateLimit

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRateLimit(rateLimit: RateLimit): Long

    @Update
    suspend fun update(rateLimit: RateLimit)

    @Transaction
    suspend fun upsertRateLimit(rateLimit: RateLimit) {
        val id: Long = insertRateLimit(rateLimit)
        if (id == -1L) {
            update(rateLimit)
        }
    }
}