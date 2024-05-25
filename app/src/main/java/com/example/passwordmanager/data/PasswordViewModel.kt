package com.example.passwordmanager.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val repository: PasswordRepository,
) : ViewModel() {

    private val _passwords = MutableLiveData<List<Password>>()
    val passwords: LiveData<List<Password>> get() = _passwords

    init {
        loadPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            try {
                _passwords.value = repository.getAll()
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error getting data: ${e.message}")
            }
        }
    }

    fun insertPassword(password: Password) {
        viewModelScope.launch {
            repository.insert(password)
            loadPasswords()
        }
    }

    fun updatePassword(password: Password) {
        viewModelScope.launch {
            repository.update(password)
            loadPasswords()
        }
    }

    fun deletePassword(password: Password) {
        viewModelScope.launch {
            repository.delete(password)
            loadPasswords()
        }
    }
}