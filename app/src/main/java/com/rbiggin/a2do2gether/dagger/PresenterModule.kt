package com.rbiggin.a2do2gether.dagger

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
import io.reactivex.Scheduler
import javax.inject.Named
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
                             cRepo: ConnectionsRepository): MainPresenter {
        return MainPresenter(aRepo, uRepo, cRepo)
    }

    @Provides
    @Singleton
    fun provideMyProfilePresenter(uRepo: UserProfileRepository, uidProvider: UidProvider): MyProfilePresenter {
        return MyProfilePresenter(uRepo, uidProvider)
    }

    @Provides
    @Singleton
    fun provideSettingsPresenter(repo: AuthRepository): SettingsPresenter {
        return SettingsPresenter(repo)
    }

    @Provides
    @Singleton
    fun provideChecklistsPresenter(): ChecklistsPresenter {
        return ChecklistsPresenter()
    }

    @Provides
    @Singleton
    fun provideToDoListPresenter() : ToDoListPresenter {
        return ToDoListPresenter()
    }

    @Provides
    @Singleton
    fun provideMyConnectionsPresenter(utils: Utilities,
                                      cRepo: ConnectionsRepository,
                                      uRepo: UserProfileRepository,
                                      @Named("main") thread: Scheduler):
                                      MyConnectionsPresenter {
        return MyConnectionsPresenter(cRepo, uRepo, utils, thread)
    }
}