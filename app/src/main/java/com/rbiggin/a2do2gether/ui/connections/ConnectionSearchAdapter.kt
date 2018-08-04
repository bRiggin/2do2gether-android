package com.rbiggin.a2do2gether.ui.connections

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_connections_search.view.*

class ConnectionSearchAdapter(private val searchResults: ArrayList<UserConnectionSearch>,
                              private val appContext: Context,
                              private val listener: MyConnectionsPresenter.Button)
                              : RecyclerView.Adapter<ConnectionSearchAdapter.ItemHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_connections_search, false)
        return ItemHolder(inflatedView, appContext, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = searchResults[position]
        val firstName = item.firstName
        val secondName = item.secondName
        val nickname = item.nickname
        holder.setName("$firstName $secondName")
        holder.setNickname(nickname)
        holder.setUid(item.uid)
        holder.setupButton(item.type)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    class ItemHolder(v: View, mContext: Context,
                     private val listener: MyConnectionsPresenter.Button)
        : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var onClickEnabled = false
        private val enabledButton: Drawable = mContext.getDrawable(R.drawable.layout_button)
        private val disabledButton: Drawable = mContext.getDrawable(R.drawable.layout_container)
        private val selfString: String = mContext.getString(R.string.connect_self)
        private val existingString: String = mContext.getString(R.string.connect_existing)
        private var uid: String? = null


        init {
            view.connectUserBtn.setOnClickListener(this)
        }

        /**
         * onClick
         */
        override fun onClick(v: View) {
            when (v.id){
                R.id.connectUserBtn -> {
                    if (onClickEnabled){
                        uid?.apply {
                            listener.onRecyclerViewButtonClicked(Constants.ConnectionsActionType.CONNECTION_REQUEST,
                                    this)
                        }
                    }
                }
                else -> {
                    //todo add comment to exception
                    throw IllegalArgumentException()
                }
            }
        }

        /**
         * setName
         */
        fun setName(userName: String){
            view.userName.text = userName
        }

        /**
         * setNickname
         */
        fun setNickname(nickname: String){
            view.userNickname.text = nickname
        }

        /**
         * setUid
         */
        fun setUid(uid: String){
            this.uid = uid
        }

        /**
         * setupButton
         */
        fun setupButton(type: Constants.ConnectionsSearchResult){
            when (type){
                Constants.ConnectionsSearchResult.SELF -> {
                    onClickEnabled = false
                    view.connectUserBtn.background = disabledButton
                    view.connectUserBtn.text = selfString
                }
                Constants.ConnectionsSearchResult.EXISTING_CONNECTION -> {
                    onClickEnabled = false
                    view.connectUserBtn.background = disabledButton
                    view.connectUserBtn.text = existingString
                }
                Constants.ConnectionsSearchResult.NEW_CONNECTION -> {
                    onClickEnabled = true
                    view.connectUserBtn.background = enabledButton
                }
            }
        }
    }
}