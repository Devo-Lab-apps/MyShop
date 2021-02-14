package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.dao.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalEntryServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalNotebookServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalPageServiceImpl
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalEntryMapper
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalNotebookMapper
import com.labs.devo.apps.myshop.data.db.local.mapper.notebook.LocalPageMapper
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.AccountServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.UserServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteEntryServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteNotebookServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemotePageServiceFirebaseImpl
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
object ServiceModule {


    @Provides
    @Singleton
    fun provideUserService(): UserService = UserServiceFirestoreImpl()

    @Provides
    @Singleton
    fun provideAccountService(): AccountService = AccountServiceFirestoreImpl()

    @Provides
    @Singleton
    fun provideRemoteNotebookService(mapperRemote: RemoteNotebookMapper): RemoteNotebookService =
        RemoteNotebookServiceFirebaseImpl(mapperRemote)

    @Provides
    @Singleton
    fun provideLocalNotebookService(
        notebookDao: NotebookDao,
        localNotebookMapper: LocalNotebookMapper
    ): LocalNotebookService =
        LocalNotebookServiceImpl(localNotebookMapper, notebookDao)

    @Provides
    @Singleton
    fun provideRemotePageService(
        remoteNotebookMapper: RemoteNotebookMapper,
        remotePageMapper: RemotePageMapper,
        localNotebookService: LocalNotebookService
    ): RemotePageService =
        RemotePageServiceFirebaseImpl(remoteNotebookMapper, remotePageMapper, localNotebookService)

    @Provides
    @Singleton
    fun provideLocalPageService(
        pageDao: PageDao,
        localNotebookMapper: LocalPageMapper
    ): LocalPageService =
        LocalPageServiceImpl(localNotebookMapper, pageDao)

    @Provides
    @Singleton
    fun provideRemoteEntryService(
        remoteEntryMapper: RemoteEntryMapper
    ): RemoteEntryService =
        RemoteEntryServiceFirebaseImpl(remoteEntryMapper)

    @Provides
    @Singleton
    fun provideLocalEntryService(
        pageDao: EntryDao,
        localNotebookMapper: LocalEntryMapper
    ): LocalEntryService =
        LocalEntryServiceImpl(pageDao, localNotebookMapper)

}