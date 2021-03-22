package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.database.database.NotebookDatabase
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.repo.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.data.repo.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.data.repo.account.implementation.AccountRepositoryImpl
import com.labs.devo.apps.myshop.data.repo.account.implementation.UserRepositoryImpl
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.repo.notebook.implementation.EntryRepositoryImpl
import com.labs.devo.apps.myshop.data.repo.notebook.implementation.NotebookRepositoryImpl
import com.labs.devo.apps.myshop.data.repo.notebook.implementation.PageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userService: UserService): UserRepository =
        UserRepositoryImpl(userService)

    @Provides
    @Singleton
    fun provideAccountRepository(accountService: AccountService): AccountRepository =
        AccountRepositoryImpl(accountService)

    @Provides
    @Singleton
    fun provideNotebookRepository(
        localNotebookService: LocalNotebookService,
        remoteNotebookService: RemoteNotebookService
    ): NotebookRepository =
        NotebookRepositoryImpl(localNotebookService, remoteNotebookService)

    @Provides
    @Singleton
    fun providePageRepository(
        localPageService: LocalPageService,
        notebookDatabase: NotebookDatabase,
        remotePageService: RemotePageService
    ): PageRepository =
        PageRepositoryImpl(localPageService, notebookDatabase, remotePageService)

    @Provides
    @Singleton
    fun provideEntryRepository(
        localEntryService: LocalEntryService,
        notebookDatabase: NotebookDatabase,
        remoteEntryService: RemoteEntryService
    ): EntryRepository =
        EntryRepositoryImpl(localEntryService, notebookDatabase, remoteEntryService)


}