package com.jeankarax.codewars.model.di

import com.jeankarax.codewars.viewmodel.UserListViewModel
import dagger.Component

@Component(modules = [UserRepositoryModule::class])
interface UserRepositoryComponent {
    fun inject(userListViewModel: UserListViewModel)
}