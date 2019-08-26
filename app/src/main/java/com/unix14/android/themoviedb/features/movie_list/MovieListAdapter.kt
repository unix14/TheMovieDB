package com.unix14.android.themoviedb.features.movie_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.movie_list_item.view.*

const val POSTER_ROUNDED_CORNERS_RADIUS = 30
class MovieListAdapter(private val listener: MovieListAdapterListener) : RecyclerView.Adapter<MovieListAdapter.MovieItemViewHolder>() {

    private var movieList:ArrayList<Movie> =  arrayListOf()
    interface MovieListAdapterListener{
        fun onMovieClick(movie: Movie)
    }

    class MovieItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.movieListItemImage
        var name: TextView = view.movieListItemName
        var description: TextView = view.movieListItemDescription

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load(Constants.SMALL_POSTER_BASE_URL + movie.image)
                .apply(RequestOptions().transform(RoundedCorners(POSTER_ROUNDED_CORNERS_RADIUS)))
                .into(image)

            name.text = movie.name
            description.text = movie.overview
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        return MovieItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false))
    }

    override fun getItemCount(): Int {
       return movieList.size
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = movieList[position]

        holder.bind(movie)

        holder.itemView.setOnClickListener{
            listener.onMovieClick(movie)
        }
    }

    fun updateList(newMovies: ArrayList<Movie>) {
        movieList = newMovies
        notifyDataSetChanged()
    }

    fun clear() {
        movieList.clear()
        notifyDataSetChanged()
    }
}