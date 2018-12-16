package com.rbiggin.a2do2gether.ui.connections

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_connection_request.view.*
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ConnectionRequestsAdapter (private val requests: ArrayList<UserConnectionRequest>,
                                 private val listener: MyConnectionsPresenter.Button,
                                 private val fragment: MyConnectionsFragment)
                                 : RecyclerView.Adapter<ConnectionRequestsAdapter.ItemHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_connection_request, false)
        return ItemHolder(inflatedView, listener, fragment)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = requests[position]
        val firstName = item.firstName
        val secondName = item.secondName
        val nickname = item.nickname
        holder.setName("$firstName $secondName")
        holder.setNickname(nickname)
        holder.setUid(item.uid)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    class ItemHolder(private val view: View,
                     private val listener: MyConnectionsPresenter.Button,
                     private val fragment: MyConnectionsFragment):
                     RecyclerView.ViewHolder(view), View.OnClickListener {

        private var uid: String? = null
        private var path: StorageReference? = null
        private val mStorage = FirebaseStorage.getInstance()
        private val mStorageRef = mStorage.reference

        init {
            view.requestAcceptBtn.setOnClickListener(this)
            view.requestRejectBtn.setOnClickListener(this)
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
                    .into(view.userProfileImage)
        }

        override fun onClick(v: View) {
            when (v.id){
                R.id.requestAcceptBtn -> {
                    uid?.let {
                        listener.onRecyclerViewButtonClicked(MyConnectionsPresenter.Action.ACCEPT_CONNECTION_REQUEST, it)
                    }
                }
                R.id.requestRejectBtn -> {
                    uid?.let {
                        listener.onRecyclerViewButtonClicked(MyConnectionsPresenter.Action.REJECT_CONNECTION_REQUEST, it)
                    }
                }
                else -> {
                    throw IllegalArgumentException("Unrecognised button View.id handed to onClick in ConnectionRequestAdapter")
                }
            }
        }
    }
}