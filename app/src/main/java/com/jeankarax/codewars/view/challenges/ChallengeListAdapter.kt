package com.jeankarax.codewars.view.challenges

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel
import kotlinx.android.synthetic.main.item_challenge_list.view.*
import kotlinx.android.synthetic.main.item_loading_challenge.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChallengeListAdapter(
    private val challengeList: ChallengesListResponse,
    private val viewModel: ChallengesListsViewModel,
    parentFragment: ChallengesListsFragment
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mParentFragment = parentFragment
    private val auxChallengesList: MutableList<ChallengeResponse> = mutableListOf()
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_LAST_ITEM = 1
    private val VIEW_TYPE_ITEM = 2
    private val placeHolderChallenge = ChallengeResponse("placeholder")
    private val placeHolderLastChallenge = ChallengeResponse("lastItem")

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        auxChallengesList.addAll(challengeList.data!!)
        if(challengeList.type == "completed"){
            auxChallengesList.add(placeHolderChallenge)
        }else{
            auxChallengesList.add(placeHolderLastChallenge)
        }

    }

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

    override fun getItemCount() = auxChallengesList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ChallengeViewHolder){
            populateItems(holder, position)
        }else if(holder is LoadMoreViewHolder){
            EspressoIdlingResource.increment()
            viewModel.getNextPage().observeOnce(mParentFragment.viewLifecycleOwner, Observer { response ->
                when(response.status){
                    Status.SUCCESS -> {
                        Handler().postDelayed({
                            auxChallengesList.removeAt(auxChallengesList.size-1)
                            response.data?.data?.let { it ->
                                    auxChallengesList.addAll(
                                        it
                                    )
                                }
                            if (response.data?.pageNumber!! <= challengeList.totalPages!!){
                                auxChallengesList.add(placeHolderChallenge)
                            }else{
                                auxChallengesList.add(placeHolderLastChallenge)
                            }
                            notifyDataSetChanged()
                            EspressoIdlingResource.decrement()
                        }, 3000)
                    }
                    Status.ERROR -> {
                        Toast.makeText(mParentFragment.context, response.message, Toast.LENGTH_LONG).show()}
                }
            })
            holder.view.pb_load_more.visibility = VISIBLE
        }

    }

    private fun populateItems(holder: ChallengeViewHolder, position: Int) {
        holder.view.tv_item_challenge_title.text = auxChallengesList[position].name
        if (auxChallengesList[position].completedAt != null) {
            val formatter = DateTimeFormatter.ofPattern("MMMM dd - yyyy")
            holder.view.tv_item_challenge_date.text = mParentFragment.getString(
                R.string.label_challenge_completed_at, formatter.format(
                    LocalDateTime.ofInstant(auxChallengesList[position].completedAt?.toInstant(),
                        ZoneId.systemDefault())))
        } else {
            var tags = ""
            if (auxChallengesList[position].tags != null) {
                for (tag in auxChallengesList[position].tags!!) {
                    tags = "$tags #$tag"
                }
                holder.view.tv_item_challenge_date.text = tags
            }
        }

        holder.view.setOnClickListener {
            EspressoIdlingResource.increment()
            val action =
                ChallengesListsFragmentDirections.actionChallengesFragmentToChallengeFragment(
                    auxChallengesList[position].id
                )
            mParentFragment.view?.let { parentFragment ->
                Navigation.findNavController(
                    parentFragment
                ).navigate(action)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(auxChallengesList[position].id){
            "placeholder" -> VIEW_TYPE_LOADING
            "lastItem" -> VIEW_TYPE_LAST_ITEM
            else -> VIEW_TYPE_ITEM
        }
    }

    class ChallengeViewHolder(var view: View): RecyclerView.ViewHolder(view)

    class LoadMoreViewHolder(var view: View): RecyclerView.ViewHolder(view)

    class LastItemViewHolder(var view: View): RecyclerView.ViewHolder(view)

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
        observe(lifecycleOwner, object: Observer<T>{
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

}