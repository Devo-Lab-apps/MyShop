package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalEntryMapper
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalNotebookMapper
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalPageMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteEntryMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteNotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemotePageMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ModelsModule {

    @Provides
    @Singleton
    fun provideRemoteNotebookMapper(): RemoteNotebookMapper = RemoteNotebookMapper()


    @Provides
    @Singleton
    fun provideLocalNotebookMapper(): LocalNotebookMapper = LocalNotebookMapper()

    @Provides
    @Singleton
    fun provideRemotePageMapper(): RemotePageMapper = RemotePageMapper()

    @Provides
    @Singleton
    fun provideLocalPageMapper(): LocalPageMapper = LocalPageMapper()


    @Provides
    @Singleton
    fun provideLocalEntryMapper(): LocalEntryMapper = LocalEntryMapper()

    @Provides
    @Singleton
    fun provideRemoteEntryMapper(): RemoteEntryMapper = RemoteEntryMapper()


}