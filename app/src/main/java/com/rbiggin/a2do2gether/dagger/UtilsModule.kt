package com.rbiggin.a2do2gether.dagger

import android.content.Context
import android.content.SharedPreferences
import android.preference.Preference
import android.preference.PreferenceManager
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.repository.UidProvider
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
class UtilsModule {

    @Provides
    @Singleton
    fun provideUtilities(): Utilities {
        return Utilities()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideUidProvider(aRepo: AuthRepository): UidProvider {
        return UidProvider(aRepo)
    }

    @Provides
    @Named("main")
    fun provideMainThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @Named("computation")
    fun provideComputationThread(): Scheduler {
        return Schedulers.computation()
    }
}