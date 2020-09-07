package com.jeankarax.codewars.model.di

import android.app.Application
import com.jeankarax.codewars.viewmodel.di.AppModule
import dagger.Component

@Component(modules = [AppModule::class, UserRepositoryModule::class])
interface UserRepositoryComponent {
    fun inject(application: Application)
}