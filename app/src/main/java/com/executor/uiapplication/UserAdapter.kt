package com.executor.uiapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.executor.uiapplication.db.UserEntity
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter(private val context: Context, private var listener: RowClickListener) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    private var moMyUser = emptyList<UserEntity>()

    fun setListData(data: List<UserEntity>) {
        this.moMyUser = data
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View, private val listener: RowClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val image = itemView.User_Avatar
        val fName = itemView.tvFName
        val lName = itemView.tvLName
        val number = itemView.tvNumber
        val email = itemView.tvEmail
        val age = itemView.tvAge
        val deleteUserId = itemView.ibDelete
        val updateId = itemView.ibEdit

        fun bind(data: UserEntity) {
            deleteUserId.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
            updateId.setOnClickListener {
                listener.onUpdateClickListener(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val loItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return MyViewHolder(loItemView, listener)
    }

    override fun onBindViewHolder(holder: UserAdapter.MyViewHolder, position: Int) {
        val loUserInfo = moMyUser[position]
        holder.fName.text = loUserInfo.fName
        holder.lName.text = loUserInfo.lName
        holder.email.text = loUserInfo.email
        holder.age.text = loUserInfo.age.toString()
        holder.number.text = loUserInfo.number
        Glide.with(context).load(loUserInfo.image).circleCrop().placeholder(R.drawable.ic_user)
            .into(holder.image)
        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickListener(moMyUser[position])
            }
        }
        holder.bind(moMyUser[position])
    }

    override fun getItemCount(): Int {
        return moMyUser.size
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(userEntity: UserEntity)
        fun onItemClickListener(userEntity: UserEntity)
        fun onUpdateClickListener(userEntity: UserEntity)
    }

}