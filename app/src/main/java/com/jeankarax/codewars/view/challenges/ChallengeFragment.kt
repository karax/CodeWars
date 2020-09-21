package com.jeankarax.codewars.view.challenges

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.Status
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.viewmodel.ChallengeViewModel
import kotlinx.android.synthetic.main.fragment_challenge.*

class ChallengeFragment : Fragment() {

    lateinit var challenge: ChallengeResponse
    private lateinit var viewModel: ChallengeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ChallengeViewModel::class.java)

        arguments?.let{
            viewModel.challengeLiveData.observe(viewLifecycleOwner, Observer { response ->
                when(response.status){
                    Status.SUCCESS -> {
                        pb_challenge_loading.visibility = GONE
                        challenge = response.data!!
                        bindComponents()
                    }
                    Status.ERROR -> {
                        pb_challenge_loading.visibility = GONE
                        Toast.makeText(context, "Error", LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        pb_challenge_loading.visibility = VISIBLE
                    }
                }
            })
            viewModel.getChallenge(ChallengeFragmentArgs.fromBundle(it).challengeId)
        }


    }

    private fun bindComponents() {
        tv_challenge_title.text = challenge.name
        tv_challenge_created_by.text = getString(R.string.label_challenge_created_by, challenge.createdBy?.username)
        tv_challenge_category.text = getString(R.string.label_challenge_category, challenge.category)
        if(challenge.approvedBy?.username != null){
            tv_challenge_approved_by.visibility = VISIBLE
            tv_challenge_approved_by.text = getString(R.string.label_challenge_approve_by, challenge.approvedBy?.username)
        }
        tv_challenge_description.text = Html.fromHtml(challenge.description)
        if(challenge.languages != null){
            var languages = ""
            for(language in challenge.languages!!){
                languages = "$languages $language "
            }
            tv_challenge_languages.text = getString(R.string.label_challenge_languages, languages)
        }
        if (challenge.tags !=null){
            var tags = ""
            for(tag in challenge.tags!!){
                tags = "$tags #$tag "
            }
            tv_challenge_tags.text = getString(R.string.label_challenge_tags, tags)
        }
        EspressoIdlingResource.decrement()
    }
}