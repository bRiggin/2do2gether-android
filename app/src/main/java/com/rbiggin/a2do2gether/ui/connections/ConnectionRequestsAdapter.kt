package com.rbiggin.a2do2gether.ui.connections

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_connection_request.view.*

class ConnectionRequestsAdapter (private val requests: ArrayList<UserConnectionRequest>,
                                 private val constants: Constants,
                                 private val listener: IntMyConnectionsRecyclerButton)
                                 : RecyclerView.Adapter<ConnectionRequestsAdapter.ItemHolder>(){

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_connection_request, false)
        return ItemHolder(inflatedView, constants, listener)
    }

    /**
     * getItemCount
     */
    override fun getItemCount(): Int {
        return requests.size
    }

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = requests[position]
        val firstName = item.firstName
        val secondName = item.secondName
        val nickname = item.nickname
        holder.setName("$firstName $secondName")
        holder.setNickname(nickname)
        holder.setUid(item.uid)
    }

    /**
     * ItemHolder
     */
    class ItemHolder(private val view: View,
                     private val constants: Constants,
                     private val listener: IntMyConnectionsRecyclerButton): RecyclerView.ViewHolder(view), View.OnClickListener {

        private var uid: String? = null


        init {
            view.requestAcceptBtn.setOnClickListener(this)
            view.requestRejectBtn.setOnClickListener(this)
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
         * onClick
         */
        override fun onClick(v: View) {
            when (v.id){
                R.id.requestAcceptBtn -> {
                    uid?.let {
                        listener.onRecyclerViewButtonClicked(constants.connectionsActionAccept(), it)
                    }
                }
                R.id.requestRejectBtn -> {
                    uid?.let {
                        listener.onRecyclerViewButtonClicked(constants.connectionsActionAccept(), it)
                    }
                }
                else -> {
                    throw IllegalArgumentException("Unrecognised button view.id handed to onClick in ConnectionRequestAdapter")
                }
            }
        }
    }
}