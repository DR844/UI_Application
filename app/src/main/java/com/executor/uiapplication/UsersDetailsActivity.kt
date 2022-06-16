package com.executor.uiapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_users_details.*
import kotlinx.android.synthetic.main.contact_detail.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UsersDetailsActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    var id: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_details)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val showIntent = mUserViewModel.getItemDetail(intent.getIntExtra("id", 0))

        toolbar?.setNavigationOnClickListener {
            finish()
        }


        showIntent.observe(this) {
            supportActionBar?.title = it.fName + " " + it.lName
            show_User_Email.text = it.email
            show_User_DOB.text = it.dob
            show_User_Number.text = it.number
            show_User_Age.text = it.age.toString()
            Glide.with(applicationContext).load(it.image).into(detail_pic)
            id = it.id
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater;
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                val intent = Intent(this, UserUpdateActivity::class.java)
                mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
                intent.putExtra("id", id)
                startActivity(intent)
                return true
            }
            R.id.menu_delete -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Are you Sure Delete this  ?")
                dialog.setPositiveButton("Delete") { _, _ ->
                    GlobalScope.launch {
                        mUserViewModel.deleteByUserId(intent.getIntExtra("id", 0))
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, " Delete", Toast.LENGTH_SHORT).show()

                }
                dialog.setNegativeButton("No") { _, _ ->
                    Toast.makeText(this, "Not Delete", Toast.LENGTH_SHORT).show()
                }
                dialog.show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

}