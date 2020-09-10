package com.jeankarax.codewars.view.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.jeankarax.codewars.R
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.view.Constants
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel
import kotlinx.android.synthetic.main.fragment_challenges.*

class ChallengesListsFragment : Fragment() {

    private lateinit var viewModel: ChallengesListsViewModel
    private lateinit var challengesPageAdapter: ChallengesPageAdapter
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EspressoIdlingResource.increment()
        return inflater.inflate(R.layout.fragment_challenges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ChallengesListsViewModel::class.java)
        setObservers()
        arguments?.let {
            userName = ChallengesListsFragmentArgs.fromBundle(it).userName
        }
        viewModel.getLists(userName)
        mt_challenges_toolbar.title = getString(R.string.title_challenges_toolbar, userName)
        setNavBottomBarListeners()

    }

    private fun setObservers() {
        viewModel.areListsOk.observe(viewLifecycleOwner, Observer {
            challengesPageAdapter = ChallengesPageAdapter(this.childFragmentManager, lifecycle, viewModel)
            vp_lists.adapter = challengesPageAdapter
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                vp_lists.visibility = GONE
                pb_challenges_call.visibility = VISIBLE
            }else{
                vp_lists.visibility = VISIBLE
                pb_challenges_call.visibility = GONE
            }
        })

        viewModel.isError.observe(viewLifecycleOwner, Observer {
            if(it == Constants.PAGE_NOT_FOUND_ERROR){
                vp_lists.visibility = GONE
                tv_error_challenges_not_found.visibility = VISIBLE
                bottom_toolbar.visibility = GONE
            }
        })

    }

    private fun setNavBottomBarListeners() {
        bottom_toolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_completed -> {
                    vp_lists.currentItem = vp_lists.currentItem - 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_authored -> {
                    vp_lists.currentItem = vp_lists.currentItem + 1
                    return@setOnNavigationItemSelectedListener true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }

        vp_lists.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottom_toolbar.menu.getItem(position).isChecked = true
            }
        })
    }

}