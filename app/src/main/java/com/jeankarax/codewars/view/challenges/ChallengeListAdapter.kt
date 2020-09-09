package com.jeankarax.codewars.view.challenges

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel
import kotlinx.android.synthetic.main.item_challenge_list.view.*
import kotlinx.android.synthetic.main.item_loading_challenge.view.*

class ChallengeListAdapter(
    private val challengeList: List<ChallengeResponse>,
    private val viewModel: ChallengesListsViewModel,
    parentFragment: ChallengesListsFragment
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mParentFragment = parentFragment
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_LAST_ITEM = 1
    private val VIEW_TYPE_ITEM = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.item_challenge_list, parent, false)
                ChallengeViewHolder(view)
            }
            VIEW_TYPE_LAST_ITEM -> {
                val view = inflater.inflate(R.layout.item_no_more_challenges, parent, false)
                LastItemViewHolder(view)
            }else -> {
                val view = inflater.inflate(R.layout.item_loading_challenge, parent, false)
                LoadMoreViewHolder(view)
            }
        }

    }

    override fun getItemCount() = challengeList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ChallengeViewHolder){
            populateItems(holder, position)
        }else if(holder is LoadMoreViewHolder){
            viewModel.getNextPage()
            holder.view.pb_load_more.visibility = VISIBLE
            viewModel.isNextPageLoadedLiveData.observe(mParentFragment.viewLifecycleOwner, Observer {
                Handler().postDelayed({
                    notifyDataSetChanged()
                }, 3000)
            })
        }

    }

    private fun populateItems(holder: ChallengeViewHolder, position: Int) {
        holder.view.tv_item_challenge_title.text = challengeList[position].name
        if (challengeList[position].completedAt != null) {
            holder.view.tv_item_challenge_date.text = challengeList[position].completedAt.toString()
        } else {
            var tags = ""
            if (challengeList[position].tags != null) {
                for (tag in challengeList[position].tags!!) {
                    tags = "$tags #$tag"
                }
                holder.view.tv_item_challenge_date.text = tags
            }
        }

        holder.view.setOnClickListener {
            val action =
                ChallengesListsFragmentDirections.actionChallengesFragmentToChallengeFragment(
                    challengeList[position].id
                )
            mParentFragment.view?.let { parentFragment ->
                Navigation.findNavController(
                    parentFragment
                ).navigate(action)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(challengeList[position].id){
            "placeholder" -> VIEW_TYPE_LOADING
            "lastItem" -> VIEW_TYPE_LAST_ITEM
            else -> VIEW_TYPE_ITEM
        }
    }

    class ChallengeViewHolder(var view: View): RecyclerView.ViewHolder(view)

    class LoadMoreViewHolder(var view: View): RecyclerView.ViewHolder(view)

    class LastItemViewHolder(var view: View): RecyclerView.ViewHolder(view)

}