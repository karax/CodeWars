package com.jeankarax.codewars.model.di

import com.jeankarax.codewars.viewmodel.UserListViewModel
import com.jeankarax.codewars.viewmodel.di.AppModule
import dagger.Component

@Component(modules = [AppModule::class, UserRepositoryModule::class])
interface UserRepositoryComponent {
    fun inject(userListViewModel: UserListViewModel)
}