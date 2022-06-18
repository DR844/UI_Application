package com.executor.uiapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import com.executor.uiapplication.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_users_details.*
import kotlinx.android.synthetic.main.contact_detail.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UsersDetailsActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    var id: Int = 0
    var dateOfBirth: String? = null


    @SuppressLint("WrongViewCast")
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


//        llPhone.setOnClickListener {
////            llPhone.setBackgroundColor(R.drawable.phone_icon_focused)
//            val toggle: ToggleButton = findViewById(R.id.llPhone)
//            toggle.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    // The toggle is enabled
//                    llPhone.setBackgroundColor(R.drawable.ic_phone_icon)
//                } else {
//                    // The toggle is disabled
//                    llPhone.setBackgroundColor(R.drawable.ic_phone)
//                }
//            }
////            llPhone.showDividers
//        }


        showIntent.observe(this) {
            supportActionBar?.title = it.fName + " " + it.lName
            show_User_Email.text = it.email
            show_User_DOB.text = it.dob
            show_User_Number.text = it.number
            show_User_Age.text = it.age.toString()
            Glide.with(applicationContext).load(it.image).into(detail_pic)
            id = it.id
            dateOfBirth = it.dob
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

                val loading = LoadingDialog(this)
                loading.startLoading()
                val handler = Handler()
                handler.postDelayed({ loading.isDismiss() }, 1000)

                intent.putExtra("id", id)
                intent.putExtra("dob", dateOfBirth)
                startActivity(intent)
                return true
            }
            R.id.menu_delete -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Are you sure delete?")
                dialog.setPositiveButton("Yes") { _, _ ->
                    GlobalScope.launch {
                        mUserViewModel.deleteByUserId(intent.getIntExtra("id", 0))
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    val loading = LoadingDialog(this)
                    loading.startLoading()
                    val handler = Handler()
                    handler.postDelayed({ loading.isDismiss() }, 2000)

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
