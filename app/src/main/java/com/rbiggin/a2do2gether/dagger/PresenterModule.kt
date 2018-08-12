package com.rbiggin.a2do2gether.dagger


import android.content.SharedPreferences
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsPresenter
import com.rbiggin.a2do2gether.ui.login.LoginPresenter
import com.rbiggin.a2do2gether.ui.main.MainPresenter
import com.rbiggin.a2do2gether.ui.profile.MyProfilePresenter
import com.rbiggin.a2do2gether.ui.settings.ChecklistsPresenter
import com.rbiggin.a2do2gether.ui.settings.SettingsPresenter
import com.rbiggin.a2do2gether.ui.todo.ToDoListPresenter
import com.rbiggin.a2do2gether.utils.Utilities
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideLoginPresenter(repo: AuthRepository): LoginPresenter {
        return LoginPresenter(repo)
    }

    @Provides
    @Singleton
    fun provideMainPresenter(aRepo: AuthRepository,
                             uRepo: UserProfileRepository,
                             cRepo: ConnectionsRepository,
                             sharedPrefs: SharedPreferences,
                             utils: Utilities): MainPresenter {
        return MainPresenter(aRepo, uRepo, cRepo, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideMyProfilePresenter(utils: Utilities,
                                  sharedPrefs: SharedPreferences,
                                  uRepo: UserProfileRepository): MyProfilePresenter {
        return MyProfilePresenter(uRepo, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideSettingsPresenter(utils: Utilities,
                                 sharedPrefs: SharedPreferences,
                                 repo: AuthRepository): SettingsPresenter {
        return SettingsPresenter(repo, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideChecklistsPresenter(utils: Utilities,
                                   sharedPrefs: SharedPreferences): ChecklistsPresenter? {
        return ChecklistsPresenter(utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideToDoListPresenter(utils: Utilities,
                                 sharedPrefs: SharedPreferences):
                                 ToDoListPresenter {
        return ToDoListPresenter(utils,sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideMyConnectionsPresenter(utils: Utilities,
                                      sharedPrefs: SharedPreferences,
                                      cRepo: ConnectionsRepository,
                                      uRepo: UserProfileRepository):
                                      MyConnectionsPresenter {
        return MyConnectionsPresenter(cRepo, uRepo, utils, sharedPrefs)
    }
}