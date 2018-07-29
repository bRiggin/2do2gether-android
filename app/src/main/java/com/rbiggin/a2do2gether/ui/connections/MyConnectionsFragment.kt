package com.rbiggin.a2do2gether.ui.connections

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.main.IntMainListener
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

import kotlinx.android.synthetic.main.fragment_my_connections.*

/**
 * Fragment that allows user to control the people they're connected to.
 */
class MyConnectionsFragment : BaseFragment(), MyConnectionsPresenter.View,
                                              MyConnectionsPresenter.Button,
                                              IntMainListener{

    /** Injected Presenter instance */
    @Inject lateinit var presenter: IntMyConnectionsPresenter

    /** Injected Presenter instance */
    @Inject lateinit var constants: Constants

    /** injected instance of Constants. */
    @Inject lateinit var utilities: Utilities

    /** ... */
    private lateinit var mSearchLayoutManager: LinearLayoutManager
    private lateinit var mPendingLayoutManager: LinearLayoutManager

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        fun newInstance(id: Int): MyConnectionsFragment {
            val fragment = MyConnectionsFragment()
            val args = Bundle()
            args.putInt("FRAGMENT_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_connections, container, false)
    }

    override fun onStart() {
        super.onStart()

        presenter.setView(this)
        presenter.onViewWillShow()

        mSearchLayoutManager = LinearLayoutManager(mContext)
        mPendingLayoutManager = LinearLayoutManager(mContext)
        myConnectionsSearchRv.layoutManager = mSearchLayoutManager
        pendingConnectionsRv.layoutManager = mPendingLayoutManager

        searchBtn.setOnClickListener {
            presenter.onSearchButtonPressed(myConnectionsSearchEt.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()
    }

    override fun onDisplayView(view: Constants.MyConnectionView) {
        when(view){
            constants.connectionsMainView() -> {
                myConnectionsViewFlipper.displayedChild = myConnectionsViewFlipper.indexOfChild(myConnectionsMainView)
            }
            constants.connectionsSearchView() -> {
                myConnectionsViewFlipper.displayedChild = myConnectionsViewFlipper.indexOfChild(myConnectionsSearchView)
            }
            else -> {
                throw IllegalArgumentException("MyConnectionsFragment, displayView: function handed illegal view: $view.")
            }
        }
    }

    override fun onDisplaySearchResults(result: ArrayList<UserConnectionSearch>) {
        mContext?.let{
            myConnectionsSearchRv.adapter = ConnectionSearchAdapter(result, it, constants, this)
        }
    }

    override fun onDisplayConnectionRequests(requests: ArrayList<UserConnectionRequest>) {
        pendingConnectionsRv.adapter = ConnectionRequestsAdapter(requests, constants, this)
    }

    override fun onClearSearchView() {
        myConnectionsSearchEt.text.clear()
        myConnectionsSearchRv.adapter = null
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        var dialogMessage = getString(R.string.error_unknown)
        message?.let{ dialogMessage = message }

        val messageString: String = when (message_id){
            constants.ERROR_NICKNAME_STRUCTURE_ERROR -> {
                getString(R.string.nickname_error, constants.NUMBER_OF_CHARACTERS_IN_NICKNAME)
            }
            constants.ERROR_USER_NOT_PUBLIC -> {
                getString(R.string.connections_user_not_public)
            }
            constants.DB_CONNECTION_REQUEST_SUBMITTED -> {
                getString(R.string.connection_request_submitted)
            }
            else -> { getString(R.string.error_unknown)
                throw IllegalArgumentException("MyConnectionsFragment, onDisplayDialogMessage: An " +
                        "unknown error has been handed to this function. Error ID: $message_id")
            }

        }
        utilities.showOKDialog(activity as Context, getString(R.string.app_name), messageString)
    }

    /**
     *
     */
    override fun menuItemPressed(item: Constants.MenuBarItem) {
        if (item == constants.menuBarItemPlus()){
            presenter.onPlusButtonPressed()
        } else {
            throw IllegalArgumentException("MyConnectionsFragment, menuItemPressed: function handed illegal item: $item.")
        }
    }

    /**
     * Display Progress Spinner
     */
    override fun displayProgressSpinner(show: Boolean) {
        if (show) {
            hideKeyboard()
            connectionsProgressSpinner.visibility = View.VISIBLE
        } else {
            connectionsProgressSpinner.visibility = View.GONE
        }
    }

    /**
     * Display No Results Found
     */
    override fun displayNoResultsFound(show: Boolean) {
        if (show) {
            connectionsNoResultsFound.visibility = View.VISIBLE
        } else {
            connectionsNoResultsFound.visibility = View.GONE
        }
    }

    /**
     * Hide keyboard
     */
    private fun hideKeyboard(){
        val view = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * onBackPressed notification from main activity.
     *
     * If returned true, main activity will perform super.onBackPressed.
     */
    override fun backPressed(): Boolean {
        return presenter.onMainActivityBackPressed()
    }

    /**
     * Recycler View Button Clicked
     */
    override fun onRecyclerViewButtonClicked(type: Constants.ConnectionsActionType, targetUid: String) {
        presenter.onRecyclerViewButtonPressed(type, targetUid)
    }
}

