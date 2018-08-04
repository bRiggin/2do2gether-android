package com.rbiggin.a2do2gether.dagger


import android.content.SharedPreferences
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.connections.IntMyConnectionsPresenter
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsPresenter
import com.rbiggin.a2do2gether.ui.login.IntLoginPresenter
import com.rbiggin.a2do2gether.ui.login.LoginPresenter
import com.rbiggin.a2do2gether.ui.main.IntMainPresenter
import com.rbiggin.a2do2gether.ui.main.MainPresenter
import com.rbiggin.a2do2gether.ui.profile.IntMyProfilePresenter
import com.rbiggin.a2do2gether.ui.profile.MyProfilePresenter
import com.rbiggin.a2do2gether.ui.settings.ChecklistsPresenter
import com.rbiggin.a2do2gether.ui.settings.IntChecklistsPresenter
import com.rbiggin.a2do2gether.ui.settings.IntSettingsPresenter
import com.rbiggin.a2do2gether.ui.settings.SettingPresenter
import com.rbiggin.a2do2gether.ui.todo.IntToDoListPresenter
import com.rbiggin.a2do2gether.ui.todo.ToDoListPresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Insert class/object/interface/file description...
 */
@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideLoginPresenter(repo: AuthRepository): IntLoginPresenter {
        return LoginPresenter(repo)
    }

    @Provides
    @Singleton
    fun provideMainPresenter(aRepo: AuthRepository,
                             uRepo: UserProfileRepository,
                             cRepo: ConnectionsRepository): IntMainPresenter {
        return MainPresenter(aRepo, uRepo, cRepo)
    }

    @Provides
    @Singleton
    fun provideMyProfilePresenter(utils: Utilities,
                                  sharedPrefs: SharedPreferences,
                                  uRepo: UserProfileRepository): IntMyProfilePresenter {
        return MyProfilePresenter(uRepo, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideSettingsPresenter(utils: Utilities,
                                 sharedPrefs: SharedPreferences,
                                 repo: AuthRepository): IntSettingsPresenter {
        return SettingPresenter(repo, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideChecklistsPresenter(utils: Utilities,
                                   sharedPrefs: SharedPreferences): IntChecklistsPresenter? {
        return ChecklistsPresenter(utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideToDoListPresenter(utils: Utilities,
                                 sharedPrefs: SharedPreferences):
                                 IntToDoListPresenter {
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