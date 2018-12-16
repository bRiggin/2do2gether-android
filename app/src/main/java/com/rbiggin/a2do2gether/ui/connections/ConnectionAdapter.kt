package com.rbiggin.a2do2gether.ui.connections

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.View
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_connection.view.*

class ConnectionAdapter (private val connections: ArrayList<UserDetails>,
                         private val fragment: MyConnectionsFragment)
                         : RecyclerView.Adapter<ConnectionAdapter.ItemHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_connection, false)
        return ItemHolder(inflatedView, fragment)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = connections[position]
        val firstName = item.firstName
        val secondName = item.secondName
        val nickname = item.nickname
        holder.setName("$firstName $secondName")
        holder.setNickname(nickname)
        holder.setProfilePicture(item.uid)
    }

    override fun getItemCount(): Int {
        return connections.size
    }

    class ItemHolder(v: View, private val fragment: MyConnectionsFragment)
        : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var uid: String? = null

        private var path: StorageReference? = null
        private val mStorage = FirebaseStorage.getInstance()
        private val mStorageRef = mStorage.reference

        fun setName(userName: String){
            view.userName.text = userName
        }

        fun setNickname(nickname: String){
            view.userNickname.text = nickname
        }

        fun setProfilePicture(uid: String){
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
    }
}