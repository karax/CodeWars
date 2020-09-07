package com.jeankarax.codewars.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.UserResponse
import kotlinx.android.synthetic.main.item_user.view.*

class UserListAdapter(
    private val userList: ArrayList<UserResponse>
):
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    fun updateUserList(newUserList: List<UserResponse>){
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.view.setOnClickListener {
            val action = UserListFragmentDirections.actionGoToChallenges()
            Navigation.findNavController(it).navigate(action)
        }
        if(userList[position].name.isNullOrBlank()){
            holder.view.tv_name.text = userList[position].username
        }else{
            holder.view.tv_name.text = userList[position].name
        }
        holder.view.tv_user_name.text = userList[position].username
        holder.view.tv_rank.text = userList[position].ranks?.overall?.name
        holder.view.tv_color.text = userList[position].ranks?.overall?.color
    }

    class UserViewHolder(var view: View): RecyclerView.ViewHolder(view)

}