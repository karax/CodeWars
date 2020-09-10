package com.jeankarax.codewars.view.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeankarax.codewars.R
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
                if(et_search_user_name.text.isNullOrBlank()){
                    tv_empty_user_error.visibility = VISIBLE

                }else{
                    tv_empty_user_error.visibility = GONE
                    viewModel.getUser(et_search_user_name.text.toString())
                    viewModel.userLiveData.observeOnce(viewLifecycleOwner, Observer {
                        val action =
                            UserListFragmentDirections.actionGoToChallenges(et_search_user_name.text.toString())
                        view?.let {
                            parentFragment?.view?.let { parentFragment ->
                                Navigation.findNavController(parentFragment).navigate(action)
                            }
                        }
                    })
                    viewModel.errorLiveData.observeOnce(viewLifecycleOwner, Observer {
                        Toast.makeText(this.context, "User not found", Toast.LENGTH_LONG).show()
                    })
            }
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

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (isLoading) {
                    rv_users_list.visibility = GONE
                    progressBar.visibility = VISIBLE
                } else {
                    rv_users_list.visibility = VISIBLE
                    progressBar.visibility = GONE
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
                    }else{
                        isOrderedByRank = false
                        viewModel.getUnsortedUserList()
                    }
                }
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
        observe(lifecycleOwner, object: Observer<T>{
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}