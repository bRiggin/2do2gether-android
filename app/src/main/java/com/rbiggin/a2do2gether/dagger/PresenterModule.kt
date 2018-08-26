package com.rbiggin.a2do2gether.dagger

import com.rbiggin.a2do2gether.notification.MessagingPresenter
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
    fun provideMainPresenter(aRepo: AuthRepository,
                             uRepo: UserProfileRepository,
                             cRepo: ConnectionsRepository,
                             sRepo: SettingsRepository): MainPresenter {
        return MainPresenter(aRepo, uRepo, cRepo, sRepo)
    }

    @Provides
    @Singleton
    fun provideMyProfilePresenter(uRepo: UserProfileRepository, uidProvider: UidProvider): MyProfilePresenter {
        return MyProfilePresenter(uRepo, uidProvider)
    }

    @Provides
    @Singleton
    fun provideSettingsPresenter(aRepo: AuthRepository,
                                 sRepo: SettingsRepository,
                                 @Named("main") mThread: Scheduler,
                                 @Named("computation") cThread: Scheduler): SettingsPresenter {
        return SettingsPresenter(aRepo, sRepo, mThread, cThread)
    }

    @Provides
    @Singleton
    fun provideChecklistsPresenter(): ChecklistsPresenter {
        return ChecklistsPresenter()
    }

    @Provides
    @Singleton
    fun provideToDoListPresenter(): ToDoListPresenter {
        return ToDoListPresenter()
    }

    @Provides
    @Singleton
    fun provideMyConnectionsPresenter(utils: Utilities,
                                      cRepo: ConnectionsRepository,
                                      uRepo: UserProfileRepository,
                                      sRepo: SettingsRepository,
                                      @Named("main") thread: Scheduler):
                                      MyConnectionsPresenter {
        return MyConnectionsPresenter(cRepo, uRepo, sRepo, utils, thread)
    }

    @Provides
    fun provideMessagingPresenter(aRepo: AuthRepository,
                                  sRepo: SettingsRepository):
                                  MessagingPresenter {
        return MessagingPresenter(aRepo, sRepo)
    }
}