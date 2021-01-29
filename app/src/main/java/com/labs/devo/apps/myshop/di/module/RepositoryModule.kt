package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.business.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.business.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.business.account.implementation.AccountRepositoryImpl
import com.labs.devo.apps.myshop.business.account.implementation.UserRepositoryImpl
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.business.notebook.implementation.NotebookRepositoryImpl
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.NotebookService
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.AccountServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.UserServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.NotebookServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.NotebookMapper
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
    fun provideNotebookRepository(notebookService: NotebookService): NotebookRepository =
        NotebookRepositoryImpl(notebookService)

    @Provides
    @Singleton
    fun provideNotebookService(mapper: NotebookMapper): NotebookService =
        NotebookServiceFirebaseImpl(mapper)

    @Provides
    @Singleton
    fun provideNotebookMapper(): NotebookMapper = NotebookMapper()
}