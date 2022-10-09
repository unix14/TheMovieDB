package com.triPcups.android.themoviedb.features.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.triPcups.android.themoviedb.R
import com.triPcups.android.themoviedb.common.Constants
import com.triPcups.android.themoviedb.features.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupViewModel()
        initAnimation()
    }

    private fun initAnimation() {
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
                    Toast.makeText(this, "Connection to server failed", Toast.LENGTH_LONG).show()
                }
                SplashViewModel.ErrorEvent.AUTH_FAILED_ERROR -> {
                    Toast.makeText(this, "Authentication with server failed", Toast.LENGTH_LONG).show()
                }
                SplashViewModel.ErrorEvent.FETCH_LANGUAGES_ERROR ->{
                    Toast.makeText(this, "Couldn't load languages from server", Toast.LENGTH_LONG).show()
                }
                SplashViewModel.ErrorEvent.FETCH_GENRES_ERROR -> {
                    Toast.makeText(this, "Couldn't load genres from server", Toast.LENGTH_LONG).show()
                }
                SplashViewModel.ErrorEvent.NO_ERROR -> {}
            }
        }
    }

    private fun handleLoading(isLoading: Boolean?) {
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
