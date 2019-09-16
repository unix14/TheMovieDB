package com.unix14.android.themoviedb.features

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.custom_views.HeaderView
import com.unix14.android.themoviedb.features.movie_details.MovieDetailsFragment
import com.unix14.android.themoviedb.features.movie_details.trailers.VideoThumbnailFragment
import com.unix14.android.themoviedb.features.movie_list.MovieListFragment
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
                MainViewModel.NavigationEvent.SHOW_SPLASH_SCREEN -> {
                    showSplash()
                }
            }
        }
    }

    private fun initUi() {
        mainActListHeaderView.listener = this
        initDrawerMenu()
        viewModel.startMainActivity()
    }

    private fun initDrawerMenu() {
        mainActDrawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.searchAMovie -> {
                    onHeaderSearchClick()
                }
                R.id.allMovies -> {
                    onAllMoviesClick()
                }
                R.id.mostRatedMovies -> {
                    onMostRatedMoviesClick()
                }
                R.id.ratedMovies -> {
                    onRatedMoviesClick()
                }
            }
            mainActDrawerLayout.closeDrawers()
            false
        }
    }

    override fun onHeaderMenuClick() {
        mainActDrawerLayout.openDrawer(mainActDrawer,true)
    }

    private fun onMostRatedMoviesClick() {
        val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
        if (movieListFrag != null) {
            movieListFrag.setListType(Constants.MOVIE_LIST_MOST_RATED_MOVIES_TYPE)
            mainActListHeaderView.setTitle(getString(R.string.nav_menu_most_rated_movies))
        } else {
            showMovieList(Constants.MOVIE_LIST_MOST_RATED_MOVIES_TYPE)
        }
    }

    private fun onAllMoviesClick() {
        val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
        if (movieListFrag != null) {
            movieListFrag.setListType(Constants.MOVIE_LIST_ALL_MOVIES_TYPE)
            mainActListHeaderView.setTitle(getString(R.string.header_view_all_movies_title))
        } else {
            showMovieList(Constants.MOVIE_LIST_ALL_MOVIES_TYPE)
        }
    }

    private fun onRatedMoviesClick() {
        val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
        if (movieListFrag != null) {
            movieListFrag.setListType(Constants.MOVIE_LIST_RATED_MOVIES_TYPE)
            mainActListHeaderView.setTitle(getString(R.string.header_view_rated_movies_title))
        } else {
            showMovieList(Constants.MOVIE_LIST_RATED_MOVIES_TYPE)
        }
    }

    override fun onHeaderSearchClick() {
        Toast.makeText(this, "Search", Toast.LENGTH_LONG).show()
    }

    private fun showMovieList(listType: Int) {
        when(listType){
            Constants.MOVIE_LIST_ALL_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.header_view_all_movies_title))
            }
            Constants.MOVIE_LIST_RATED_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.header_view_rated_movies_title))
            }
        }
        showFragment(MovieListFragment.newInstance(listType), Constants.MOVIE_LIST_FRAGMENT)
    }

    private fun showMovieDetails(movie: Movie) {
        MovieDetailsFragment.newInstance(movie).show(supportFragmentManager, Constants.MOVIE_DETAILS_FRAGMENT)
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
        openWebBrowser(Constants.IMDB_BASE_URL + imdbId)
    }

    private fun openWebBrowser(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    override fun addRatedMovieToLocalList(movie: Movie) {
        viewModel.addLocalRatedMovie(movie)
    }

    override fun onVideoIdClick(videoId: String) {
        openWebBrowser(Constants.YOUTUBE_VIDEO_BASE_URL + videoId)
    }

    override fun searchMovieInNetflix(movieName: String) {
        try {
            val intent = Intent(Intent.ACTION_SEARCH)
            intent.setClassName("com.netflix.mediaclient","com.netflix.mediaclient.ui.search.SearchActivity")
            intent.putExtra("query", movieName)
            ContextCompat.startActivity(this, intent, null)

        } catch (e: Exception) {
            openWebBrowser(Constants.NETFLIX_SEARCH_URL + movieName)
        }
    }

    override fun searchMovieInGoogle(movieName: String) {
        openWebBrowser(Constants.GOOGLE_SEARCH_URL + movieName)
    }
}
