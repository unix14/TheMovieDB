package com.unix14.android.themoviedb.features.movie_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.models.MovieListResponse
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatedMoviesViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class ErrorEvent{
        NO_ERROR,
        FETCH_DATA_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val movieListData = MutableLiveData<ArrayList<Movie>>()
    val paginationStatus = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()
    private var lastMovieListResponse : MovieListResponse? = null

    fun getRatedMoviesList() {
        progressData.startProgress()

        //try loading list from settings
        val savedList = apiSettings.getRatedMovieList()
        if (savedList.isNotEmpty()){
            movieListData.postValue(savedList)
            paginationStatus.postValue(false)
            progressData.endProgress()
            return
        }
        //else try reusing last response from server
        val lastResponse = movieListData.value
        lastResponse?.let{
            movieListData.postValue(it)
            paginationStatus.postValue(it.size < Constants.API_PAGINATION_ITEMS_PER_PAGE)
            progressData.endProgress()
            return
        }
        //else do the request for this list
        apiService.getRatedMoviesForGuest(apiSettings.guestSessionId).enqueue(object :Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>,response: Response<MovieListResponse>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    lastMovieListResponse = response.body()

                    lastMovieListResponse?.let{
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
                        movieListData.postValue(it.results)

                        apiSettings.setRatedMovieList(it.results)

                        paginationStatus.postValue(it.page < it.totalPages)
                    }
                }else{
                    errorEvent.postValue(ErrorEvent.FETCH_DATA_ERROR)
                }
            }

            override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }
        })
    }

    fun getAdditionalMovies(page: Int) {
        if(page <= 1 || page > lastMovieListResponse!!.totalPages){
            //we check to see if we still in the first page
            // or we asking for a page that don't exist
            // and this way we don't need to make another call
            return
        }
        progressData.startProgress()
        apiService.getRatedMoviesForGuest(apiSettings.guestSessionId,page).enqueue(object :Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>,response: Response<MovieListResponse>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    lastMovieListResponse = response.body()

                    lastMovieListResponse?.let{
                        val paginatedList = movieListData.value!!.plus(it.results) as ArrayList<Movie>
                        movieListData.postValue(paginatedList)
                        paginationStatus.postValue(it.page < it.totalPages)
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
                        apiSettings.setRatedMovieList(paginatedList)
                    }
                }else{
                    errorEvent.postValue(ErrorEvent.FETCH_DATA_ERROR)
                }
            }

            override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }
        })
    }
}
