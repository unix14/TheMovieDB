package com.unix14.android.themoviedb.features

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.features.movie_details.MovieDetailsFragment
import com.unix14.android.themoviedb.features.movie_list.MovieListFragment
import com.unix14.android.themoviedb.features.sign_in.SignInFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.features.splash.SplashActivity

class MainActivity : AppCompatActivity() , MovieListFragment.MovieListFragmentListener , MovieDetailsFragment.MovieDetailsFragmentListener {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        initUi()
    }

    private fun setupViewModel() {
        viewModel.navigationEvent.observe(this, Observer {
            navigationEvent -> handleNavigationEvent(navigationEvent) })
        viewModel.errorEvent.observe(this, Observer {
            errorEvent -> handleErrorEvent(errorEvent) })
    }

    private fun handleErrorEvent(errorEvent: MainViewModel.ErrorEvent?) {
        errorEvent?.let{
            when(it){
                MainViewModel.ErrorEvent.AUTH_FAILED_ERROR -> {
                    Toast.makeText(this,"Authentication with server failed, Please try again",Toast.LENGTH_LONG).show()
                }
                MainViewModel.ErrorEvent.NO_ERROR ->{}
            }
        }
    }

    private fun showSplash() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
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
                MainViewModel.NavigationEvent.SHOW_SPLASH_SCREEN ->{
                    showSplash()
                }
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

    private fun showMovieDetails(movie: Movie) {
        MovieDetailsFragment.newInstance(movie,viewModel.getMovieRating(movie.id)).show(supportFragmentManager,Constants.SIGN_IN_FRAGMENT)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.popup_show, 0, 0, R.anim.popup_hide)
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }

    override fun onMovieClick(movie: Movie) {
        showMovieDetails(movie)
    }

    override fun openIMDBWebsite(imdbId: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.IMDB_BASE_URL + imdbId))
        startActivity(browserIntent)
    }

    override fun addRatedMovieToLocalList(movie: Movie) {
        viewModel.addLocalRatedMovie(movie)
    }

}
