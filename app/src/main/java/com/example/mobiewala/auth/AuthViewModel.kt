package com.example.mobiewala.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiewala.database.AppDatabase
import com.example.mobiewala.database.UserEntity
import com.example.mobiewala.database.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if (user != null && user.password == password) {
                _loginResult.value = LoginResult.Success(user)
            } else {
                _loginResult.value = LoginResult.Error("Invalid credentials")
            }
        }
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            val existingUser = userRepository.getUserByEmail(email)
            if (existingUser == null) {
                val user = UserEntity(name = name, email = email, password = password)
                userRepository.insert(user)
                _loginResult.value = LoginResult.Success(user) // Auto-login after signup
            } else {
                _loginResult.value = LoginResult.Error("User with this email already exists")
            }
        }
    }
}

sealed class LoginResult {
    data class Success(val user: UserEntity) : LoginResult()
    data class Error(val message: String) : LoginResult()
}