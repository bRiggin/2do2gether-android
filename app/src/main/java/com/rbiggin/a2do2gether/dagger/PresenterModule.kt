package com.rbiggin.a2do2gether.dagger


import com.rbiggin.a2do2gether.firebase.FirebaseStorage
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
    fun provideMyProfilePresenter(aRepo: AuthRepository, uRepo: UserProfileRepository,
                                  constants: Constants): IntMyProfilePresenter {
        return MyProfilePresenter(aRepo, uRepo, constants)
    }

    @Provides
    @Singleton
    fun provideSettingsPresenter(repo: AuthRepository, constants: Constants): IntSettingsPresenter {
        return SettingPresenter(repo, constants)
    }

    @Provides
    @Nullable
    @Singleton
    fun provideChecklistsPresenter(constants: Constants): IntChecklistsPresenter? {
        return ChecklistsPresenter(constants)
    }

    @Provides
    @Singleton
    fun provideToDoListPresenter(constants: Constants): IntToDoListPresenter {
        return ToDoListPresenter(constants)
    }

    @Provides
    @Singleton
    fun provideMyConnectionsPresenter(constants: Constants, cRepo: IntConnectionsRepository,
                                      aRepo: AuthRepository, uRepo: UserProfileRepository): IntMyConnectionsPresenter {
        return MyConnectionsPresenter(constants, cRepo, uRepo, aRepo)
    }
}