package com.labs.devo.apps.myshop.di.module

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.auth.implementation.FirebaseUserAuth
import com.labs.devo.apps.myshop.data.db.local.database.dao.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.RemoteKeyDao
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    ) = Room.databaseBuilder(app, NotebookDatabase::class.java, "notebook_database")
        .fallbackToDestructiveMigration()
        .build()


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
    fun provideRemoteKeyDao(database: NotebookDatabase): RemoteKeyDao = database.remoteKeyDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}