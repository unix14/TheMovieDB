package com.triPcups.android.themoviedb.features.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.triPcups.android.themoviedb.R
import com.triPcups.android.themoviedb.common.Constants
import com.triPcups.android.themoviedb.databinding.ActivitySplashBinding
import com.triPcups.android.themoviedb.features.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        setupViewModel()
        initAnimation()
    }

    private fun initAnimation()= with(binding) {
        splashActLoadingLayout.animate()
            .alpha(1f)
            .setDuration(Constants.DEFAULT_ALPHA_DURATION_IN_MS)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    viewModel.startSplashActivity()
                }
            }).start()
    }

    private fun setupViewModel() {
        viewModel.navigationEvent.observe(this, Observer {
                navigationEvent -> handleNavigationEvent(navigationEvent) })
        viewModel.progressData.observe(this, Observer {
                isLoading -> handleLoading(isLoading) })
        viewModel.errorEvent.observe(this, Observer {
                errorEvent -> handleErrorEvent(errorEvent) })
    }

    private fun handleErrorEvent(errorEvent: SplashViewModel.ErrorEvent?) {
        errorEvent?.let {
            when (it) {
                SplashViewModel.ErrorEvent.CONNECTION_FAILED_ERROR -> {
                    Toast.makeText(this, getString(R.string.error_connection_failed), Toast.LENGTH_LONG).show()
                }
//                SplashViewModel.ErrorEvent.AUTH_FAILED_ERROR -> {
//                    Toast.makeText(this, getString(R.string.error_auth_fail), Toast.LENGTH_LONG).show()
//                }
//                SplashViewModel.ErrorEvent.FETCH_LANGUAGES_ERROR ->{
//                    Toast.makeText(this, getString(R.string.error_fetch_langs_fail), Toast.LENGTH_LONG).show()
//                }
//                SplashViewModel.ErrorEvent.FETCH_GENRES_ERROR -> {
//                    Toast.makeText(this, getString(R.string.error_fetch_genres_fail), Toast.LENGTH_LONG).show()
//                }
                SplashViewModel.ErrorEvent.NO_ERROR -> {}
            }
        }
    }

    private fun handleLoading(isLoading: Boolean?) = with(binding){
        isLoading?.let {
            if (it) {
                splashActPb.visibility = View.VISIBLE
            } else {
                splashActPb.visibility = View.GONE
            }
        }
    }

    private fun handleNavigationEvent(navigationEvent: SplashViewModel.NavigationEvent?) {
        navigationEvent?.let {
            when (it) {
                SplashViewModel.NavigationEvent.GO_TO_MAIN_ACTIVITY -> {
                    showMainActivity()
                }
            }
        }
    }

    private fun showMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
