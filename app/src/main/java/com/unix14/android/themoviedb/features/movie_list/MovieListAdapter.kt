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
import com.unix14.android.themoviedb.databinding.MovieListItemBinding
import com.unix14.android.themoviedb.models.Movie


class MovieListAdapter(private val listener: MovieListAdapterListener) :
    ListAdapter<Movie, MovieListAdapter.MovieItemViewHolder>(MovieListDiffCallback()) {

    private lateinit var binding: MovieListItemBinding
    private var expandedMovieId: Int = -1
    private var lastExpandedPosition: Int = -1

    interface MovieListAdapterListener {
        fun onMovieClick(movie: Movie)
    }

    class MovieItemViewHolder(binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var image: ImageView = binding.movieListItemImage
        var name: TextView = binding.movieListItemName
        var description: TextView = binding.movieListItemDescription
        var stars: RatingBar = binding.movieListItemRatingBar
        var readMore: TextView = binding.movieListItemReadMore

        fun bind(movie: Movie, isExpanded : Boolean) {
            Glide.with(itemView.context)
                .load(Constants.SMALL_POSTER_BASE_URL + movie.image)
                .apply(RequestOptions().transform(RoundedCorners(Constants.POSTER_ROUNDED_CORNERS_RADIUS)))
                .into(image)

            name.text = movie.name
            description.text = movie.overview
            readMore.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            stars.rating = movie.voteAvg % Constants.TOTAL_RATING_STARS

            if (isExpanded) {
                description.maxLines = Int.MAX_VALUE
                readMore.text = itemView.context.getString(R.string.movie_list_frag_read_less)
            } else {
                description.maxLines = Constants.MINIMUM_DESCRIPTION_LINES
                readMore.text = itemView.context.getString(R.string.movie_list_frag_read_more)
            }

            //make read more button disappear
            if(!isExpanded){
                lateinit var toggleMoreButton: Runnable
                toggleMoreButton = Runnable {
                    if(description.layout == null) { // wait while layout become available
                        description.post(toggleMoreButton)
                        return@Runnable
                    }
                    readMore.visibility = if(description.layout.text.toString() != movie.overview) View.VISIBLE else View.GONE
                }
                description.post(toggleMoreButton)
            } else {
                readMore.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        binding = MovieListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MovieItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie, expandedMovieId == movie.id)

        holder.itemView.setOnClickListener {
            listener.onMovieClick(movie)
        }
        holder.itemView.setOnLongClickListener {
            onReadMoreButtonClicked(movie,position)
            true
        }
        binding.movieListItemReadMore.setOnClickListener {
            onReadMoreButtonClicked(movie,position)
        }
    }

    private fun onReadMoreButtonClicked(movie: Movie, position: Int){
        expandedMovieId = if(expandedMovieId == movie.id){
            -1
        }else{
            movie.id
        }
        notifyItemChanged(position)
        notifyItemChanged(lastExpandedPosition)
        lastExpandedPosition = position
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