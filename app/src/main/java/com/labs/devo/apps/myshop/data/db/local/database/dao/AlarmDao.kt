package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*


@Entity(tableName = "alarm_key")
data class AlarmKey(
    @PrimaryKey
    val uniqueId: String,
    val serviceName: String
)

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrReplace(alarmKey: AlarmKey)

    @Query("SELECT * FROM alarm_key WHERE serviceName = :query")
    suspend fun remoteKeyByQuery(query: String): AlarmKey?

    @Query("DELETE FROM alarm_key WHERE serviceName = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM alarm_key")
    suspend fun deleteAll()
}