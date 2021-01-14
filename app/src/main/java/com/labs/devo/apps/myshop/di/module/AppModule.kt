package com.labs.devo.apps.myshop.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.auth.implementation.FirebaseUserAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesUserAuth(auth: FirebaseAuth, db: FirebaseFirestore): UserAuth =
        FirebaseUserAuth(auth, db)

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}