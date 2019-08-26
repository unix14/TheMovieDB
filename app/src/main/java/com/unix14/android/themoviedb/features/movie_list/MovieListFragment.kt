package com.unix14.android.themoviedb.features.movie_list

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.unix14.android.themoviedb.common.InfiniteRecyclerViewScrollListener
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.movie_list_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MovieListFragment : Fragment(), MovieListAdapter.MovieListAdapterListener {

    interface MovieListFragmentListener{
        fun onMovieClick(movieId: Movie)
    }

    companion object {
        fun newInstance() = MovieListFragment()
    }

    private var listener: MovieListFragmentListener? = null
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MovieListAdapter
    val viewModel by viewModel<MovieListViewModel>()
    private lateinit var infiniteRecyclerViewScrollListener: InfiniteRecyclerViewScrollListener
    private var page: Int = 1

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.movie_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.getMovieList(page)
    }

    private fun initFloatingActionButton() {
        movieListFragScrollToTopButton.setOnClickListener {
            movieListFragRecycler.smoothScrollToPosition(0)
        }

        movieListFragRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy<0 && !movieListFragScrollToTopButton.isShown)
                    movieListFragScrollToTopButton.show()
                else if(dy>0 && movieListFragScrollToTopButton.isShown)
                    movieListFragScrollToTopButton.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    movieListFragScrollToTopButton.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun initSwipeLayout() {
        movieListFragPullToRefresh.setColorSchemeColors(Color.BLUE, Color.RED, Color.BLACK)
        movieListFragPullToRefresh.setOnRefreshListener {
            page=1
            adapter.clear()
            viewModel.getMovieList(page)
        }
    }

    private fun setupViewModel() {
        viewModel.progressData.observe(viewLifecycleOwner, Observer {
            isLoading -> handleProgressBar(isLoading) })
        viewModel.movieListData.observe(viewLifecycleOwner, Observer {
                movieList -> handleFeedList(movieList) })
        viewModel.paginationStatus.observe(viewLifecycleOwner, Observer {
             infiniteRecyclerViewScrollListener.setHaveMoreData(it) })
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let{
            movieListFragPullToRefresh.isRefreshing = it
        }
    }

    private fun handleFeedList(movieList: ArrayList<Movie>?) {
        movieList?.let{
            if(it.isNotEmpty()){
                adapter.updateList(it)
                infiniteRecyclerViewScrollListener.notifyDataLoaded()
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
        listener?.let{
            it.onMovieClick(movie)
        }
    }

    private fun getInfiniteScrollListener(): InfiniteRecyclerViewScrollListener {
        return object : InfiniteRecyclerViewScrollListener(this.layoutManager) {
            override fun onDataHunger() {}

            override fun requestData(page: Int) {
                viewModel.getAdditionalMovies(page)
            }
        }
    }
}
