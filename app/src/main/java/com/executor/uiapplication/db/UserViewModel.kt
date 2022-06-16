package com.executor.uiapplication.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    val getAllUser: LiveData<List<UserEntity>>
    private val repository: UserRepository

    init {
        val userDAO = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDAO)
        getAllUser = repository.getAllUser
    }

    suspend fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(userEntity)
        }
    }

    suspend fun updateUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(userEntity)
        }
    }

    suspend fun deleteUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(userEntity)
        }
    }

    suspend fun isEmailExist(email: String): Int {
        viewModelScope.launch(Dispatchers.IO) {
            repository.isEmailExist(email)
        }
        return email.toInt()
    }

     fun getItemDetail(id: Int): LiveData<UserEntity> {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getItemDetail(id)
        }
        return repository.getItemDetail(id)
    }

    suspend fun deleteByUserId(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteByUserId(id)
        }
    }

}