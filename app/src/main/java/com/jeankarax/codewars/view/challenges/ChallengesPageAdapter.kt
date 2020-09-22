package com.jeankarax.codewars.view.challenges

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel

class ChallengesPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                            viewModel: ChallengesListsViewModel, lists: List<ChallengesListResponse>?)
    :FragmentStateAdapter(fragmentManager, lifecycle) {

    private var mViewModel: ChallengesListsViewModel = viewModel
    private var mChallengeLists = lists

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return CompletedChallengesFragment(
                mViewModel, mChallengeLists!![0]
            )
            1 -> return AuthoredChallengesFragment(
                mViewModel, mChallengeLists!![1]
            )
        }
        return BlankFragment()
    }

    override fun getItemCount() = 2
}