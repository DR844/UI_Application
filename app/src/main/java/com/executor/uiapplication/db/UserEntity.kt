package com.executor.uiapplication.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.math.ln

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val image: String,
    val fName: String,
    val lName: String,
    val email: String,
    val dob: String,
    val age: Int = 0,
    val number: String,
    val createDate: Date,

//    @PrimaryKey(autoGenerate = true)
//    var id: Int = 0
//
//    @ColumnInfo(name = "first_name")
//    var fName: String? = null
//
//    @ColumnInfo(name = "last_name")
//    var lName: String? = null
//
//    @ColumnInfo(name = "Email")
//    var emails: String? = null
//
//    @ColumnInfo(name = "phone_number")
//    var number: String? = null
//
//    @ColumnInfo(name = "DOB")
//    val dob: String? = null
//
//    @ColumnInfo(name = "Age")
//    val age: Int = 0
//
//    @ColumnInfo(name = "Image")
//    val image: String? = null
//
//    @ColumnInfo(name = "Create Date")
//    val createDate: Date? = null
//
//    fun getUid(): Int {
//        return id
//    }
//
//    fun getFirstName(): String? {
//        return fName
//    }
//
//    fun getLastName(): String? {
//        return lName
//    }
//
//    fun getPhoneNumber(): String? {
//        return number
//    }
//
//    fun getEmail(): String {
//        return emails!!
//    }
//
//    fun getAge(): String {
//        return age.toString()
//    }
//
//    fun getDOB(): String {
//        return dob!!
//    }
//
//    fun getDate(): String {
//        return createDate.toString()
//    }
//    fun Contacts(
//        uid: Int,
//        firstName: String,
//        lastName: String,
//        phoneNumber: String,
//        address: String,
//    ) {
//        uid = uid
//        firstName = firstName
//        lastName = lastName
//        phoneNumber = phoneNumber
//        address = address
//    }

)