package com.example.passwordmanager.data

class PasswordRepository (private val myDao: PasswordDao) {
    suspend fun insert(entity: Password) {
        myDao.insert(entity)
    }

    suspend fun update(entity: Password) {
        myDao.update(entity)
    }

    suspend fun delete(entity: Password) {
        myDao.delete(entity)
    }

    suspend fun getAll(): List<Password> {
        return myDao.getAllPasswords()
    }
}