package com.unix14.android.themoviedb.features.movie_list

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.common.InfiniteRecyclerViewScrollListener
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.movie_list_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

const val LIST_TYPE_KEY = "list_type_key"

class MovieListFragment : Fragment(), MovieListAdapter.MovieListAdapterListener {

    interface MovieListFragmentListener {
        fun onMovieClick(movie: Movie)
    }

    companion object {
        /**
         *
         * @param listType the type of data to show in the list at start of this fragment
         * should be Constants.MOVIE_LIST_ALL_MOVIES_TYPE  or Constants.MOVIE_LIST_RATED_MOVIES_TYPE
         * @return A new instance of fragment MovieDetailsFragment.
         */
        @JvmStatic
        fun newInstance(listType: Int) =
            MovieListFragment().apply {
                arguments = Bundle().apply {
                    putInt(LIST_TYPE_KEY, listType)
                }
            }
    }


    private var listener: MovieListFragmentListener? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MovieListAdapter
    val viewModel by viewModel<AllMoviesViewModel>()
    val ratedViewModel by viewModel<RatedMoviesViewModel>()
    val mostRatedViewModel by viewModel<MostRatedMoviesViewModel>()
    val upcomingViewModel by viewModel<UpComingMoviesViewModel>()

    private var listType: Int = Constants.MOVIE_LIST_ALL_MOVIES_TYPE
    private lateinit var infiniteRecyclerViewScrollListener: InfiniteRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listType = it.getInt(LIST_TYPE_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewModel()
        initUi()
    }

    private fun initRecycler() {
        layoutManager = LinearLayoutManager(context)
        movieListFragRecycler.layoutManager = layoutManager

        adapter = MovieListAdapter(this)

        infiniteRecyclerViewScrollListener = getInfiniteScrollListener()
        movieListFragRecycler.addOnScrollListener(infiniteRecyclerViewScrollListener)

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(movieListFragRecycler.context, R.drawable.divider)
        dividerDrawable?.let {
            dividerItemDecoration.setDrawable(it)
        }
        movieListFragRecycler.addItemDecoration(dividerItemDecoration)

        movieListFragRecycler.adapter = adapter
    }

    private fun initUi() {
        initRecycler()
        initSwipeLayout()
        initFloatingActionButton()

        initListByType(listType)
    }

    private fun initListByType(listType: Int) {
        when (listType) {
            Constants.MOVIE_LIST_ALL_MOVIES_TYPE -> {
                viewModel.getMovieList()
            }
            Constants.MOVIE_LIST_RATED_MOVIES_TYPE -> {
                ratedViewModel.getRatedMoviesList()
            }
            Constants.MOVIE_LIST_MOST_RATED_MOVIES_TYPE -> {
                mostRatedViewModel.getRatedMoviesList()
            }
            Constants.MOVIE_LIST_UPCOMING_MOVIES_TYPE -> {
                upcomingViewModel.getUpcomingMoviesList()
            }
        }
    }

    private fun initFloatingActionButton() {
        movieListFragScrollToTopButton.setOnClickListener {
            movieListFragRecycler.smoothScrollToPosition(0)
        }

        movieListFragRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !movieListFragScrollToTopButton.isShown)
                    movieListFragScrollToTopButton.show()
                else if (dy > 0 && movieListFragScrollToTopButton.isShown)
                    movieListFragScrollToTopButton.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Handler().postDelayed({
                            movieListFragScrollToTopButton.show()
                        },2000)
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun initSwipeLayout() {
        movieListFragPullToRefresh.setColorSchemeColors(Color.BLUE, Color.RED, Color.BLACK)
        movieListFragPullToRefresh.setOnRefreshListener {
            initListByType(listType)
        }
    }

    private fun setupViewModel() {
        //Now in Cinema VM
        viewModel.progressData.observe(viewLifecycleOwner, Observer {
                isLoading -> handleProgressBar(isLoading) })
        viewModel.movieListData.observe(viewLifecycleOwner, Observer {
                movieList -> handleFeedList(movieList) })
        viewModel.paginationStatus.observe(viewLifecycleOwner, Observer {
            infiniteRecyclerViewScrollListener.setHaveMoreData(it) })

        //Rated By You VM
        ratedViewModel.progressData.observe(viewLifecycleOwner, Observer {
                isLoading -> handleProgressBar(isLoading) })
        ratedViewModel.movieListData.observe(viewLifecycleOwner, Observer {
                movieList -> handleFeedList(movieList) })
        ratedViewModel.paginationStatus.observe(viewLifecycleOwner, Observer {
            infiniteRecyclerViewScrollListener.setHaveMoreData(it) })

        //Top Rated VM
        mostRatedViewModel.progressData.observe(viewLifecycleOwner, Observer {
                isLoading -> handleProgressBar(isLoading) })
        mostRatedViewModel.movieListData.observe(viewLifecycleOwner, Observer {
                movieList -> handleFeedList(movieList) })
        mostRatedViewModel.paginationStatus.observe(viewLifecycleOwner, Observer {
            infiniteRecyclerViewScrollListener.setHaveMoreData(it) })

        //Up coming VM
        upcomingViewModel.progressData.observe(viewLifecycleOwner, Observer {
                isLoading -> handleProgressBar(isLoading) })
        upcomingViewModel.movieListData.observe(viewLifecycleOwner, Observer {
                movieList -> handleFeedList(movieList) })
        upcomingViewModel.paginationStatus.observe(viewLifecycleOwner, Observer {
            infiniteRecyclerViewScrollListener.setHaveMoreData(it) })
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let {
            movieListFragPullToRefresh.isRefreshing = it
        }
    }

    private fun handleFeedList(movieList: ArrayList<Movie>?) {
        movieList?.let {
            adapter.submitList(it)
            infiniteRecyclerViewScrollListener.notifyDataLoaded()

            if (it.isEmpty()) {
                movieListFragNoMoviesText.visibility = View.VISIBLE
            } else {
                movieListFragNoMoviesText.visibility = View.GONE
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MovieListFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MovieListFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onMovieClick(movie: Movie) {
        listener?.onMovieClick(movie)
    }

    private fun getInfiniteScrollListener(): InfiniteRecyclerViewScrollListener {
        return object : InfiniteRecyclerViewScrollListener(this.layoutManager) {
            override fun onDataHunger() {}

            override fun requestData(offset: Int) {
                val page = 1 + offset / Constants.API_PAGINATION_ITEMS_PER_PAGE
                when (listType) {
                    Constants.MOVIE_LIST_ALL_MOVIES_TYPE -> {
                        viewModel.getAdditionalMovies(page)
                    }
                    Constants.MOVIE_LIST_RATED_MOVIES_TYPE -> {
                        ratedViewModel.getAdditionalMovies(page)
                    }
                    Constants.MOVIE_LIST_MOST_RATED_MOVIES_TYPE -> {
                        mostRatedViewModel.getAdditionalMovies(page)
                    }
                    Constants.MOVIE_LIST_UPCOMING_MOVIES_TYPE -> {
                        upcomingViewModel.getAdditionalMovies(page)
                    }
                }
            }
        }
    }

    fun setListType(listType: Int) {
        this.listType = listType
        initListByType(listType)
    }
}
