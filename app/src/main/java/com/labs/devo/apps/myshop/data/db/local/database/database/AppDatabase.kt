package com.labs.devo.apps.myshop.data.db.local.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.labs.devo.apps.myshop.business.helper.allowedOperationMap
import com.labs.devo.apps.myshop.data.db.local.database.dao.RateDao
import com.labs.devo.apps.myshop.data.db.local.database.util.AppDatabaseConverter
import com.labs.devo.apps.myshop.data.models.util.RateLimit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(entities = [RateLimit::class], exportSchema = false, version = 1)
@TypeConverters(AppDatabaseConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rateDao(): RateDao

    class Callback(
        val database: Provider<AppDatabase>,
        private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch {
                val rateDao = database.get().rateDao()
                for (operation in allowedOperationMap) {
                    rateDao.insertRateLimit(RateLimit(operation.key, 0))
                }
            }
        }
    }
}
