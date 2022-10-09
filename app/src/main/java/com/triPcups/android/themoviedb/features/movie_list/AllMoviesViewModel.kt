package com.triPcups.android.themoviedb.features.movie_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.triPcups.android.themoviedb.common.ProgressData
import com.triPcups.android.themoviedb.common.SingleLiveEvent
import com.triPcups.android.themoviedb.models.Movie
import com.triPcups.android.themoviedb.models.MovieListResponse
import com.triPcups.android.themoviedb.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMoviesViewModel(private val apiService: ApiService) : ViewModel() {

    enum class ErrorEvent{
        NO_ERROR,
        FETCH_DATA_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val movieListData = MutableLiveData<ArrayList<Movie>>()
    val paginationStatus = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()
    private var lastMovieListResponse: MovieListResponse? = null

    fun getMovieList() {
        progressData.startProgress()
        apiService.getNowInCinema().enqueue(object :Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>,response: Response<MovieListResponse>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    lastMovieListResponse = response.body()

                    lastMovieListResponse?.let{
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
                        movieListData.postValue(it.results)

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
        if(page <= 1 && page <= lastMovieListResponse!!.totalPages){
            //we check to see if we still in the first page
            // and this way we don't need to make another call
            return
        }
        progressData.startProgress()
        apiService.getNowInCinema(page).enqueue(object :Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>,response: Response<MovieListResponse>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    lastMovieListResponse = response.body()

                    lastMovieListResponse?.let{
                        val paginatedList = movieListData.value!!.plus(it.results) as ArrayList<Movie>
                        movieListData.postValue(paginatedList)
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
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
