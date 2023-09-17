package com.triPcups.android.themoviedb.features

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.emmanuelkehinde.shutdown.Shutdown
import com.google.android.gms.ads.*
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.thefinestartist.finestwebview.FinestWebView
import com.triPcups.android.themoviedb.BuildConfig
import com.triPcups.android.themoviedb.R
import com.triPcups.android.themoviedb.common.Constants
import com.triPcups.android.themoviedb.common.KeyboardUtil
import com.triPcups.android.themoviedb.custom_views.HeaderView
import com.triPcups.android.themoviedb.features.movie_details.MovieDetailsFragment
import com.triPcups.android.themoviedb.features.movie_details.trailers.VideoThumbnailFragment
import com.triPcups.android.themoviedb.features.movie_list.MovieListFragment
import com.triPcups.android.themoviedb.features.splash.SplashActivity
import com.triPcups.android.themoviedb.models.Movie
import com.triPcups.android.themoviedb.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random


class MainActivity : AppCompatActivity(), MovieListFragment.MovieListFragmentListener,
    MovieDetailsFragment.MovieDetailsFragmentListener,
    HeaderView.HeaderViewListener, VideoThumbnailFragment.VideoThumbnailFragmentListener {

    private lateinit var binding: ActivityMainBinding
    private var movieClicks: Int = 0
    private val viewModel by viewModel<MainViewModel>()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        initUi()

        initAds()
    }

    private fun initAds() = with(binding) {
        //bottom banner
        if(BuildConfig.DEBUG) {
            mainActAdView.visibility = View.GONE
        } else {
            MobileAds.initialize(this@MainActivity)
            val adRequest: AdRequest = AdRequest.Builder().build()
            mainActAdView.loadAd(adRequest)
        }

        //interstitial
        mInterstitialAd = InterstitialAd(this@MainActivity)
        mInterstitialAd?.adUnitId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            "ca-app-pub-7481638286003806/9040934858"
        }
        loadInterstitial()
    }

    private fun loadInterstitial() {
        mInterstitialAd?.loadAd(AdRequest.Builder().build())
    }

    private fun setupViewModel() {
        viewModel.navigationEvent.observe(this,Observer {
                navigationEvent -> handleNavigationEvent(navigationEvent) })
        viewModel.errorEvent.observe(this, Observer {
                errorEvent -> handleErrorEvent(errorEvent) })
        viewModel.progressData.observe(this, Observer {
                isLoading -> handleProgressBar(isLoading) })
    }

    private fun handleProgressBar(isLoading: Boolean?) = with(binding) {
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
//                MainViewModel.ErrorEvent.AUTH_FAILED_ERROR -> {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.error_auth_fail),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
                MainViewModel.ErrorEvent.CONNECTION_FAILED_ERROR -> {
                    Toast.makeText(
                        this,
                        getString(R.string.error_connection_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
//                MainViewModel.ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR -> {
//                    Toast.makeText(
//                        this,
//                        getString(R.string.error_fetching_movies_fail),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
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

    private fun initUi()= with(binding) {
        mainActListHeaderView.listener = this@MainActivity
        initDrawerMenu()
        viewModel.startMainActivity()
    }

    private fun initDrawerMenu()= with(binding) {
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
                R.id.upcomingMovies -> {
                    onUpcomingMoviesClick()
                }
                R.id.popularMovies -> {
                    onPopularMoviesClick()
                }
                R.id.shareApp -> {
                    shareThisApp()
                }
                R.id.moreCoolAppsBtn -> {
                    moreCoolApps()
                }
                R.id.tripyCupsBtn -> {
                    open3pCupsApp()
                }
                R.id.rateBtn -> {
                    rateThisApp()
                }
            }
            mainActDrawerLayout.closeDrawers()
            false
        }


//        appTitle.setOnClickListener {
//            rateThisApp()
//        }
    }

    private fun open3pCupsApp() {
        val triPckg = "com.triPcups.www3P.cups"
        val isInstalled = isPackageInstalled(triPckg, packageManager)

        if(isInstalled) {
            val launchIntent =
                packageManager.getLaunchIntentForPackage(triPckg)
            launchIntent?.let { startActivity(it) }
        } else {
            openWebBrowser("http://3p-cups.blogspot.com/")
        }
    }

    private fun moreCoolApps() {
        val devId = "8456795065374888880"
        try {
            var playstoreuri1: Uri = Uri.parse("market://dev?id=" + devId)
            var playstoreIntent1 = Intent(ACTION_VIEW, playstoreuri1)
            startActivity(playstoreIntent1)
        }catch (exp:Exception){
            var playstoreuri2: Uri = Uri.parse("https://play.google.com/store/apps/dev?id=" + devId)
            var playstoreIntent2 = Intent(ACTION_VIEW, playstoreuri2)
            startActivity(playstoreIntent2)
        }
    }

    private fun rateThisApp() {
        val manager = if(BuildConfig.DEBUG) {
            FakeReviewManager(this)
        } else {
            ReviewManagerFactory.create(this)
        }

        //launch google's in-app review dialog
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = request.result

                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.


                    Toast.makeText(this, "Thank you for rating this app :)", Toast.LENGTH_LONG).show()

                    // There was some problem, continue regardless of the result.
                    try {
                        val playstoreuri1: Uri = Uri.parse("market://details?id=" + packageName)
                        val playstoreIntent1 = Intent(ACTION_VIEW, playstoreuri1)
                        startActivity(playstoreIntent1)
                    }catch (exp:Exception){
                        val playstoreuri2: Uri = Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
                        val playstoreIntent2 = Intent(ACTION_VIEW, playstoreuri2)
                        startActivity(playstoreIntent2)
                    }




                }

            } else {
                // There was some problem, continue regardless of the result.
                try {
                    val playstoreuri1: Uri = Uri.parse("market://details?id=" + packageName)
                    val playstoreIntent1 = Intent(ACTION_VIEW, playstoreuri1)
                    startActivity(playstoreIntent1)
                }catch (exp:Exception){
                    val playstoreuri2: Uri = Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
                    val playstoreIntent2 = Intent(ACTION_VIEW, playstoreuri2)
                    startActivity(playstoreIntent2)
                }
            }
        }
    }

    private fun isPackageInstalled(
        packageName: String,
        packageManager: PackageManager
    ): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    private fun shareThisApp() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = getString(R.string.nav_menu_share_this_app_text) + Constants.GOOGLE_STORE_BASE_URL + applicationContext.packageName
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    override fun shareMovie(movie: Movie) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = getString(R.string.movie_details_frag_share_movie_text) + Constants.IMDB_BASE_URL + movie.imdbId
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, movie.name)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    override fun onHeaderMenuClick() {
        binding.apply {
            mainActDrawerLayout.openDrawer(mainActDrawer, true)
        }
    }

    private fun onHeaderSearchClick() = with(binding){
        mainActListHeaderView.openSearchField()
    }

    override fun onHeaderSearchSubmit(query: String?) {
        if(!query.isNullOrEmpty()){
            showMovieListByType(Constants.SEARCH_MOVIES_TYPE)
            val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
            movieListFrag?.setQuery(query)
        }
    }

    override fun onOpenKeyboard() {
        KeyboardUtil.openKeyboard(this)
    }

    override fun onSearchFailed() {
        binding.mainActListHeaderView.closeSearchField()
        KeyboardUtil.hideKeyboard(this)
    }

    private fun onPopularMoviesClick() {
        showMovieListByType(Constants.MOVIE_LIST_POPULAR_MOVIES_TYPE)
    }

    private fun onUpcomingMoviesClick() {
        showMovieListByType(Constants.MOVIE_LIST_UPCOMING_MOVIES_TYPE)
    }

    private fun onMostRatedMoviesClick() {
        showMovieListByType(Constants.MOVIE_LIST_MOST_RATED_MOVIES_TYPE)
    }

    private fun onAllMoviesClick() {
        showMovieListByType(Constants.MOVIE_LIST_ALL_MOVIES_TYPE)
    }

    private fun onRatedMoviesClick() {
        showMovieListByType(Constants.MOVIE_LIST_RATED_MOVIES_TYPE)
    }

    private fun showMovieListByType(listType: Int) {
        val movieListFrag = getFragmentByTag(Constants.MOVIE_LIST_FRAGMENT) as MovieListFragment?
        if (movieListFrag != null) {
//            movieListFrag.setQuery(null)
            movieListFrag.setListType(listType)
            setHeaderViewTitle(listType)
        } else {
            showMovieList(listType)
        }
    }

    private fun showMovieList(listType: Int, query: String? = null) {
        setHeaderViewTitle(listType)
        showFragment(MovieListFragment.newInstance(listType, query), Constants.MOVIE_LIST_FRAGMENT)
    }

    private fun setHeaderViewTitle(listType: Int)= with(binding){
        when (listType) {
            Constants.MOVIE_LIST_ALL_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.header_view_all_movies_title))
            }
            Constants.MOVIE_LIST_RATED_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.header_view_rated_movies_title))
            }
            Constants.MOVIE_LIST_MOST_RATED_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.nav_menu_most_rated_movies))
            }
            Constants.MOVIE_LIST_UPCOMING_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.nav_menu_upcoming_movies))
            }
            Constants.MOVIE_LIST_POPULAR_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.nav_menu_popular_movies))
            }
            Constants.SEARCH_MOVIES_TYPE -> {
                mainActListHeaderView.setTitle(getString(R.string.nav_menu_search))
            }
        }
    }

    private fun showMovieDetails(movie: Movie) {
        MovieDetailsFragment.newInstance(movie)
            .show(supportFragmentManager, Constants.MOVIE_DETAILS_FRAGMENT)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.mainActListHeaderView.setVoiceQueryData(requestCode, resultCode, data)
    }

    override fun onMovieClick(movie: Movie) {
        val movieWithData = viewModel.getMovieAdditionalData(movie)
        KeyboardUtil.hideKeyboard(this)
        movieClicks++

        handleProgressBar(true)
        mInterstitialAd?.let{
            val shouldLoad = it.isLoaded && movieClicks + Random.nextInt(-3, 4) % 7 == 0
            if (shouldLoad) {
                it.show()

                it.adListener = object : AdListener() {
                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        handleProgressBar(false)
                        showMovieDetails(movieWithData)
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        handleProgressBar(false)
                        showMovieDetails(movieWithData)
                        loadInterstitial()
                    }

                }
            } else {
                handleProgressBar(false)
                showMovieDetails(movieWithData)
                loadInterstitial()
            }
        }
    }

    override fun openIMDBWebsite(imdbId: String) {
        openWebBrowser(Constants.IMDB_BASE_URL + imdbId)
    }

    private fun openWebBrowser(link: String) {
//        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))

        FinestWebView.Builder(this).show(link)
    }

    override fun addRatedMovieToLocalList(movie: Movie) {
        viewModel.addLocalRatedMovie(movie)
    }

    override fun onVideoIdClick(videoId: String) {
        val appIntent = Intent(ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            openWebBrowser(Constants.YOUTUBE_VIDEO_BASE_URL + videoId)
        }
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

    override fun onBackPressed() {
        binding.apply {
            when {
                mainActDrawerLayout.isDrawerOpen(mainActDrawer) -> mainActDrawerLayout.closeDrawers()
                mainActListHeaderView.onBackPress() -> mainActListHeaderView.closeSearchField()
                else -> Shutdown.now(this@MainActivity)
            }
        }
    }
}
