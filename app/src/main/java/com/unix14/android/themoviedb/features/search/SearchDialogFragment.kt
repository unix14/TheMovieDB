package com.unix14.android.themoviedb.features.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.UiUtils
import kotlinx.android.synthetic.main.search_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchDialogFragment : DialogFragment(), TextView.OnEditorActionListener, TextWatcher {

    val viewModel by viewModel<SearchDialogViewModel>()

    companion object {
        fun newInstance() = SearchDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initUi()
    }

    private fun initUi() {
        searchFragEditText.requestFocus()
        searchFragEditText.setOnEditorActionListener(this)
        searchFragEditText.addTextChangedListener(this)

        searchFragSearchBtn.setOnClickListener {
            performSearch(searchFragEditText.text.toString())
        }

        dialog.window?.let{
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            val wlp :WindowManager.LayoutParams = it.attributes

            wlp.gravity = Gravity.TOP
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            it.attributes = wlp
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        searchFragEditText.isActivated = p0?.isNotEmpty()!!
    }

    override fun onEditorAction(tv: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            tv?.let{
                //use tv.text.toString() to perform search
                performSearch(it.text.toString())
            }
            return true
        }
        return false
    }

    private fun performSearch(query: String) {
        Toast.makeText(context,query,Toast.LENGTH_LONG).show()



        //Use Vm to search with apiService
        viewModel.searchMovie(query)

        //Close Keyboard
        context?.let{
            UiUtils.closeKeyboard(it,dialog.window!!.decorView)
        }
    }
}
