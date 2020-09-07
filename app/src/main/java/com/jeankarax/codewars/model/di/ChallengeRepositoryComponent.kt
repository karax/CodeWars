package com.jeankarax.codewars.model.di

import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel
import dagger.Component

@Component(modules = [ChallengeRepositoryModule::class])
interface ChallengeRepositoryComponent {
    fun injectInChallengeListsViewModel(challengesListsViewModel: ChallengesListsViewModel)
}