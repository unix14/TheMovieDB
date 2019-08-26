package com.unix14.android.themoviedb.features.sign_in

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import kotlinx.android.synthetic.main.sign_in_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.net.Uri
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants


class SignInFragment : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    private val viewModel by viewModel<SignInViewModel>()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sign_in_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        initUi()
    }

    private fun setupViewModel() {
        viewModel.navigationEvent.observe(this, Observer {
                navigationEvent -> handleNavigationEvent(navigationEvent) })
        viewModel.progressData.observe(this, Observer {
                isLoading -> handleProgressBar(isLoading) })

    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let {
            if (isLoading) {
                signInFragPb.visibility = View.GONE
            } else {
                signInFragPb.visibility = View.VISIBLE
            }
        }
    }

    private fun handleNavigationEvent(navigationEvent: SignInViewModel.NavigationEvent?) {
        navigationEvent?.let {
            when (it) {
                is SignInViewModel.NavigationEvent.NavigateToConfirmScreenEvent -> {
                    val requestToken = it.requestToken
                    forwardToAuthentication(requestToken)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        /*viewModel.*/checkIsAuthorized()
    }

    private fun checkIsAuthorized() {

        if(false){
            testing.text = "Conncted !!"

        }

    }

    private fun forwardToAuthentication(requestToken: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.AUTHENTICATION_URL + requestToken))
        startActivity(browserIntent)
    }

    private fun initUi() {
        signInFragLoginBtn.setOnClickListener {
            viewModel.generateSessionId()
        }
    }


}