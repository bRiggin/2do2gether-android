package com.rbiggin.a2do2gether.dagger

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.firebase.*
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Insert class/object/interface/file description...
 */
@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(fbAuth: FirebaseAuth, sharedPrefs: SharedPreferences,
                              api: IntFirebaseDatabase, utils: Utilities, constants: Constants): IntAuthRepository {
        return AuthRepository(fbAuth, sharedPrefs, api, utils, constants)
    }

    @Provides
    @Singleton
    fun provideUserRepository(fbDatabase: IntFirebaseDatabase, fbStorage: IntFirebaseStorage,
                              constants: Constants, utilities: Utilities): UserProfileRepository {
        return UserProfileRepository(fbDatabase, fbStorage, constants, utilities)
    }

    @Provides
    @Singleton
    fun provideConnectionsRepository(fbDatabase: IntFirebaseDatabase,
                                     constants: Constants): IntConnectionsRepository {
        return ConnectionsRepository(fbDatabase, constants)
    }
}