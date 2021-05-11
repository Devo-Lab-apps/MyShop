package com.labs.devo.apps.myshop.di.module

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.auth.implementation.FirebaseUserAuth
import com.labs.devo.apps.myshop.const.AppConstants.APP_DATABASE
import com.labs.devo.apps.myshop.const.AppConstants.ITEM_DATABASE
import com.labs.devo.apps.myshop.const.AppConstants.NOTEBOOK_DATABASE
import com.labs.devo.apps.myshop.data.db.local.database.dao.RateDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.RemoteKeyDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDetailDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.PageDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.RecurringEntryDao
import com.labs.devo.apps.myshop.data.db.local.database.database.AppDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.ItemDatabase
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


/**
 * App level module to provide singleton dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesUserAuth(auth: FirebaseAuth): UserAuth = FirebaseUserAuth(auth)

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun providesNotebookDatabase(
        app: Application
    ): NotebookDatabase = Room.databaseBuilder(app, NotebookDatabase::class.java, NOTEBOOK_DATABASE)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesAppDatabase(
        app: Application,
        callback: AppDatabase.Callback
    ): AppDatabase = Room.databaseBuilder(app, AppDatabase::class.java, APP_DATABASE)
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    @Singleton
    fun providesItemDatabase(
        app: Application
    ): ItemDatabase = Room.databaseBuilder(app, ItemDatabase::class.java, ITEM_DATABASE)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideRateLimitDao(appDatabase: AppDatabase): RateDao = appDatabase.rateDao()

    @Provides
    @Singleton
    fun provideItemDao(itemDatabase: ItemDatabase): ItemDao = itemDatabase.itemDao()

    @Provides
    @Singleton
    fun provideItemDetailDao(itemDatabase: ItemDatabase): ItemDetailDao = itemDatabase.itemDetailDao()

    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun providePageDao(database: NotebookDatabase): PageDao = database.pageDao()

    @Provides
    @Singleton
    fun provideNotebookDao(database: NotebookDatabase): NotebookDao = database.notebookDao()

    @Provides
    @Singleton
    fun provideEntryDao(database: NotebookDatabase): EntryDao = database.entryDao()

    @Provides
    @Singleton
    fun provideRecurringEntryDao(database: NotebookDatabase): RecurringEntryDao =
        database.recurringEntryDao()

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: NotebookDatabase): RemoteKeyDao = database.remoteKeyDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}