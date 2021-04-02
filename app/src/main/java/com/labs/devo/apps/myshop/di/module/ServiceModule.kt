package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalRecurringEntryService
import com.labs.devo.apps.myshop.data.db.local.database.dao.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.RecurringEntryDao
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalEntryServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalNotebookServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalPageServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalRecurringEntryServiceImpl
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.*
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.AccountServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.UserServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.*
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
        notebookDao: NotebookDao
    ): LocalNotebookService =
        LocalNotebookServiceImpl(notebookDao)

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
        pageDao: PageDao
    ): LocalPageService =
        LocalPageServiceImpl(pageDao)

    @Provides
    @Singleton
    fun provideRemoteEntryService(
        remoteEntryMapper: RemoteEntryMapper
    ): RemoteEntryService =
        RemoteEntryServiceFirebaseImpl(remoteEntryMapper)

    @Provides
    @Singleton
    fun provideLocalEntryService(
        entryDao: EntryDao,
    ): LocalEntryService =
        LocalEntryServiceImpl(entryDao)

    @Provides
    @Singleton
    fun provideRecurringRemoteEntryService(
        remoteRecurringEntryMapper: RemoteRecurringEntryMapper
    ): RemoteRecurringEntryService =
        RemoteRecurringEntryServiceFirebaseImpl(remoteRecurringEntryMapper)

    @Provides
    @Singleton
    fun provideLocalRecurringEntryService(
        entryDao: RecurringEntryDao,
    ): LocalRecurringEntryService =
        LocalRecurringEntryServiceImpl(entryDao)

    @Provides
    @Singleton
    fun provideMicroRemoteEntryService(
        remoteMicroEntryMapper: RemoteMicroEntryMapper
    ): RemoteMicroEntryService =
        RemoteMicroEntryServiceFirebaseImpl(remoteMicroEntryMapper)


}