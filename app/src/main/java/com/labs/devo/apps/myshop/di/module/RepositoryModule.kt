package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.business.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.business.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.business.account.implementation.AccountRepositoryImpl
import com.labs.devo.apps.myshop.business.account.implementation.UserRepositoryImpl
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.business.notebook.implementation.NotebookRepositoryImpl
import com.labs.devo.apps.myshop.business.notebook.implementation.PageRepositoryImpl
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.AccountServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.UserServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteNotebookServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemotePageServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.NotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.PageMapper
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
    fun provideUserService(): UserService = UserServiceFirestoreImpl()

    @Provides
    @Singleton
    fun provideAccountService(): AccountService = AccountServiceFirestoreImpl()

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
    fun provideNotebookRepository(remoteNotebookService: RemoteNotebookService): NotebookRepository =
        NotebookRepositoryImpl(remoteNotebookService)

    @Provides
    @Singleton
    fun provideNotebookService(mapper: NotebookMapper): RemoteNotebookService =
        RemoteNotebookServiceFirebaseImpl(mapper)

    @Provides
    @Singleton
    fun provideNotebookMapper(): NotebookMapper = NotebookMapper()

    @Provides
    @Singleton
    fun providePageRepository(remotePageService: RemotePageService): PageRepository =
        PageRepositoryImpl(remotePageService)

    @Provides
    @Singleton
    fun providePageService(notebookMapper: NotebookMapper, pageMapper: PageMapper): RemotePageService =
        RemotePageServiceFirebaseImpl(notebookMapper, pageMapper)

    @Provides
    @Singleton
    fun providePageMapper(): PageMapper = PageMapper()
}