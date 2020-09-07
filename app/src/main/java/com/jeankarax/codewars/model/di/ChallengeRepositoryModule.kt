package com.jeankarax.codewars.model.di

import com.jeankarax.codewars.model.api.ChallengeAPI
import com.jeankarax.codewars.model.repository.ChallengeRepository
import com.jeankarax.codewars.model.repository.IChallengeRepository
import dagger.Module
import dagger.Provides

@Module(includes = [APICallsModule::class])
class ChallengeRepositoryModule {
    @Provides
    open fun providesChallengeRepository(challengeAPI: ChallengeAPI): IChallengeRepository{
        return ChallengeRepository(challengeAPI)
    }
}