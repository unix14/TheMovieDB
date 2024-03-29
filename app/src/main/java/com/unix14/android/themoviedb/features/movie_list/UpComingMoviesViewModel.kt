package com.unix14.android.themoviedb.features.movie_list

import androidx.lifecycle.MutableLiveData
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

//todo:: get rid of this horrible code duplication -> fast!!!
class UpComingMoviesViewModel(private val apiService: ApiService) : ViewModel() {

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

    fun getUpcomingMoviesList() {
        progressData.startProgress()
        //todo use coroutines instead
        apiService.getUpComingMovies().enqueue(object :Callback<MovieListResponse>{
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
        if(page <= 1 || page > lastMovieListResponse!!.totalPages){
            //we check to see if we still in the first page
            // or we asking for a page that don't exist
            // and this way we don't need to make another call
            return
        }
        progressData.startProgress()
        apiService.getUpComingMovies(page).enqueue(object :Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>,response: Response<MovieListResponse>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    lastMovieListResponse = response.body()

                    lastMovieListResponse?.let{
                        val paginatedList = movieListData.value!!.plus(it.results) as ArrayList<Movie>
                        movieListData.postValue(paginatedList)
                        paginationStatus.postValue(it.page < it.totalPages)
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
