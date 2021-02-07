package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.business.notebook.implementation.PageRepositoryImpl
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalNotebookServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalPageServiceImpl
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalNotebookMapper
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalPageMapper
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteNotebookServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemotePageServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteNotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemotePageMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NotebookModule {
    @Provides
    @Singleton
    fun provideNotebookService(mapperRemote: RemoteNotebookMapper): RemoteNotebookService =
        RemoteNotebookServiceFirebaseImpl(mapperRemote)

    @Provides
    @Singleton
    fun provideNotebookMapper(): RemoteNotebookMapper = RemoteNotebookMapper()

    @Provides
    @Singleton
    fun providePageRepository(
        localPageService: LocalPageService,
        remotePageService: RemotePageService
    ): PageRepository =
        PageRepositoryImpl(localPageService, remotePageService)

    @Provides
    @Singleton
    fun provideRemotePageService(
        remoteNotebookMapper: RemoteNotebookMapper,
        remotePageMapper: RemotePageMapper
    ): RemotePageService =
        RemotePageServiceFirebaseImpl(remoteNotebookMapper, remotePageMapper)

    @Provides
    @Singleton
    fun provideLocalPageService(
        pageDao: PageDao,
        localNotebookMapper: LocalPageMapper
    ): LocalPageService =
        LocalPageServiceImpl(localNotebookMapper, pageDao)


    @Provides
    @Singleton
    fun provideLocalNotebookService(
        notebookDao: NotebookDao,
        localNotebookMapper: LocalNotebookMapper
    ): LocalNotebookService =
        LocalNotebookServiceImpl(localNotebookMapper, notebookDao)


    @Provides
    @Singleton
    fun provideRemotePageMapper(): RemotePageMapper = RemotePageMapper()

    @Provides
    @Singleton
    fun provideLocalPageMapper(): LocalPageMapper = LocalPageMapper()

    @Provides
    @Singleton
    fun provideLocalNotebookMapper(): LocalNotebookMapper = LocalNotebookMapper()
}