package com.rbiggin.a2do2gether.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application){
    @Provides
    @Singleton
    fun provideContext(): Context = app
}
