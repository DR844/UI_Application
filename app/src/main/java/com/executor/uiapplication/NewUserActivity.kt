package com.executor.uiapplication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.executor.uiapplication.db.UserDatabase
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import com.executor.uiapplication.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NewUserActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    private val CAMERA_REQUEST = R.string.CAMERA_REQUEST
    private val STORAGE_REQUEST = R.string.STORAGE_REQUEST

    lateinit var msCameraPermission: Array<String>
    private var mPhotoPath: String? = ""
    private var myAge = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        msCameraPermission =
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        // initialize views
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.add_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar?.setNavigationOnClickListener {
            finish()
        }


        fab_add_photo.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                showDialog()
            }
        }

        val loMyCalender = Calendar.getInstance()

        val loDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            loMyCalender.set(Calendar.YEAR, year)
            loMyCalender.set(Calendar.MONTH, month)
            loMyCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatable(loMyCalender)
        }

        DOB.setOnClickListener {
            val loDialog = DatePickerDialog(
                this,
                loDatePicker,
                loMyCalender.get(Calendar.YEAR),
                loMyCalender.get(Calendar.MONTH),
                loMyCalender.get(Calendar.DAY_OF_MONTH)
            )
            loDialog.datePicker.maxDate = loMyCalender.timeInMillis
            loDialog.show()
        }

        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        Add_User_Cancel.setOnClickListener {
            finish()
        }
        Add_User_Save.setOnClickListener {
            insertDataToDatabase()
        }

//        First_Name.setOnClickListener {
//            llAddImg.setColorFilter(R.drawable.ic_user)
//        }
//        First_Name.onFocusChangeListener.onFocusChange(View(this),false){
//            if(First_Name.isClickable){
//
//            }
//        }
//        First_Name.addTextChangedListener {
//            llAddImg.setColorFilter(R.drawable.ic_user_grey)
//        }
    }

    private fun showDialog() {
        val loAlertDialog = AlertDialog.Builder(this)
        loAlertDialog.apply {
            setTitle(getString(R.string.camera_option))
            setPositiveButton(getString(R.string.CAMERA)) { _, _ ->
                takePicture()
            }
            setNegativeButton(getString(R.string.GALLERY)) { _, _ ->
                val liPickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    liPickPhoto,
                    1
                ) //one can be replaced with any action code
            }
            setNeutralButton(getString(R.string.CANCEL)) { _, _ ->

            }
        }.create().show()
    }

    private fun takePicture() {
        val liIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (liIntent.resolveActivity(packageManager) != null) {
            var loPhotofile: File? = null
            try {
                loPhotofile = createImageFile()
            } catch (e: IOException) {
            }
            if (loPhotofile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    getString(R.string.photoUri),
                    loPhotofile
                )
                liIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(liIntent, 0)
            }

        }
    }

    private fun createImageFile(): File? {
        val lsFilename = getString(R.string.filename)
        val loStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val loImage = File.createTempFile(lsFilename, getString(R.string.image_ext), loStorageDir)
        mPhotoPath = loImage.absolutePath
        return loImage
    }

    private fun requestCameraPermission() {
        requestPermissions(msCameraPermission, STORAGE_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val lbResult = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        val lbResult2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return lbResult && lbResult2

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK) {
//                ivProfile.rotation = 90f
                Glide.with(this).load(mPhotoPath).circleCrop().into(ivProfile)
            }
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = imageReturnedIntent?.data
                mPhotoPath = selectedImage.toString()
                Glide.with(this).load(mPhotoPath).circleCrop().into(ivProfile)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val camera_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED)
                    val storage_accepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED)
                    if (camera_accepted && storage_accepted) {
                        showDialog()
                    }
                }
            }
        }
    }


    private fun updatable(myCalender: Calendar) {
        val loCurrentYear = Calendar.getInstance().get(Calendar.YEAR)
        val loBirthYear = myCalender.get(Calendar.YEAR)
        myAge = loCurrentYear - loBirthYear
        val lsMyFormat = getString(R.string.date_format)
        val loSdf = SimpleDateFormat(lsMyFormat, Locale.UK)
        DOB.text = loSdf.format(myCalender.time)
    }

    private fun insertDataToDatabase() {

        val fName = First_Name.text.toString()
        val lName = Last_Name.text.toString()
        val email = Emails.text.toString()
        val dob = DOB.text.toString()
        val number = Phone_Number.text.toString()


        if (fName.isEmpty()) {
            First_Name.error = getString(R.string.error)
            return
        }
        if (lName.isEmpty()) {
            Last_Name.error = getString(R.string.error)
            return
        }
        if (number.isEmpty()) {
            Phone_Number.error = getString(R.string.error)
            return
        }
        if (email.isEmpty()) {
            Emails.error = getString(R.string.error)
            return
        }

        if (!TextUtils.isEmpty(dob)) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, getString(R.string.email_check), Toast.LENGTH_SHORT).show()
            } else {
                if (UserDatabase.getDatabase(this).userDao().isEmailExist(email) == 0) {
                    val user =
                        UserEntity(
                            0,
                            mPhotoPath!!,
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

                    val liIntent = Intent(this, MainActivity::class.java)

                    val loading = LoadingDialog(this)
                    loading.startLoading()
                    val handler = Handler()
                    handler.postDelayed({ loading.isDismiss() }, 2000)

                    startActivity(liIntent)

                    Toast.makeText(this, getString(R.string.success_msg), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.email_exists), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.dob_check),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }
}
