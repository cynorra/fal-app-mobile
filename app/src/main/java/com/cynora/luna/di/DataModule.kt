package com.cynora.luna.di

import com.cynora.luna.core.data.repository.AuthRepository
import com.cynora.luna.core.data.repository.AuthRepositoryImpl
import com.cynora.luna.core.data.repository.FortuneRepository
import com.cynora.luna.core.data.repository.FortuneRepositoryImpl
import com.cynora.luna.core.data.repository.SmartPoolSelector
import com.cynora.luna.core.database.dao.FortuneDao
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        dataStore: UserPreferencesDataStore,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(auth, dataStore, firestore)
    }

    @Provides
    @Singleton
    fun provideFortuneRepository(
        fortuneDao: FortuneDao,
        firestore: FirebaseFirestore,
        smartPoolSelector: SmartPoolSelector,
        dataStore: UserPreferencesDataStore
    ): FortuneRepository {
        return FortuneRepositoryImpl(fortuneDao, firestore, smartPoolSelector, dataStore)
    }
}
