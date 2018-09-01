package com.rbiggin.a2do2gether.ui.connections

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_connections_search.view.*

class ConnectionSearchAdapter(private val searchResults: ArrayList<UserConnectionSearch>,
                              private val appContext: Context,
                              private val listener: MyConnectionsPresenter.Button,
                              private val fragment: MyConnectionsFragment)
                              : RecyclerView.Adapter<ConnectionSearchAdapter.ItemHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_connections_search, false)
        return ItemHolder(inflatedView, appContext, fragment, listener)
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
                     private val fragment: MyConnectionsFragment,
                     private val listener: MyConnectionsPresenter.Button)
                     : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var onClickEnabled = false
        private val enabledButton: Drawable = mContext.getDrawable(R.drawable.layout_button)
        private val disabledButton: Drawable = mContext.getDrawable(R.drawable.layout_container)
        private val selfString: String = mContext.getString(R.string.connect_self)
        private val existingString: String = mContext.getString(R.string.connect_existing)
        private var uid: String? = null

        private var path: StorageReference? = null
        private val mStorage = FirebaseStorage.getInstance()
        private val mStorageRef = mStorage.reference

        init {
            view.connectUserBtn.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id){
                R.id.connectUserBtn -> {
                    if (onClickEnabled){
                        uid?.apply {
                            listener.onRecyclerViewButtonClicked(MyConnectionsPresenter.Action.CONNECTION_REQUEST,
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

        fun setName(userName: String){
            view.userName.text = userName
        }

        fun setNickname(nickname: String){
            view.userNickname.text = nickname
        }

        fun setUid(uid: String){
            this.uid = uid
            this.path = mStorageRef.child("profile_pictures/${this.uid}.jpg")
            this.path?.let {
                setImage(it)
            }
        }

        private fun setImage(reference: StorageReference){
            Glide.with(fragment)
                    .using(FirebaseImageLoader())
                    .load(reference)
                    .error(R.drawable.profile_default)
                    .into(view.userProfilePicture)
        }

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