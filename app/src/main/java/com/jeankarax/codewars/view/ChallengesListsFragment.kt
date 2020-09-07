package com.jeankarax.codewars.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.jeankarax.codewars.R
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel

class ChallengesListsFragment : Fragment() {

    private lateinit var viewModel: ChallengesListsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ChallengesListsViewModel::class.java)
        viewModel.getLists("g964")

    }

}