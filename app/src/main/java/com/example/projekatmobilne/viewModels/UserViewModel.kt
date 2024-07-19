package com.example.projekatmobilne.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekatmobilne.Repository.UserRepository
import com.example.projekatmobilne.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user


    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _user.value = userRepository.getUserProfile(userId)
        }
    }

    public fun addUserProfile(userId: String, user: User,onComplete: () -> Unit) {
        viewModelScope.launch {
            val success = userRepository.addUserProfile(userId, user)
            if (success) {
                onComplete()
            }
        }
    }
}