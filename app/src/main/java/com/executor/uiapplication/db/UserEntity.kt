package com.executor.uiapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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
)