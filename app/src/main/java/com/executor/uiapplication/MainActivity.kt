package com.executor.uiapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UserAdapter.RowClickListener {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        add_User.setOnClickListener {
            val intent = Intent(this, NewUserActivity::class.java)
            startActivity(intent)
        }


        val adapter = UserAdapter(this@MainActivity, this@MainActivity)
        Recycler_View.adapter = adapter
        Recycler_View.layoutManager = LinearLayoutManager(this@MainActivity)

        mUserViewModel = ViewModelProvider(this@MainActivity)[UserViewModel::class.java]

        mUserViewModel.getAllUser.observe(this@MainActivity) {
            adapter.setListData(it)
        }

    }

    override fun onDeleteUserClickListener(userEntity: UserEntity) {
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Are you sure delete  ${userEntity.fName}?")
        dialog.setPositiveButton("Yes") { _, _ ->
            GlobalScope.launch {
                mUserViewModel.deleteUser(userEntity)
            }
            Toast.makeText(this, userEntity.fName, Toast.LENGTH_SHORT).show()
        }
        dialog.setNegativeButton("No") { _, _ ->
            Toast.makeText(this, "Not Delete", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    override fun onItemClickListener(userEntity: UserEntity) {
        val intent = Intent(this@MainActivity, UsersDetailsActivity::class.java)
        intent.putExtra("id", userEntity.id)
        startActivity(intent)
    }

    override fun onUpdateClickListener(userEntity: UserEntity) {
        val intent = Intent(this@MainActivity, UserUpdateActivity::class.java)
        intent.putExtra("id", userEntity.id)
        intent.putExtra("dob", userEntity.dob)
        startActivity(intent)
    }
}