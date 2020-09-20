package com.jeankarax.codewars.view.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.viewmodel.UserListViewModel
import kotlinx.android.synthetic.main.fragment_users.*

class UserListFragment : Fragment() {

    private var isOrderedByRank: Boolean = false
    private lateinit var viewModel: UserListViewModel
    private lateinit var userListAdapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buildRecyclerView()
        viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        setObservers()
        setOnClickListeners()
        viewModel.getUsersList()
    }

    override fun onResume() {
        super.onResume()
        isOrderedByRank = false
        tv_users_ordered_by.text = getString(R.string.text_users_ordered_by_search)
    }

    private fun buildRecyclerView() {
        userListAdapter = UserListAdapter(arrayListOf(), this)
        rv_users_list.layoutManager = LinearLayoutManager(context)
        rv_users_list.adapter = userListAdapter
        rv_users_list.addItemDecoration(
            DividerItemDecoration(
                context,
                (rv_users_list.layoutManager as LinearLayoutManager).orientation
            )
        )
    }

    private fun setOnClickListeners() {
        ib_search.setOnClickListener {
                getUser()
        }
        mt_toolbar.setOnMenuItemClickListener { itemClicked ->
            return@setOnMenuItemClickListener when (itemClicked.itemId) {
                R.id.menu_sort_user_list -> {
                    showSortMenu()
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
    }

    private fun setObservers() {

        viewModel.userListLiveData.observe(viewLifecycleOwner, Observer {
                rv_users_list.apply {
                    userListAdapter.updateUserList(it)
                }
        })
    }

    private fun showSortMenu() {

        val options = arrayOf(getString(R.string.title_order_by_rank))
        val checkedItems = BooleanArray(1) { isOrderedByRank }

        AlertDialog.Builder(requireContext())
            .setNeutralButton(getString(R.string.text_cancel), null)
            .setMultiChoiceItems(options, checkedItems) { dialog, which, isChecked ->
                if (which == 0) {
                    if (isChecked){
                        isOrderedByRank = true
                        viewModel.getSortedUserList()
                        tv_users_ordered_by.text = getString(R.string.text_users_ordered_by_rank)
                    }else{
                        isOrderedByRank = false
                        viewModel.getUnsortedUserList()
                        tv_users_ordered_by.text = getString(R.string.text_users_ordered_by_search)
                    }
                }
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun getUser() {
        EspressoIdlingResource.increment()
        if (et_search_user_name.text.isNullOrBlank()) {
            tv_empty_user_error.visibility = VISIBLE

        } else {
            tv_empty_user_error.visibility = GONE
            viewModel.getUser(et_search_user_name.text.toString())
            viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
                when(it.status){
                    Status.LOADING -> {
                        progressBar.visibility = VISIBLE
                    }
                    Status.SUCCESS -> {
                        progressBar.visibility = GONE
                        goToUserChallengesList()
                    }
                    Status.ERROR -> {
                        progressBar.visibility = GONE
                        Toast.makeText(context, it.message, LENGTH_LONG).show()
                    }
                }
            })
        }
    }

    private fun goToUserChallengesList() {
        val action =
            UserListFragmentDirections.actionGoToChallenges(et_search_user_name.text.toString())
        view?.let {
            parentFragment?.view?.let { parentFragment ->
                Navigation.findNavController(parentFragment).navigate(action)
            }
        }
        EspressoIdlingResource.decrement()
    }
}