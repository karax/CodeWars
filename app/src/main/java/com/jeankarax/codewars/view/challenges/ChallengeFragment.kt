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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jeankarax.codewars.R
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.view.Constants
import com.jeankarax.codewars.viewmodel.ChallengeViewModel
import com.jeankarax.codewars.viewmodel.ChallengesListsViewModel
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenges.*

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

        setObservers()

        arguments?.let{
            viewModel.getChallenge(ChallengeFragmentArgs.fromBundle(it).challengeId)
        }


    }

    private fun setObservers() {
        viewModel.challengeLiveData.observe(viewLifecycleOwner, Observer {
            challenge = it
            bindComponents()
        })
        viewModel.isError.observe(viewLifecycleOwner, Observer {
            sv_challenge_details.visibility = GONE
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it){
                sv_challenge_details.visibility = GONE
                pb_challenge_loading.visibility = VISIBLE
            }else{
                sv_challenge_details.visibility = VISIBLE
                pb_challenge_loading.visibility = GONE
            }
        })
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

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
        observe(lifecycleOwner, object: Observer<T>{
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}