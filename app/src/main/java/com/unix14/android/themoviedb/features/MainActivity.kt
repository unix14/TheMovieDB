package com.unix14.android.themoviedb.features

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.features.movie_list.MovieListFragment
import com.unix14.android.themoviedb.features.sign_in.SignInFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        initUi()
    }

    private fun setupViewModel() {
        viewModel.progressData.observe(this, Observer {
            isLoading -> handleProgressBar(isLoading) })
        viewModel.navigationEvent.observe(this, Observer {
            navigationEvent -> handleNavigationEvent(navigationEvent) })
        viewModel.errorEvent.observe(this, Observer {
            errorEvent -> handleErrorEvent(errorEvent)
        })
    }

    private fun handleErrorEvent(errorEvent: MainViewModel.ErrorEvent?) {
        errorEvent?.let{
            when(it){
                MainViewModel.ErrorEvent.CONNECTION_FAILED_ERROR ->{
                    Toast.makeText(this,"Connection to server failed",Toast.LENGTH_LONG).show()
                }
                MainViewModel.ErrorEvent.AUTH_FAILED_ERROR -> {
                    Toast.makeText(this,"Authentication with server failed",Toast.LENGTH_LONG).show()
                }
                MainViewModel.ErrorEvent.NO_ERROR ->{}
            }
        }
    }

    private fun handleNavigationEvent(navigationEvent: MainViewModel.NavigationEvent?) {
        navigationEvent?.let{
            when(navigationEvent){
                MainViewModel.NavigationEvent.SHOW_MOVIE_LIST_SCREEN -> {
                    showMovieList()
                }
                MainViewModel.NavigationEvent.SHOW_SIGN_IN_SCREEN -> {
                    showSignIn()
                }
            }
        }
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let{
            if(it){
                mainActPb.visibility = View.VISIBLE
            }else{
                mainActPb.visibility = View.GONE
            }
        }
    }

    private fun initUi() {
        viewModel.startMainActivity()
    }

    private fun showMovieList() {
        showFragment(MovieListFragment.newInstance(),Constants.MOVIE_LIST_FRAGMENT)
    }

    private fun showSignIn() {
        showFragment(SignInFragment.newInstance(),Constants.SIGN_IN_FRAGMENT)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }
}
