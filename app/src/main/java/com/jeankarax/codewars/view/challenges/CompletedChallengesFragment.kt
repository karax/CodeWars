package com.jeankarax.codewars.view.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.ChallengesListResponse
import kotlinx.android.synthetic.main.fragment_completed_challenges_list.*

class CompletedChallengesFragment(loadedCompletedList: ChallengesListResponse) : Fragment() {

    private val mLoadedCompletedList = loadedCompletedList

    private lateinit var challengeListAdapter: ChallengeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_completed_challenges_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildRecyclerView()
    }

    private fun buildRecyclerView() {
        val challengesList = mLoadedCompletedList.data as MutableList
        challengeListAdapter = ChallengeListAdapter(challengesList)
        rv_completed_challenges_list.layoutManager = LinearLayoutManager(context)
        rv_completed_challenges_list.adapter = challengeListAdapter
        rv_completed_challenges_list.addItemDecoration(
            DividerItemDecoration(
                context,
                (rv_completed_challenges_list.layoutManager as LinearLayoutManager).orientation
            )
        )
    }

}
