package com.example.storedataandfiles.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storedataandfiles.User
import com.example.storedataandfiles.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDao: UserDao
): ViewModel() {
    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> = _userList

    fun insertUser(name: String, age: Int) {
        viewModelScope.launch {
            userDao.insert(User(name = name, age = age))
            loadUsers()
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _userList.value = userDao.getAll()
        }
    }
}