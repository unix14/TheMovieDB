package com.unix14.android.themoviedb.features

import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.models.MovieListResponse
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class NavigationEvent {
        SHOW_MOVIE_LIST_SCREEN,
        SHOW_RATED_MOVIE_SCREEN,
        SHOW_SPLASH_SCREEN,
    }

    enum class ErrorEvent {
        NO_ERROR,
        AUTH_FAILED_ERROR,
        FETCH_RATED_MOVIE_LIST_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()
    val ratedMovieList: ArrayList<Movie> = arrayListOf()

    private fun getRatedMoviesList() {
        progressData.startProgress()
        apiService.getRatedMoviesForGuest(apiSettings.guestSessionId).enqueue(object : Callback<MovieListResponse> {
                override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        val movieListResponse = response.body()
                        movieListResponse?.let {
                            val movieList = it.results
                            ratedMovieList.addAll(movieList)
                            apiSettings.setRatedMovieList(movieList)
                        }

                        navigationEvent.postValue(NavigationEvent.SHOW_MOVIE_LIST_SCREEN)
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
                    } else {
                        errorEvent.postValue(ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR)
                    }
                }

                override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                    errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
                    progressData.endProgress()
                }
            })
    }

    fun startMainActivity() {
        if (apiSettings.isValidUser() && ratedMovieList.isEmpty()) {
            getRatedMoviesList()
        } else {
            navigationEvent.postValue(NavigationEvent.SHOW_MOVIE_LIST_SCREEN)
            errorEvent.postValue(ErrorEvent.AUTH_FAILED_ERROR)

            ratedMovieList.addAll(apiSettings.getRatedMovieList())
        }
    }

    /**
    *
     * Some of the information we get alongside Movie in Movie's list responses is
     * missing since we need to make a different call to get it.
     * for example when we fetch ArrayList<Movie> of all Movies we get language field is "en"
     * so we have to make a call and get a list of all of the languages.
     * and then later when we want to open a new MovieDetailsFragment we can know by checking this list that "en" means "English"
     * Rating, Language and Genre
     * should be fetched separately at startup (SplashActivity)
    *
     */
    fun getMovieAdditionalData(movie: Movie): Movie {
        movie.rating = getMovieRating(movie.id)
        movie.language = getLanguageByIso(movie.originalLang)
        movie.genre = getGenreNameByGenreID(movie.genreIds[0].toString())
        return movie
    }

    private fun getMovieRating(desiredMovieId: Int): Float {
        for (movie in ratedMovieList) {
            if (movie.id == desiredMovieId)
                return movie.rating
        }
        return 0f
    }


    private fun getLanguageByIso(iso: String): String {
        val langList = apiSettings.getLanguageList()
        for (lang in langList) {
            if (lang.iso == iso)
                return lang.englishName
        }
        return iso
    }

    private fun getGenreNameByGenreID(genreId: String): String {
        val genres = apiSettings.getGenresList()
        for (genre in genres) {
            if (genre.id == genreId)
                return genre.name
        }
        return ""
    }

    /**
     *
     * we add the newly rated movie to a local list
     * because we already know the list we got is updated at this point
     * so we only need to add the newly rated movie to the list of rated movies.
     * This list is the list we later show in 'Rated By You' list
     *
     */
    fun addLocalRatedMovie(movie: Movie) {
        ratedMovieList.add(movie)
        apiSettings.setRatedMovieList(ratedMovieList)
    }
}