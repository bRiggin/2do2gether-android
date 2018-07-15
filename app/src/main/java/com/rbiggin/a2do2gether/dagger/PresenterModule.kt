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
import org.jetbrains.annotations.Nullable
import javax.inject.Singleton

/**
 * Insert class/object/interface/file description...
 */
@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideLoginPresenter(repo: AuthRepository, constants: Constants): IntLoginPresenter {
        return LoginPresenter(repo, constants)
    }

    @Provides
    @Singleton
    fun provideMainPresenter(constants: Constants, aRepo: AuthRepository, uRepo: UserProfileRepository): IntMainPresenter {
        return MainPresenter(constants, aRepo, uRepo)
    }

    @Provides
    @Singleton
    fun provideMyProfilePresenter(utils: Utilities, sharedPrefs: SharedPreferences,
                                  uRepo: UserProfileRepository, constants: Constants): IntMyProfilePresenter {
        return MyProfilePresenter(uRepo, constants, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideSettingsPresenter(utils: Utilities, sharedPrefs: SharedPreferences, repo: AuthRepository,
                                 constants: Constants): IntSettingsPresenter {
        return SettingPresenter(repo, constants, utils, sharedPrefs)
    }

    @Provides
    @Nullable
    @Singleton
    fun provideChecklistsPresenter(utils: Utilities, sharedPrefs: SharedPreferences, constants: Constants): IntChecklistsPresenter? {
        return ChecklistsPresenter(constants, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideToDoListPresenter(constants: Constants, utils: Utilities, sharedPrefs: SharedPreferences):
                                IntToDoListPresenter {
        return ToDoListPresenter(constants, utils, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideMyConnectionsPresenter(constants: Constants, utils: Utilities, sharedPrefs: SharedPreferences,
                                      cRepo: IntConnectionsRepository, uRepo: UserProfileRepository):
                                      IntMyConnectionsPresenter {
        return MyConnectionsPresenter(constants, cRepo, uRepo, utils, sharedPrefs)
    }
}