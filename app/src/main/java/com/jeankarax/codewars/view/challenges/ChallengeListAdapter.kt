package com.jeankarax.codewars.view.challenges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.ChallengeResponse
import kotlinx.android.synthetic.main.item_challenge_list.view.*

class ChallengeListAdapter(
   private val challengeList: MutableList<ChallengeResponse>
):
    RecyclerView.Adapter<ChallengeListAdapter.ChallengeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_challenge_list, parent, false)
        return ChallengeViewHolder(view)
    }

    override fun getItemCount() = challengeList.size

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.view.tv_challenge_title.text = challengeList[position].name
        if (challengeList[position].completedAt != null) {
            holder.view.tv_challenge_date.text = challengeList[position].completedAt.toString()
        } else {
            var tags = ""
            if (challengeList[position].tags != null) {
                for (tag in challengeList[position].tags!!) {
                    tags = "$tags #$tag"
                }
                holder.view.tv_challenge_date.text = tags
            }
        }
    }

    class ChallengeViewHolder(var view: View): RecyclerView.ViewHolder(view)

}