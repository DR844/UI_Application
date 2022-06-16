package com.executor.uiapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("select * from user_table")
    fun getAllUser(): LiveData<List<UserEntity>>

    @Query("SELECT count(email) FROM user_table WHERE email = :email")
    fun isEmailExist(email: String): Int

    @Query("SELECT * FROM user_table WHERE id = :uid")
    fun getItemDetail(uid: Int): LiveData<UserEntity>

    @Query("SELECT * FROM user_table WHERE id = :uid")
    fun getItemId(uid: Int):Int

    @Query("DELETE FROM user_table WHERE id = :userId")
    fun deleteByUserId(userId: Int)


}