package com.executor.uiapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_users_details.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class UsersDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_details)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add User Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }
}