package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.business.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.business.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.business.account.implementation.AccountRepositoryImpl
import com.labs.devo.apps.myshop.business.account.implementation.UserRepositoryImpl
import com.labs.devo.apps.myshop.business.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.business.notebook.implementation.EntryRepositoryImpl
import com.labs.devo.apps.myshop.business.notebook.implementation.NotebookRepositoryImpl
import com.labs.devo.apps.myshop.business.notebook.implementation.PageRepositoryImpl
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
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
        remotePageService: RemotePageService
    ): PageRepository =
        PageRepositoryImpl(localPageService, remotePageService)

    @Provides
    @Singleton
    fun provideEntryRepository(
        localEntryService: LocalEntryService,
        remoteEntryService: RemoteEntryService
    ): EntryRepository =
        EntryRepositoryImpl(localEntryService, remoteEntryService)


}