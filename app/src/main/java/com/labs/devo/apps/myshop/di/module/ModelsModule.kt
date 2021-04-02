package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelsModule {

    @Provides
    @Singleton
    fun provideRemoteNotebookMapper(): RemoteNotebookMapper = RemoteNotebookMapper()


    @Provides
    @Singleton
    fun provideRemotePageMapper(): RemotePageMapper = RemotePageMapper()

    @Provides
    @Singleton
    fun provideRemoteEntryMapper(): RemoteEntryMapper = RemoteEntryMapper()

    @Provides
    @Singleton
    fun provideRemoteRecurringEntryMapper(): RemoteRecurringEntryMapper =
        RemoteRecurringEntryMapper()

    @Provides
    @Singleton
    fun provideRemoteMicroEntryMapper(): RemoteMicroEntryMapper =
        RemoteMicroEntryMapper()

}