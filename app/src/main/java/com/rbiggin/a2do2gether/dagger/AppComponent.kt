package com.rbiggin.a2do2gether.dagger

import com.rbiggin.a2do2gether.application.AppModule
import com.rbiggin.a2do2gether.notification.MessagingService
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsFragment
import com.rbiggin.a2do2gether.ui.login.LoginActivity
import com.rbiggin.a2do2gether.ui.login.fragments.MasterFragment
import com.rbiggin.a2do2gether.ui.main.MainActivity
import com.rbiggin.a2do2gether.ui.profile.MyProfileFragment
import com.rbiggin.a2do2gether.ui.settings.SettingsFragment
import com.rbiggin.a2do2gether.ui.todo.ToDoListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,
                      PresenterModule::class,
                      RepositoryModule::class,
                      FirebaseModule::class,
                      UtilsModule::class])
interface AppComponent {
    fun inject(target: LoginActivity){}

    fun inject(target: MainActivity){}

    fun inject(target: MasterFragment){}

    fun inject(target: BaseFragment){}

    fun inject(target: MyProfileFragment){}

    fun inject(target: SettingsFragment){}

    fun inject(target: MyConnectionsFragment){}

    fun inject(target: ToDoListFragment){}

    fun inject(target: ChecklistsFragment){}

    fun inject(target: MessagingService){}
}
