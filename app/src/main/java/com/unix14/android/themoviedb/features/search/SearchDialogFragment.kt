package com.unix14.android.themoviedb.features.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.common.InfiniteRecyclerViewScrollListener
import com.unix14.android.themoviedb.common.UiUtils
import com.unix14.android.themoviedb.features.movie_list.MovieListAdapter
import com.unix14.android.themoviedb.models.Movie
import kotlinx.android.synthetic.main.search_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchDialogFragment : DialogFragment(), TextView.OnEditorActionListener, TextWatcher,
    MovieListAdapter.MovieListAdapterListener {


    interface SearchFragmentListener {
        fun onMovieClick(movie: Movie)
    }

    private var listener: SearchFragmentListener? = null
    val viewModel by viewModel<SearchDialogViewModel>()
    private lateinit var infiniteRecyclerViewScrollListener: InfiniteRecyclerViewScrollListener
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MovieListAdapter

    companion object {
        fun newInstance() = SearchDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecycler()
        setupViewModel()
        initUi()
    }

    private fun initRecycler() {
        layoutManager = LinearLayoutManager(context)
        searchFragRecycler.layoutManager = layoutManager

        adapter = MovieListAdapter(this)

        infiniteRecyclerViewScrollListener = getInfiniteScrollListener()
        searchFragRecycler.addOnScrollListener(infiniteRecyclerViewScrollListener)

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(searchFragRecycler.context, R.drawable.divider)
        dividerDrawable?.let {
            dividerItemDecoration.setDrawable(it)
        }
        searchFragRecycler.addItemDecoration(dividerItemDecoration)

        searchFragRecycler.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel.progressData.observe(viewLifecycleOwner, Observer {
                isLoading -> handleProgressBar(isLoading) })
        viewModel.movieListData.observe(viewLifecycleOwner, Observer {
                movieList -> handleSearchResultList(movieList) })
        viewModel.paginationStatus.observe(viewLifecycleOwner, Observer {
            infiniteRecyclerViewScrollListener.setHaveMoreData(it) })

    }

    private fun handleSearchResultList(results: ArrayList<Movie>?) {
        results?.let {
            adapter.submitList(it)
            infiniteRecyclerViewScrollListener.notifyDataLoaded()

            if (it.isEmpty()) {
                searchFragRecycler.visibility = View.GONE
                searchFragNoMoviesText.visibility = View.VISIBLE
                searchFragNoMoviesText.text = getString(R.string.search_frag_no_results) + " '" +searchFragEditText.text.toString() +"'"
            } else {
                searchFragNoMoviesText.visibility = View.GONE
                searchFragRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun handleProgressBar(isLoading: Boolean?) {
        isLoading?.let {
            if (it) {
                searchFragPb.visibility = View.VISIBLE
            } else {
                searchFragPb.visibility = View.GONE
            }
        }
    }

    private fun initUi() {
        searchFragEditText.showSoftInputOnFocus = true
        searchFragEditText.requestFocus()
        searchFragEditText.setOnEditorActionListener(this)
        searchFragEditText.addTextChangedListener(this)

        searchFragCloseBtn.setOnClickListener {
            dismiss()
        }

        //ShowKeyboard
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        searchFragSearchBtn.setOnClickListener {
            performSearch(searchFragEditText.text.toString())

            //Close Keyboard
            activity?.let{
                UiUtils.closeKeyboard(it,searchFragEditText)
            }
        }

        dialog.window?.let{
//            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            val wlp :WindowManager.LayoutParams = it.attributes

            wlp.gravity = Gravity.TOP
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            it.attributes = wlp
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
        searchFragEditText.isActivated = query?.isNotEmpty()!!
        performSearch(query.toString())
    }

    override fun onEditorAction(tv: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) {

            tv?.let{
                //use tv.text.toString() to perform search
                performSearch(it.text.toString())

//                //Close Keyboard
                activity?.let{ ctx ->
                    UiUtils.closeKeyboard(ctx,tv)
                }
            }
            return true
        }
        return false
    }

    private fun performSearch(query: String) {
        //Use Vm to search with apiService
        viewModel.searchMovie(query)
    }

    override fun onMovieClick(movie: Movie) {
        listener?.onMovieClick(movie)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement SearchFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getInfiniteScrollListener(): InfiniteRecyclerViewScrollListener {
        return object : InfiniteRecyclerViewScrollListener(this.layoutManager) {
            override fun onDataHunger() {}

            override fun requestData(offset: Int) {
                val page = 1 + offset / Constants.API_PAGINATION_ITEMS_PER_PAGE
                val query = searchFragEditText.text.toString()
                viewModel.getMoreResults(query, page)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //Close Keyboard
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}
