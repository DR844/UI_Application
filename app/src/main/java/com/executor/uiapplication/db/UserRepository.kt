package com.executor.uiapplication.db

import androidx.lifecycle.LiveData

class UserRepository(private val userDAO: UserDAO) {

    val getAllUser: LiveData<List<UserEntity>> = userDAO.getAllUser()


    suspend fun insertUser(userEntity: UserEntity) {
        userDAO.insertUser(userEntity)
    }

    suspend fun updateUser(userEntity: UserEntity) {
        userDAO.updateUser(userEntity)
    }

    suspend fun deleteUser(userEntity: UserEntity) {
        userDAO.deleteUser(userEntity)
    }

    suspend fun isEmailExist(email: String): Int {
        return userDAO.isEmailExist(email)
    }

    fun getItemDetail(id: Int): LiveData<UserEntity> {
        return userDAO.getItemDetail(id)
    }

    suspend fun deleteByUserId(id: Int) {
        return userDAO.deleteByUserId(id)
    }
//    suspend fun getItemId(id: Int):Int {
//        return userDAO.deleteByUserId(id)
//    }
}