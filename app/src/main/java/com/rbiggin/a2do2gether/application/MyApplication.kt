package com.rbiggin.a2do2gether.application

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.rbiggin.a2do2gether.dagger.AppComponent
import com.rbiggin.a2do2gether.dagger.DaggerAppComponent

/**
 * Instance of 2Do2gether Application
 */
class MyApplication : Application() {

    lateinit var daggerComponent: AppComponent

    /**
     * onCreate
     */
    override fun onCreate() {
        super.onCreate()
        daggerComponent = initDagger(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    /**
     * Initialise Dagger
     */
    private fun initDagger(app: MyApplication): AppComponent{
        return DaggerAppComponent.builder().appModule(AppModule(app)).build()
    }
}