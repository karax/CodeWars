package com.jeankarax.codewars.model.di

import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.repository.IUserRepository
import com.jeankarax.codewars.model.repository.UserRepository
import dagger.Module
import dagger.Provides

@Module(includes = [APICallsModule::class])
open class UserRepositoryModule {
    @Provides
    open fun providesUserRepository(userAPI: UserAPI): IUserRepository {
        return UserRepository(userAPI)
    }
}