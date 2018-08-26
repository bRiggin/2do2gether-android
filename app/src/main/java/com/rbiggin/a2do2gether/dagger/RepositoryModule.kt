package com.rbiggin.a2do2gether.dagger

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.firebase.*
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.utils.Utilities
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(fbAuth: FirebaseAuth, sharedPrefs: SharedPreferences,
                              api: IntFirebaseDatabase, utils: Utilities): AuthRepository {
        return AuthRepository(fbAuth, sharedPrefs, api, utils)
    }

    @Provides
    @Singleton
    fun provideUserRepository(fbDatabase: IntFirebaseDatabase, fbStorage: IntFirebaseStorage,
                              utilities: Utilities, uidProvider: UidProvider): UserProfileRepository {
        return UserProfileRepository(fbDatabase, fbStorage, utilities, uidProvider)
    }

    @Provides
    @Singleton
    fun provideConnectionsRepository(fbDatabase: IntFirebaseDatabase, uidProvider: UidProvider)
            : ConnectionsRepository {
        return ConnectionsRepository(fbDatabase, uidProvider)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(utilities: Utilities, uidProvider: UidProvider, writer: FirebaseDatabaseWriter)
            : SettingsRepository {
        return SettingsRepository(uidProvider, writer, utilities)
    }
}