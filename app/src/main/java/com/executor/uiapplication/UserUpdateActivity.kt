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
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import com.executor.uiapplication.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.activity_user_update.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserUpdateActivity : AppCompatActivity() {

    private lateinit var mUserViewModel: UserViewModel

    private val CAMERA_REQUEST = (R.string.CAMERA_REQUEST)
    private val STORAGE_REQUEST = (R.string.STORAGE_REQUEST)

    lateinit var msCameraPermission: Array<String>


    private var msPhotoPath: String? = ""

    companion object {
        private var miMyAge = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_update)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.update_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }
        msCameraPermission =
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        fab_updateAdd_photo.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                showDialog()
            }
        }


        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val liUpdateUser =
            mUserViewModel.getItemDetail(intent.getIntExtra(getString(R.string.id), 0))
        liUpdateUser.observe(this) {
            Update_First_Name.setText(it.fName)
            Update_Last_Name.setText(it.lName)
            Update_Emails.setText(it.email)
            Update_DOB.text = it.dob
            Update_Phone_Number.setText(it.number)
            Glide.with(applicationContext).load(it.image).circleCrop().into(ivUpdateProfile)
            msPhotoPath = it.image

            if (msPhotoPath == "") {
                Glide.with(applicationContext).load(R.drawable.person_icon).circleCrop()
                    .into(ivUpdateProfile)
                Log.d("Test", "Empty");
            }
        }

        val lSdf = SimpleDateFormat(getString(R.string.date_format))

        val lDate = lSdf.parse(intent.getStringExtra(getString(R.string.dob))!!)

//        val date = sdf.parse(updateIntent.observe(this){it.dob}.toString())!!

        val loMyCalender = Calendar.getInstance()
        loMyCalender.time = lDate!!

        val loDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

            loMyCalender.set(Calendar.YEAR, year)
            loMyCalender.set(Calendar.MONTH, month)
            loMyCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updatable(loMyCalender)
        }

        Update_DOB.setOnClickListener {
            val lDialog = DatePickerDialog(
                this,
                loDatePicker,
                loMyCalender.get(Calendar.YEAR),
                loMyCalender.get(Calendar.MONTH),
                loMyCalender.get(Calendar.DAY_OF_MONTH)
            )
            lDialog.datePicker.maxDate = loMyCalender.timeInMillis
            lDialog.show()
        }


        Update_User_Save.setOnClickListener {
            UpdateDataToDatabase()
        }

        Update_User_Cancel.setOnClickListener {
            finish()
        }
//        Update_Emails.setOnClickListener {
//            if (llUpdateEmail.isClickable) {
//                Update_Emails.error = "don't change"
//                return@setOnClickListener
//            }
//        }


    }


    private fun showDialog() {
        val lAlertDialog = AlertDialog.Builder(this)
        lAlertDialog.apply {
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
        msPhotoPath = loImage.absolutePath
        return loImage
    }

    private fun requestCameraPermission() {
        requestPermissions(msCameraPermission, (R.string.CAMERA_REQUEST))
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
                Glide.with(this).load(msPhotoPath).circleCrop().into(ivUpdateProfile)
            }
            1 -> if (resultCode == RESULT_OK) {
                val selectedImage: Uri? = imageReturnedIntent?.data
                msPhotoPath = selectedImage.toString()
                Glide.with(this).load(msPhotoPath).circleCrop().into(ivUpdateProfile)
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

    private fun UpdateDataToDatabase() {

        val id = intent.getIntExtra("id", 0)
        val fName = Update_First_Name.text.toString()
        val lName = Update_Last_Name.text.toString()
        val email = Update_Emails.text.toString()
        val number = Update_Phone_Number.text.toString()
        val dob = Update_DOB.text.toString()


        val date = Update_DOB.text.toString()
        val dateParts: List<String> = date.split("-")
        val year = dateParts[2]
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        miMyAge = currentYear - year.toInt()



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


        if (!TextUtils.isEmpty(dob)) {
            val user =
                UserEntity(
                    id,
                    msPhotoPath!!,
                    fName,
                    lName,
                    email,
                    dob,
                    miMyAge,
                    number,
                    Date()
                )

            GlobalScope.launch {
                mUserViewModel.updateUser(user)
            }

            val intent = Intent(this, MainActivity::class.java)

            val loading = LoadingDialog(this)
            loading.startLoading()
            val handler = Handler()
            handler.postDelayed({ loading.isDismiss() }, 2000)

            startActivity(intent)

            Toast.makeText(this, getString(R.string.success_msg), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.dob_check),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun updatable(myCalender: Calendar) {
        val loCurrentYear = Calendar.getInstance().get(Calendar.YEAR)
        val loBirthYear = myCalender.get(Calendar.YEAR)
        miMyAge = loCurrentYear - loBirthYear
        val lsMyFormat = getString(R.string.date_format)
        val loSdf = SimpleDateFormat(lsMyFormat, Locale.UK)
        Update_DOB.text = loSdf.format(myCalender.time)
    }
}
