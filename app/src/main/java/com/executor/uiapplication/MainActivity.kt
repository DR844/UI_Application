package com.executor.uiapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Boolean.TYPE

class MainActivity : AppCompatActivity(), UserAdapter.RowClickListener {
    private lateinit var mUserViewModel: UserViewModel
    var TYPES = "type"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        add_User.setOnClickListener {
            // add new contact
            val intent = Intent(this, NewUserActivity::class.java)
//            intent.putExtra(TYPES, 0)
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
        dialog.setTitle("Are you Sure Delete this ${userEntity.fName} ?")
        dialog.setPositiveButton("Delete") { _, _ ->
            GlobalScope.launch {
                mUserViewModel.deleteUser(userEntity)
            }
            Toast.makeText(this, "${userEntity.fName} Delete", Toast.LENGTH_SHORT).show()
        }
        dialog.setNegativeButton("No") { _, _ ->
            Toast.makeText(this, "Not Delete", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    override fun onItemClickListener(userEntity: UserEntity) {
        val intent = Intent(this@MainActivity, UsersDetailsActivity::class.java)
//        intent.putExtra("User", userEntity[position])
        intent.putExtra("id", userEntity.id)
        intent.putExtra("fname", userEntity.fName)
        intent.putExtra("lname", userEntity.lName)
        intent.putExtra("email", userEntity.email)
        intent.putExtra("dob", userEntity.dob)
        intent.putExtra("number", userEntity.number)
        intent.putExtra("img", userEntity.image)
        startActivity(intent)
    }
}