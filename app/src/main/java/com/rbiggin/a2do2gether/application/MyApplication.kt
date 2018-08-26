package com.rbiggin.a2do2gether.application

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.rbiggin.a2do2gether.BuildConfig
import com.rbiggin.a2do2gether.dagger.AppComponent
import com.rbiggin.a2do2gether.dagger.DaggerAppComponent
import timber.log.Timber.DebugTree
import timber.log.Timber

class MyApplication : Application() {

    lateinit var daggerComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        daggerComponent = initDagger(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initDagger(app: MyApplication): AppComponent{
        return DaggerAppComponent.builder().appModule(AppModule(app)).build()
    }
}