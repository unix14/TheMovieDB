package com.unix14.android.themoviedb.features.movie_list

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.movie_list_item.view.*


class MovieListAdapter(private val listener: MovieListAdapterListener) :
    ListAdapter<Movie, MovieListAdapter.MovieItemViewHolder>(MovieListDiffCallback()) {

    interface MovieListAdapterListener {
        fun onMovieClick(movie: Movie)
    }

    class MovieItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.movieListItemImage
        var name: TextView = view.movieListItemName
        var description: TextView = view.movieListItemDescription
        var stars: RatingBar = view.movieListItemRatingBar
        var readMore: TextView = view.movieDetailsFragWebsiteLink

        fun bind(movie: Movie) {
            Glide.with(itemView.context)
                .load(Constants.SMALL_POSTER_BASE_URL + movie.image)
                .apply(RequestOptions().transform(RoundedCorners(Constants.POSTER_ROUNDED_CORNERS_RADIUS)))
                .into(image)

            name.text = movie.name
            description.text = movie.overview
            readMore.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            stars.rating = movie.voteAvg % Constants.TOTAL_RATING_STARS

            readMore.setOnClickListener {
                it.isActivated = !it.isActivated

                if (!it.isActivated) {
                    description.maxLines = Constants.MINIMUM_DESCRIPTION_LINES
                    readMore.text = itemView.context.getString(R.string.movie_list_frag_read_more)

                } else {
                    description.maxLines = Int.MAX_VALUE
                    readMore.text = itemView.context.getString(R.string.movie_list_frag_read_less)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        return MovieItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item,parent,false))
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)

        holder.itemView.setOnClickListener {
            listener.onMovieClick(movie)
        }
    }

    class MovieListDiffCallback : DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.equals(newItem)
        }
    }
}