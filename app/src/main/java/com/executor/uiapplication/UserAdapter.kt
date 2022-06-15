package com.executor.uiapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.executor.uiapplication.db.UserEntity
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter(private val context: Context, private var listener: RowClickListener) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    private var myUser = emptyList<UserEntity>()

    fun setListData(data: List<UserEntity>) {
        this.myUser = data
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View, private val listener: RowClickListener) :
        RecyclerView.ViewHolder(itemView) {
        //        val image = itemView.civImage
        val fName = itemView.tvFName
        val lName = itemView.tvLName
        val number = itemView.tvNumber
        //        val email = itemView.Emails
        val age = itemView.tvAge
        val deleteUserId = itemView.ibDelete
        fun bind(data: UserEntity) {
            deleteUserId.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return MyViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: UserAdapter.MyViewHolder, position: Int) {
        val userInfo = myUser[position]
        holder.fName.text = userInfo.fName
        holder.lName.text = userInfo.lName
//        holder.email.text = userInfo.email
        holder.age.text = userInfo.age.toString()
        holder.number.text = userInfo.number
        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickListener(myUser[position])
            }
        }
        holder.bind(myUser[position])
    }

    override fun getItemCount(): Int {
        return myUser.size
    }

    interface RowClickListener {
        fun onDeleteUserClickListener(userEntity: UserEntity)
        fun onItemClickListener(userEntity: UserEntity)
    }

}