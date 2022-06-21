package com.executor.uiapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.executor.uiapplication.db.UserViewModel
import com.executor.uiapplication.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_users_details.*
import kotlinx.android.synthetic.main.contact_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UsersDetailsActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel
    var miId: Int = 0
    var msDateOfBirth: String? = ""
    private var msPhotoPath: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_details)

        setSupportActionBar(detailToolbar)


        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val liShowUser = mUserViewModel.getItemDetail(intent.getIntExtra(getString(R.string.id), 0))

        detailToolbar?.setNavigationOnClickListener {
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


        liShowUser.observe(this) {
            supportActionBar?.title = it.fName + " " + it.lName
            show_User_Email.text = it.email
            show_User_DOB.text = it.dob
            show_User_Number.text = it.number
            show_User_Age.text = it.age.toString()
            Glide.with(applicationContext).load(it.image).into(detail_pic)
            miId = it.id
            msDateOfBirth = it.dob
            msPhotoPath = it.image

            if (msPhotoPath == "") {
                Glide.with(applicationContext).load(R.drawable.profile).into(detail_pic)
                Log.d("Test", "Empty");
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val lInflater = menuInflater;
        lInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                val liIntent = Intent(this, UserUpdateActivity::class.java)

                val loading = LoadingDialog(this)
                loading.startLoading()
                val handler = Handler()
                handler.postDelayed({ loading.isDismiss() }, 1000)

                liIntent.putExtra(getString(R.string.id), miId)
                liIntent.putExtra(getString(R.string.dob), msDateOfBirth)
                startActivity(liIntent)
                return true
            }
            R.id.menu_delete -> {
                val lDialog = AlertDialog.Builder(this)
                lDialog.setTitle(getString(R.string.delete_title))
                lDialog.setPositiveButton(getString(R.string.Yes)) { _, _ ->
                    GlobalScope.launch {
                        mUserViewModel.deleteByUserId(intent.getIntExtra(getString(R.string.id), 0))
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    val lLoading = LoadingDialog(this)
                    lLoading.startLoading()
                    val lHandler = Handler()
                    lHandler.postDelayed({ lLoading.isDismiss() }, 2000)

//                    Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show()
                }
                lDialog.setNegativeButton(getString(R.string.No)) { _, _ ->
//                    Toast.makeText(this, "Not Delete", Toast.LENGTH_SHORT).show()
                }
                lDialog.show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

}
