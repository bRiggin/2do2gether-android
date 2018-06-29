package com.rbiggin.a2do2gether.dagger

import com.rbiggin.a2do2gether.firebase.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): IntFirebaseStorage {
        return FirebaseStorage
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): IntFirebaseDatabase {
        return FirebaseDatabase
    }
}