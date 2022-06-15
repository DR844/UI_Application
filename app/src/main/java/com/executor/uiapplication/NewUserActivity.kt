package com.executor.uiapplication

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.executor.uiapplication.db.UserDatabase
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.activity_users_details.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewUserActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel
    private var myAge = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        // initialize views
        setSupportActionBar(add_toolbar)
        supportActionBar?.title = "Add User Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        add_toolbar?.setNavigationOnClickListener {
            finish()
        }

        val myCalender = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatable(myCalender)
        }

        DOB.setOnClickListener {
            val dialog = DatePickerDialog(
                this,
                datePicker,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = myCalender.timeInMillis
            dialog.show()
        }

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        Add_User_Cancel.setOnClickListener {
            finish()
        }
        Add_User_Save.setOnClickListener {
            insertDataToDatabase()
        }

    }


    private fun updatable(myCalender: Calendar) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val birthYear = myCalender.get(Calendar.YEAR)
        myAge = currentYear - birthYear
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        DOB.text = sdf.format(myCalender.time)
    }

    private fun insertDataToDatabase() {

        val fName = First_Name.text.toString()
        val lName = Last_Name.text.toString()
        val email = Emails.text.toString()
        val dob = DOB.text.toString()
        val number = Phone_Number.text.toString()

        if (fName.isEmpty()) {
            First_Name.error = "cannot be empty"
            return
        }
        if (lName.isEmpty()) {
            Last_Name.error = "cannot be Empty"
            return
        }
        if (number.isEmpty()) {
            Phone_Number.error = "cannot be Empty"
            return
        }
        if (email.isEmpty()) {
            Emails.error = "cannot be Empty"
            return
        }

        if (!TextUtils.isEmpty(dob)) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, " Invalid Email Format", Toast.LENGTH_SHORT).show()
            } else {
                if (UserDatabase.getDatabase(this).userDao().isEmailExist(email) == 0) {
                    val user =
                        UserEntity(
                            0,
                            "hi",
                            fName,
                            lName,
                            email,
                            dob,
                            myAge,
                            number,
                            Date()
                        )
                    GlobalScope.launch {
                        mUserViewModel.insertUser(user)
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this, "Successfully Added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "This id Already Exists", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Please fill out DOB Fields ",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}