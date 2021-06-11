package com.labs.devo.apps.myshop.di.module

import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemDetailService
import com.labs.devo.apps.myshop.data.db.local.abstraction.item.LocalItemService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalEntryService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalNotebookService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalPageService
import com.labs.devo.apps.myshop.data.db.local.abstraction.notebook.LocalRecurringEntryService
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDetailDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.PageDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.notebook.RecurringEntryDao
import com.labs.devo.apps.myshop.data.db.local.implementation.item.LocalItemDetailServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.item.LocalItemServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalEntryServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalNotebookServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalPageServiceImpl
import com.labs.devo.apps.myshop.data.db.local.implementation.notebook.LocalRecurringEntryServiceImpl
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemDetailService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.item.RemoteItemService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteEntryService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteNotebookService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemotePageService
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.RemoteRecurringEntryService
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.AccountServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.account.UserServiceFirestoreImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.item.RemoteItemDetailEntityFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.item.RemoteItemEntityFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteEntryServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteNotebookServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemotePageServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.implementation.notebook.RemoteRecurringEntryServiceFirebaseImpl
import com.labs.devo.apps.myshop.data.db.remote.mapper.item.RemoteItemDetailMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.item.RemoteItemMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteEntryMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteNotebookMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemotePageMapper
import com.labs.devo.apps.myshop.data.db.remote.mapper.notebook.RemoteRecurringEntryMapper
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
    fun provideRemoteItemService(
        remoteItemMapper: RemoteItemMapper
    ): RemoteItemService =
        RemoteItemEntityFirebaseImpl(remoteItemMapper)

    @Provides
    @Singleton
    fun provideLocalItemService(
        itemDao: ItemDao,
    ): LocalItemService =
        LocalItemServiceImpl(itemDao)

    @Provides
    @Singleton
    fun provideRemoteItemDetailService(
        remoteItemDetailMapper: RemoteItemDetailMapper
    ): RemoteItemDetailService =
        RemoteItemDetailEntityFirebaseImpl(remoteItemDetailMapper)

    @Provides
    @Singleton
    fun provideLocalItemDetailService(
        itemDetailDao: ItemDetailDao,
    ): LocalItemDetailService =
        LocalItemDetailServiceImpl(itemDetailDao)


}