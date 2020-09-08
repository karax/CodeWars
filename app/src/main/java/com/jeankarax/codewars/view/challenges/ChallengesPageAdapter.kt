package com.jeankarax.codewars.view.challenges

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel

class ChallengesPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, viewModel: ChallengesListsViewModel) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var mViewModel: ChallengesListsViewModel = viewModel

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return CompletedChallengesFragment(
                mViewModel.getLoadedCompletedList()
            )
            1 -> return AuthoredChallengesFragment(
                mViewModel.getLoadedAuthoredList()
            )
        }
        return ErrorFragment()
    }

    override fun getItemCount() = 2
}