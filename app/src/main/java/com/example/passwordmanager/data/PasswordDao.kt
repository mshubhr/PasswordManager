package com.example.passwordmanager.data

import androidx.room.*

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: Password)

    @Update
    suspend fun update(password: Password)

    @Delete
    suspend fun delete(password: Password)

    @Query("SELECT * FROM passwords")
    suspend fun getAllPasswords(): List<Password>
}