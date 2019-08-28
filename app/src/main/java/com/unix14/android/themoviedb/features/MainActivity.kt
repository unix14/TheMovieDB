package com.unix14.android.themoviedb.features

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.custom_views.HeaderView
import com.unix14.android.themoviedb.features.movie_details.MovieDetailsFragment
import com.unix14.android.themoviedb.features.movie_details.trailers.VideoThumbnailFragment
import com.unix14.android.themoviedb.features.movie_list.MovieListFragment
import com.unix14.android.themoviedb.features.sign_in.SignInFragment
import com.unix14.android.themoviedb.features.splash.SplashActivity
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), MovieListFragment.MovieListFragmentListener,
    MovieDetailsFragment.MovieDetailsFragmentListener,
    HeaderView.HeaderViewListener, VideoThumbnailFragment.VideoThumbnailFragmentListener {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        initHeaderView()
        initUi()
    }

    private fun setupViewModel() {
        viewModel.navigationEvent.observe(this, Observer {
                navigationEvent -> handleNavigationEvent(navigationEvent) })
        viewModel.errorEvent.observe(this, Observer {
                errorEvent -> handleErrorEvent(errorEvent) })
        viewModel.progressData.observe(this, Observer {
                isLoading -> handleProgressBar(isLoading) })
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let {
            if (it) {
                mainActPb.visibility = View.VISIBLE
            } else {
                mainActPb.visibility = View.GONE
            }
        }
    }

    private fun handleErrorEvent(errorEvent: MainViewModel.ErrorEvent?) {
        errorEvent?.let {
            when (it) {
                MainViewModel.ErrorEvent.AUTH_FAILED_ERROR -> {
                    Toast.makeText(this, "Authentication with server failed, Please try again", Toast.LENGTH_LONG).show()
                }
                MainViewModel.ErrorEvent.CONNECTION_FAILED_ERROR -> {
                    Toast.makeText(this, "Connection with server failed, Please try again", Toast.LENGTH_LONG).show()
                }
                MainViewModel.ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR -> {
                    Toast.makeText(this,"Fetching rated movies list from server has failed, Please try again",Toast.LENGTH_LONG).show()
                }
                MainViewModel.ErrorEvent.NO_ERROR -> { }
            }
        }
    }

    private fun showSplash() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    /**
     *
     * We can control which screen to show from MainViewModel
     * also possible to decide which list to show; All movies or rated ones
     *
     */
    private fun handleNavigationEvent(navigationEvent: MainViewModel.NavigationEvent?) {
        navigationEvent?.let {
            when (navigationEvent) {
                MainViewModel.NavigationEvent.SHOW_MOVIE_LIST_SCREEN -> {
                    showMovieList(Constants.MOVIE_LIST_ALL_MOVIES_TYPE)
                }
                MainViewModel.NavigationEvent.SHOW_RATED_MOVIE_SCREEN -> {
                    showMovieList(Constants.MOVIE_LIST_RATED_MOVIES_TYPE)
                }
                MainViewModel.NavigationEvent.SHOW_SIGN_IN_SCREEN -> {
                    showSignIn()
                }
                MainViewModel.NavigationEvent.SHOW_SPLASH_SCREEN -> {
                    showSplash()
                }
            }
        }
    }

    private fun initUi() {
        viewModel.startMainActivity()
    }

    private fun initHeaderView() {
        mainActListHeaderView.listener = this
        setHeaderViewLayout(true)
    }

    override fun onHeaderAllMoviesClick() {
        setHeaderViewLayout(true)
        val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
        if (movieListFrag != null) {
            movieListFrag.setListType(Constants.MOVIE_LIST_ALL_MOVIES_TYPE)
        } else {
            showMovieList(Constants.MOVIE_LIST_ALL_MOVIES_TYPE)
        }
    }

    override fun onHeaderRatedMoviesClick() {
        setHeaderViewLayout(false)
        val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
        if (movieListFrag != null) {
            movieListFrag.setListType(Constants.MOVIE_LIST_RATED_MOVIES_TYPE)
        } else {
            showMovieList(Constants.MOVIE_LIST_RATED_MOVIES_TYPE)
        }
    }

    private fun setHeaderViewLayout(isAllMoviesScreen: Boolean) {
        if (isAllMoviesScreen) {
            mainActListHeaderView.setRatedMoviesButtonClickable(true)
            mainActListHeaderView.setAllMoviesButtonClickable(false)
            mainActListHeaderView.setRatedMoviesButtonActivated(false)
            mainActListHeaderView.setAllMoviesButtonActivated(true)
            mainActListHeaderView.setTitle(getString(R.string.header_view_all_movies_title))
        } else {
            mainActListHeaderView.setRatedMoviesButtonClickable(false)
            mainActListHeaderView.setAllMoviesButtonClickable(true)
            mainActListHeaderView.setRatedMoviesButtonActivated(true)
            mainActListHeaderView.setAllMoviesButtonActivated(false)
            mainActListHeaderView.setTitle(getString(R.string.header_view_rated_movies_title))
        }
    }

    private fun showMovieList(listType: Int) {
        showFragment(MovieListFragment.newInstance(listType), Constants.MOVIE_LIST_FRAGMENT)
    }

    private fun showSignIn() {
        showFragment(SignInFragment.newInstance(), Constants.SIGN_IN_FRAGMENT)
    }

    private fun showMovieDetails(movie: Movie) {
        MovieDetailsFragment.newInstance(movie).show(supportFragmentManager, Constants.SIGN_IN_FRAGMENT)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.popup_show, 0, 0, R.anim.popup_hide)
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }

    private fun getFragmentByTag(tag: String): Fragment? {
        val fragmentManager = this@MainActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.tag == tag)
                return fragment
        }
        return null
    }

    override fun onMovieClick(movie: Movie) {
        val movieWithData = viewModel.getMovieAdditionalData(movie)
        showMovieDetails(movieWithData)
    }

    override fun openIMDBWebsite(imdbId: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.IMDB_BASE_URL + imdbId)))
    }

    private fun openYoutubeVideo(videoId: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_VIDEO_BASE_URL + videoId)))
    }

    override fun addRatedMovieToLocalList(movie: Movie) {
        viewModel.addLocalRatedMovie(movie)
    }

    override fun onVideoIdClick(videoId: String) {
        openYoutubeVideo(videoId)
    }
}
