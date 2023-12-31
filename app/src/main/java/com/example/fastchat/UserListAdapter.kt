package com.example.fastchat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fastchat.databinding.UserListBinding

class UserListAdapter(var context: Context, private var userList: ArrayList<UserListModel>): RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.UserViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserListAdapter.UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.userName.text = user.userName
        holder.binding.message.text = user.message

        Glide.with(context).load(user.profileImage)
            .placeholder(R.drawable.profile)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.binding.profile)
            holder.binding.profile.setBackgroundColor(Color.TRANSPARENT)
        "Say hello!".also { holder.binding.message.text = it }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("name", user.userName)
            intent.putExtra("profile", user.profileImage)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    inner class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding: UserListBinding = UserListBinding.bind(itemView)
    }
}