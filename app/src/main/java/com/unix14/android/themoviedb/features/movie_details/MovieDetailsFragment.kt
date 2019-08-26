package com.unix14.android.themoviedb.features.movie_details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.common.DateUtils
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.movie_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val MOVIE_ID = "movie_id"
class MovieDetailsFragment : Fragment() {

    interface MovieDetailsFragmentListener{
        fun openIMDBWebsite(imdbId: String)
    }

    private var listener: MovieDetailsFragmentListener? = null
    private var movieId: String? = null

    companion object {
        /**
         *
         * @param movieId of the movie item clicked as a String
         * @return A new instance of fragment MovieDetailsFragment.
         */
        @JvmStatic
        fun newInstance(movieId: String) =
            MovieDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(MOVIE_ID, movieId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movieId = it.getString(MOVIE_ID)
        }
    }

    private val viewModel by viewModel<MovieDetailsViewModel>()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        initUi()
    }

    private fun setupViewModel() {
        viewModel.progressData.observe(this, Observer {
            isLoading -> handleProgressBar(isLoading) })
        viewModel.movieDetailsData.observe(this, Observer {
            movieDetails -> handleMovieDetails(movieDetails) })
        viewModel.errorEvent.observe(this, Observer {
            errorEvent -> handleErrorEvent(errorEvent) })
    }

    private fun handleErrorEvent(errorEvent: MovieDetailsViewModel.ErrorEvent?) {
        errorEvent?.let{
            when(it){
                MovieDetailsViewModel.ErrorEvent.SERVER_CONNECTION_ERROR ->{
                    Toast.makeText(context,"Connection to server failed",Toast.LENGTH_LONG).show()
                }
                MovieDetailsViewModel.ErrorEvent.FETCH_DATA_ERROR -> {
                    Toast.makeText(context,"Fetch data from server failed",Toast.LENGTH_LONG).show()
                }
                MovieDetailsViewModel.ErrorEvent.NO_ERROR ->{}
            }
        }
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let{
            if(it){
                movieDetailsFragPb.visibility = View.VISIBLE
            }else{
                movieDetailsFragPb.visibility = View.GONE
            }
        }
    }

    private fun handleMovieDetails(movieDetails: Movie?) {
        movieDetails?.let{
            movieDetailsFragName.text = it.name
            movieDetailsFragDescription.text = it.overview

            movieDetailsFragYear.text = DateUtils.getYear(it.realeseDate)

            movieDetailsFragLanguage.text = it.originalLang

            movieDetailsFragVotes.text = it.voteCount.toString()
            movieDetailsFragPopularity.text = it.popularity.toString()

            movieDetailsFragWebsiteLink.setOnClickListener {
                listener?.openIMDBWebsite(movieDetails.imdbId)
            }

            if(it.adult){
                movieDetailsFragAdultFilm.visibility = View.VISIBLE
            }else{
                movieDetailsFragAdultFilm.visibility = View.GONE
            }



            Glide.with(context)
                .load(Constants.BIG_POSTER_BASE_URL + it.image)
                .apply(RequestOptions().transform(RoundedCorners(Constants.POSTER_ROUNDED_CORNERS_RADIUS)))

                .into(movieDetailsFragImage)

            Glide.with(context)
                .load(Constants.BIG_POSTER_BASE_URL + it.backdropPath)
                .into(movieDetailsFragMovieImage)
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

    private fun initUi() {
        movieId?.let{
            viewModel.getMovieDetails(it)
        }

    }


}
