package com.unix14.android.themoviedb.features.movie_details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.common.DateUtils
import com.unix14.android.themoviedb.features.movie_details.trailers.TrailersAdapter
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.models.Video
import kotlinx.android.synthetic.main.movie_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.ceil
import kotlin.math.roundToInt


private const val MOVIE_KEY = "movie_key"

class MovieDetailsFragment : DialogFragment(), ViewPager.OnPageChangeListener {

    interface MovieDetailsFragmentListener {
        fun openIMDBWebsite(imdbId: String)
        fun addRatedMovieToLocalList(movie: Movie)
        fun searchMovieInNetflix(movieName: String)
        fun searchMovieInGoogle(movieName: String)
        fun shareMovie(movie: Movie)
    }

    private var listener: MovieDetailsFragmentListener? = null
    private lateinit var adapter: TrailersAdapter

    private lateinit var movie: Movie

    companion object {
        /**
         *
         * @param movie the companion item clicked as a String
         * @return A new instance of fragment MovieDetailsFragment.
         */
        @JvmStatic
        fun newInstance(movie: Movie) =
            MovieDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MOVIE_KEY, movie)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getSerializable(MOVIE_KEY) as Movie
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    private val viewModel by viewModel<MovieDetailsViewModel>()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.movie_details_fragment, container, false)
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!,R.color.dark_43)))
            it.attributes?.windowAnimations = R.style.FullScreenDialogStyle
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewModel()
        initTrailersList()
        initClicks()
        initUi()
    }

    private fun initUi() {
        val movieId = movie.id.toString()
        viewModel.getMovieDetails(movieId)
        viewModel.getVideosForMovie(movieId)

        movie.rating.let {
            movieDetailsFragRatingBar.rating = it
            setIsRatedLayout(it > 0)
            movieDetailsFragRateBtn.visibility = View.GONE
        }

        movie.language.let {
            movieDetailsFragLanguage.text = it
        }

        movie.genre.let {
            movieDetailsFragGenre.text = it
        }
    }

    private fun initTrailersList() {
        val backdropThumbnail = Constants.BIG_POSTER_BASE_URL + movie.backdropPath
        adapter = TrailersAdapter(childFragmentManager, backdropThumbnail)
        movieDetailsFragViewPager.adapter = adapter
        movieDetailsFragViewPager.addOnPageChangeListener(this)
    }

    private fun initClicks() {
        movieDetailsFragRateBtn.setOnClickListener {
            val enteredRating = movieDetailsFragRatingBar.rating
            if (enteredRating > 0) {
                viewModel.sendRating(movie.id.toString(), enteredRating)
            } else {
                Toast.makeText(context, "You MUST rate at least 0.5 star", Toast.LENGTH_SHORT).show()
            }
        }

        movieDetailsFragRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            movieDetailsFragRateBtn.alpha = 0f
            movieDetailsFragRateBtn.visibility = View.VISIBLE
            movieDetailsFragRateBtn.animate()
                .alpha(1f)
                .setDuration(Constants.FASTER_ALPHA_DURATION_IN_MS)
                .start()

            //Scroll to end of fragment
            movieDetailsFragScrollView.post { movieDetailsFragScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }

        movieDetailsFragNetflixLink.setOnClickListener {
            listener?.searchMovieInNetflix(movie.name)
        }

        movieDetailsFragGoogleLink.setOnClickListener {
            listener?.searchMovieInGoogle(movie.name)
        }
    }

    private fun setupViewModel() {
        viewModel.progressData.observe(viewLifecycleOwner,Observer {
                isLoading -> handleProgressBar(isLoading) })
        viewModel.movieDetailsData.observe(viewLifecycleOwner,Observer {
                movieDetails -> handleMovieDetails(movieDetails) })
        viewModel.errorEvent.observe(viewLifecycleOwner,Observer {
                errorEvent -> handleErrorEvent(errorEvent) })
        viewModel.ratingMovieEvent.observe(viewLifecycleOwner,Observer {
                ratingEvent -> handleRatingEvent(ratingEvent) })
        viewModel.trailerVideosData.observe(this,Observer {
                trailerVideos -> handleTrailers(trailerVideos) })
    }

    private fun handleTrailers(trailerVideos: ArrayList<Video>?) {
        trailerVideos?.let {
            adapter.updateList(it)
            movieDetailsFragIndicator.setViewPager(movieDetailsFragViewPager)

            if (it.isNotEmpty()) {
                movieDetailsFragNextPage.alpha = 0f
                movieDetailsFragNextPage.visibility = View.VISIBLE

                //Animate fade in
                movieDetailsFragNextPage.animate()
                    .alpha(1f)
                    .setStartDelay(Constants.FASTER_ALPHA_DURATION_IN_MS)
                    .setDuration(Constants.DEFAULT_ALPHA_DURATION_IN_MS)
                    .start()
            } else {
                movieDetailsFragNextPage.visibility = View.GONE
            }
        }
    }

    private fun handleRatingEvent(ratingEvent: MovieDetailsViewModel.RatingEvent?) {
        ratingEvent?.let {
            when (it) {
                MovieDetailsViewModel.RatingEvent.RATING_ERROR -> {
                    Toast.makeText(context,"Rating failed, Please try again later",Toast.LENGTH_LONG).show()
                }
                MovieDetailsViewModel.RatingEvent.RATED -> {
                    Toast.makeText(context, "Rating Sent!", Toast.LENGTH_LONG).show()
                    movie.rating = movieDetailsFragRatingBar.rating
                    listener?.addRatedMovieToLocalList(movie)
                    setIsRatedLayout(true)
                }
            }
        }
    }

    private fun handleErrorEvent(errorEvent: MovieDetailsViewModel.ErrorEvent?) {
        errorEvent?.let {
            when (it) {
                MovieDetailsViewModel.ErrorEvent.SERVER_CONNECTION_ERROR -> {
                    Toast.makeText(context, "Connection to server failed", Toast.LENGTH_LONG).show()
                }
                MovieDetailsViewModel.ErrorEvent.FETCH_DATA_ERROR -> {
                    Toast.makeText(context, "Fetch data from server failed", Toast.LENGTH_LONG).show()
                }
                MovieDetailsViewModel.ErrorEvent.NO_ERROR -> {
                }
            }
        }
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let {
            if (it) {
                movieDetailsFragPb.visibility = View.VISIBLE
            } else {
                movieDetailsFragPb.visibility = View.GONE
            }
        }
    }

    private fun handleMovieDetails(movieDetails: Movie?) {
        movieDetails?.let {
            //Set Movie Details
            movieDetailsFragName.text = it.name
            movieDetailsFragDescription.text = it.overview
            movieDetailsFragYear.text = DateUtils.getYear(it.realeseDate)
            movieDetailsFragVotes.text = it.voteCount.toString()
            movieDetailsFragPopularity.text = ceil(it.popularity.toDouble()).roundToInt().toString() + "%"
            movieDetailsFragPublicRatingBar.rating = it.voteAvg % 5

            //setClicks
            movieDetailsFragWebsiteLink.setOnClickListener {
                listener?.openIMDBWebsite(movieDetails.imdbId)
            }

            if (it.adult) {
                movieDetailsFragAdultFilm.visibility = View.VISIBLE
            } else {
                movieDetailsFragAdultFilm.visibility = View.GONE
            }

            Glide.with(context)
                .load(Constants.BIG_POSTER_BASE_URL + it.image)
                .apply(RequestOptions().transform(RoundedCorners(Constants.POSTER_ROUNDED_CORNERS_RADIUS)))
                .into(movieDetailsFragImage)

            movieDetailsFragShareBtn.setOnClickListener {
                listener?.shareMovie(movieDetails)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MovieDetailsFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MovieDetailsFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setIsRatedLayout(rated: Boolean) {
        if (rated) {
            //disable rating bar clicks and send button
            movieDetailsFragRateBtn.visibility = View.GONE
            movieDetailsFragRatingBar.setIsIndicator(true)
            movieDetailsFragRateTitle.text = getString(R.string.movie_details_frag_you_rated_movie)
        } else {
            // enable send button and RatingBar
            movieDetailsFragRateBtn.visibility = View.VISIBLE
            movieDetailsFragRatingBar.setIsIndicator(false)
            movieDetailsFragRateTitle.text = getString(R.string.movie_details_frag_rate_this_movie)
        }
    }

    //View Pager on Scroll Changed Listener's callbacks
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageSelected(position: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //if swiped first element
        if (position > 0) {
            movieDetailsFragNextPage?.let {
                it.alpha = 1f
                //Animate fade out
                it.animate()
                    .alpha(0f)
                    .setStartDelay(Constants.DEFAULT_ALPHA_DURATION_IN_MS)
                    .setDuration(Constants.DEFAULT_ALPHA_DURATION_IN_MS)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            it.visibility = View.GONE
                        }
                    })
                    .start()
            }
        }
    }
}
